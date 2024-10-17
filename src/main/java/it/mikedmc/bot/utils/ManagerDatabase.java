package it.mikedmc.bot.utils;

import it.mikedmc.bot.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ManagerDatabase {


    public static final String jdbcUrl = Config.animanzaJdbcUrl;
    public static final String jdbcUser = Config.mikedmcJdbcUser;
    public static final String jdbcPassword = Config.mikedmcJdbcPassword;
    private static final String driver = Config.mikedmcJdbcDriver;
    public static boolean createTableIfNotExistsAnime() {

        String sql = "CREATE TABLE IF NOT EXISTS anime (" +
                "    name VARCHAR(255) PRIMARY KEY NOT NULL," +
                "    linkImage VARCHAR(255) NOT NULL," +
                "    linkAnime VARCHAR(255) NOT NULL," +
                "    episode FLOAT NOT NULL," +
                "    episodes FLOAT NOT NULL," +
                "    finished DOUBLE NOT NULL," +
                "    description TEXT NOT NULL," +
                "    releaseDate VARCHAR(50) NOT NULL," +
                "    releaseDateFormatted DATE NOT NULL," +
                "    studio VARCHAR(255) NOT NULL," +
                "    genres VARCHAR(255) NOT NULL," +
                "    malLink VARCHAR(255)," +
                "    aniListLink VARCHAR(255)," +
                "    mangaWorldLink VARCHAR(255)" +
                ")";

        try {
            Class.forName(driver);

            try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
                 Statement statement = connection.createStatement()) {

                statement.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                // Gestisci l'eccezione in modo appropriato
                return false;
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static boolean createTableIfNotExistsNotification() {

        String query = "CREATE TABLE IF NOT EXISTS notification ("
                + "id INT NOT NULL AUTO_INCREMENT, "
                + "anime_name VARCHAR(255) NOT NULL, "
                + "user_id VARCHAR(255) NOT NULL, "
                + "last BOOLEAN NOT NULL, "
                + "PRIMARY KEY (id), "
                + "FOREIGN KEY (anime_name) REFERENCES anime(name)"
                + ");";

        try {
            Class.forName(driver);

            try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
                 Statement statement = connection.createStatement()) {

                statement.execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
                // Gestisci l'eccezione in modo appropriato
                return false;
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


    public static boolean createTableIfNotExistsChannel() {

        String sql = "CREATE TABLE IF NOT EXISTS channel ("
                + "server_id BIGINT(18) NOT NULL, "
                + "channel_id BIGINT(19) NOT NULL, "
                + "server_name VARCHAR(255) NOT NULL, "
                + "channel_name VARCHAR(255) NOT NULL, "
                + "PRIMARY KEY (server_id)"
                + ");";

        try {
            Class.forName(driver);

            try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
                 Statement statement = connection.createStatement()) {

                statement.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                // Gestisci l'eccezione in modo appropriato
                return false;
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
