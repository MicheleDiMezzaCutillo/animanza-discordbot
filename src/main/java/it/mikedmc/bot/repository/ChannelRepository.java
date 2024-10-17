package it.mikedmc.bot.repository;

import it.mikedmc.bot.utils.ManagerDatabase;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChannelRepository {

    public static List<TextChannel> findAll(ReadyEvent event) {
        List<TextChannel> channels = new ArrayList<>();

        String query = "SELECT server_id, channel_id FROM channel";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String idServer = rs.getString("server_id");
                        String idCanale = rs.getString("channel_id");

                        try {
                            channels.add(event.getJDA().getGuildById(idServer).getTextChannelById(idCanale));
                        } catch (NullPointerException e) {
                            // non trova un server (forse perchè è stato eliminato)
                            ChannelRepository.delete(idServer);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return channels;
    }

    public static TextChannel findByGuildId(SlashCommandInteractionEvent event, String guildId) {
        String query = "SELECT server_id, channel_id FROM channel WHERE server_id = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, guildId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String idServer = rs.getString("server_id");
                        String idCanale = rs.getString("channel_id");

                        return event.getJDA().getGuildById(idServer).getTextChannelById(idCanale);
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

    public static boolean delete(String serverId) {
        String query = "DELETE FROM channel WHERE server_id = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                // Imposta i parametri della query
                pstmt.setString(1, serverId);

                // Esegui la cancellazione
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0; // Ritorna true se è stato cancellato almeno un record

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean save(String serverId, String channelId, String serverName, String channelName) {
        String query = "INSERT INTO channel (server_id, channel_id, server_name, channel_name) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE channel_id = VALUES(channel_id), server_name = VALUES(server_name), channel_name = VALUES(channel_name)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(ManagerDatabase.jdbcUrl, ManagerDatabase.jdbcUser, ManagerDatabase.jdbcPassword);
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                // Imposta i parametri della query
                pstmt.setString(1, serverId);
                pstmt.setString(2, channelId);
                pstmt.setString(3, serverName);
                pstmt.setString(4, channelName);

                // Esegui l'inserimento
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0; // Ritorna true se è stato inserito almeno un record

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
