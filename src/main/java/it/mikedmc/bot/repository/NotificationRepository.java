package it.mikedmc.bot.repository;

import it.mikedmc.bot.model.Anime;
import it.mikedmc.bot.model.Notification;
import it.mikedmc.bot.utils.ManagerDatabase;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    public static void sentNotificationToAllUsers(String name, MessageEmbed embed, ReadyEvent event) {
        String query = "SELECT user_id FROM notification WHERE anime_name = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setString(1, name);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String id = rs.getString("id_utente");
                        event.getJDA().retrieveUserById(id).queue(user -> {
                            user.openPrivateChannel().queue(privateChannel -> {
                                privateChannel.sendMessageEmbeds(embed).setActionRow(
                                        Button.danger("del","Elimina questa notifica")
                                ).queue();
                            });
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Notification> getAnimesByUserIdWithOffsetAndLimit (String userId, int offset, int limit) {
        List<Notification> result = new ArrayList<>();

        String query = "SELECT a.*, n.* FROM anime a JOIN notification n ON a.name = n.anime_name WHERE n.user_id = ? LIMIT ? OFFSET ?";


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setString(1, userId);
                pstmt.setInt(2, limit);
                pstmt.setInt(3, offset);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Anime anime = new Anime();
                        anime.setName(rs.getString("name"));
                        anime.setLinkImage(rs.getString("linkImage"));
                        anime.setLinkAnime(rs.getString("linkAnime"));
                        anime.setEpisode(rs.getFloat("episode"));
                        anime.setEpisodes(rs.getFloat("episodes"));
                        anime.setFinished(rs.getBoolean("finished"));

                        anime.setDescription(anime.getDescription());
                        anime.setReleaseDate(rs.getString("releaseDate"));
                        anime.setStudio(rs.getString("studio"));
                        anime.setGenres(rs.getString("genres"));
                        anime.setMalLink(rs.getString("malLink"));
                        anime.setAniListLink(rs.getString("aniListLink"));
                        anime.setMangaWorldLink(rs.getString("mangaWorldLink"));

                        Notification notification = new Notification();
                        notification.setAnime(anime);
                        notification.setUser_id(userId);
                        notification.setId(rs.getFloat("id"));
                        notification.setLast(rs.getBoolean("last"));

                        result.add(notification);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static int countByUserId(String userId) {
        String query = "SELECT COUNT(*) FROM notification WHERE user_id = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setString(1, userId);  // Sostituisci 'userId' con il valore effettivo dell'utente

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public static void sentNotificationToUsers(String name, MessageEmbed embed, ReadyEvent event) {
        String query = "SELECT user_id FROM notification WHERE anime_name = ? AND last = false";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setString(1, name);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String id = rs.getString("user_id");
                        event.getJDA().retrieveUserById(id).queue(user -> {
                            user.openPrivateChannel().queue(privateChannel -> {
                                privateChannel.sendMessageEmbeds(embed).setActionRow(
                                        Button.danger("del","Elimina questa notifica")
                                ).queue();
                            });
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteNotifications(String name) {
        String query = "DELETE FROM notification WHERE anime_name = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {


                pstmt.setString(1, name);

                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isAlreadyRegistered(String userId, String name) {
        String query = "SELECT COUNT(*) FROM notification WHERE user_id = ? AND anime_name = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setString(1, userId);
                pstmt.setString(2, name);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return 1 == rs.getInt(1);

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean save(String userId, String name, boolean b) {
        String query = "INSERT INTO notification (anime_name, user_id, last) VALUES (?, ?, ?) ";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                // Imposta i parametri della query
                pstmt.setString(1, name);
                pstmt.setString(2, userId);
                pstmt.setBoolean(3, b);

                // Esegui l'inserimento
                return pstmt.executeUpdate() > 0; // Ritorna true se è stato inserito almeno un record

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteById(int id) {
        String query = "DELETE FROM notification WHERE id = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {


                pstmt.setFloat(1, id);

                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
