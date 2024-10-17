package it.mikedmc.bot.repository;

import it.mikedmc.bot.model.Anime;
import it.mikedmc.bot.model.LessAnime;
import it.mikedmc.bot.utils.ManagerDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnimeRepository {

    public static LessAnime checkAnime (String name) {

        String query = "SELECT episode, finished, episodes FROM anime WHERE name = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setString(1, name);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        float episode = rs.getFloat("episode");
                        float episodes = rs.getFloat("episodes");
                        boolean finished = rs.getBoolean("finished");

                        return new LessAnime(name,episode,episodes,finished);
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }



    public static void updateAnime(String name, float lastEpisode, boolean finished) {
        String query = "UPDATE anime SET episode = ? WHERE name = ?";
        if (finished) {
            query = "UPDATE anime SET episode = ?, finished = true WHERE name = ?";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setFloat(1, lastEpisode);
            pstmt.setString(2, name);

            int affectedRows = pstmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Anime> findAnimesInProgressLimited(int limit) {
        List<Anime> animeList = new ArrayList<>();
        String query = "SELECT name, linkImage, linkAnime, episode, episodes, description, " +
                "releaseDate, studio, genres, malLink, aniListLink, mangaWorldLink, releaseDateFormatted " +
                "FROM anime " +
                "WHERE episode < episodes AND episodes != -1 " +
                "ORDER BY releaseDateFormatted DESC";
        if (limit != -1) {
            query += " LIMIT ?";
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, limit);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                // Otteniamo i vari dati.
                String name = resultSet.getString("name");
                String linkImage = resultSet.getString("linkImage");
                String linkAnime = resultSet.getString("linkAnime");
                float episode = resultSet.getFloat("episode");
                float episodes = resultSet.getFloat("episodes");
                String description = resultSet.getString("description");
                String releaseDate = resultSet.getString("releaseDate");
                LocalDate releaseDateFormatted = resultSet.getObject("releaseDateFormatted", LocalDate.class);
                String studio = resultSet.getString("studio");
                String genres = resultSet.getString("genres");
                String malLink = resultSet.getString("malLink");
                String aniListLink = resultSet.getString("aniListLink");
                String mangaWorldLink = resultSet.getString("mangaWorldLink");

                // Creiamo un istanza di Anime con i vari dati.
                Anime anime = new Anime(name, linkImage, linkAnime, episode, episodes, description, releaseDate, releaseDateFormatted, studio, genres, malLink, aniListLink, mangaWorldLink);

                animeList.add(anime);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return animeList;
    }


    public static boolean save(Anime anime) {
// Query SQL per inserire i dati nella tabella anime
        String query = "INSERT INTO anime (name, linkImage, linkAnime, episode, episodes, finished, description, " +
                "releaseDate, releaseDateFormatted, studio, genres, malLink, " +
                "aniListLink, mangaWorldLink) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Carico il driver JDBC di MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connessione al database
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                // Impostazione dei parametri della query
                pstmt.setString(1, anime.getName());
                pstmt.setString(2, anime.getLinkImage());
                pstmt.setString(3, anime.getLinkAnime());
                pstmt.setFloat(4, anime.getEpisode());
                pstmt.setFloat(5, anime.getEpisodes());
                pstmt.setBoolean(6, anime.isFinished());
                pstmt.setString(7, anime.getDescription());
                pstmt.setString(8, anime.getReleaseDate());
                pstmt.setDate(9, java.sql.Date.valueOf(anime.getReleaseDateFormatted())); // Converto LocalDate in java.sql.Date
                pstmt.setString(10, anime.getStudio());
                pstmt.setString(11, anime.getGenres());
                pstmt.setString(12, anime.getMalLink());
                pstmt.setString(13, anime.getAniListLink());
                pstmt.setString(14, anime.getMangaWorldLink());

                // Esecuzione della query
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    return true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static void sendToChannels(Anime anime, List<TextChannel> channels) {
        MessageEmbed embed = embedAnime(anime.getName(),anime.getLinkImage(),anime.getLinkAnime(),anime.getReleaseDate(),anime.getStudio(),anime.getGenres(),anime.getEpisodes(),anime.getDescription());

        try {
              // ha solo 1 episodio, quindi una sola puntata.
            if (anime.getEpisodes() == 1) {
                for (TextChannel tc : channels) {
                    // mettiamo solo i bottoni dei link.
                    tc.sendMessageEmbeds(embed).setComponents(anime.getBottoniExtra()).queue();
                }
            } else {
                List<Button> bottoniBase = new ArrayList<>();
                bottoniBase.add(Button.success("add","Tienimi aggiornato"));
                bottoniBase.add(Button.danger("rep","Report"));

                // non sappiamo quanti episodi sono in tutto.
                if (anime.getEpisodes() == -1) {
                    for (TextChannel tc : channels) {
                        // non avrà il bottone che avvisa quando finisce l'anime.
                        tc.sendMessageEmbeds(embed).setComponents(anime.getBottoniExtra()).addActionRow(bottoniBase).queue();
                    }
                } else {
                    // aggiungiamo il bottone per essere avvisato quando finisce l'anime.
                    bottoniBase.add(Button.primary("last","Avvisami quando finisce"));
                    for (TextChannel tc : channels) {
                        tc.sendMessageEmbeds(embed).setComponents(anime.getBottoniExtra()).addActionRow(bottoniBase).queue();
                    }
                }

            }


        } catch (NullPointerException e) {
        }

    }

    public static void sendToChannel(Anime anime, TextChannel channel) throws InsufficientPermissionException{
        MessageEmbed embed = embedAnime(anime.getName(),anime.getLinkImage(),anime.getLinkAnime(),anime.getReleaseDate(),anime.getStudio(),anime.getGenres(),anime.getEpisodes(),anime.getDescription());

        try {
            // ha solo 1 episodio, quindi una sola puntata.
            if (anime.getEpisodes() == 1) {
                // mettiamo solo i bottoni dei link.
                channel.sendMessageEmbeds(embed).setComponents(anime.getBottoniExtra()).queue();
            } else {
                List<Button> bottoniBase = new ArrayList<>();
                bottoniBase.add(Button.success("add","Tienimi aggiornato"));
                bottoniBase.add(Button.danger("rep","Report"));

                // non sappiamo quanti episodi sono in tutto.
                if (anime.getEpisodes() == -1) {
                    // non avrà il bottone che avvisa quando finisce l'anime.
                    channel.sendMessageEmbeds(embed).setComponents(anime.getBottoniExtra()).addActionRow(bottoniBase).queue();
                } else {
                    // aggiungiamo il bottone per essere avvisato quando finisce l'anime.
                    bottoniBase.add(Button.primary("last","Avvisami quando finisce"));

                    channel.sendMessageEmbeds(embed).setComponents(anime.getBottoniExtra()).addActionRow(bottoniBase).queue();
                }

            }


        } catch (NullPointerException e) {
        }

    }

    private static MessageEmbed embedAnime(String nome, String linkImmagine, String linkAnime, String dataUscita, String studio, String generi, float episodi, String descrizione) {

        String episodiString = episodi+"";
        if (episodi == -1) {
            episodiString = "Indefinito";
        } else if (episodi%1==0) {
            episodiString = (int) episodi + "";
        }
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(nome,linkAnime)
                .setDescription("**Episodi** : `" + episodiString + "`\n" +
                        "**Data d'uscita** : `" + dataUscita + "`\n" +
                        "**Studio** : `" + studio + "`\n" +
                        "**Tag** : `" + generi + "`\n" +
                        "**Descrizione** : `" + descrizione + "`")
                .setImage(linkImmagine)
                .setFooter("ᴘᴏᴡᴇʀᴇᴅ ʙʏ ᴍɪᴋᴇᴅᴍᴄ","https://cdn.discordapp.com/avatars/183542841732104192/94fa2892db53fb5ec0ee2a8f36b39582?size=1024");
        return embed.build();
    }

}
