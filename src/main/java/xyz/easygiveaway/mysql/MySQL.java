package xyz.easygiveaway.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.easygiveaway.main.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {

    private final Main main;
    private static Connection conn;

    private static final Logger LOGGER = LoggerFactory.getLogger(MySQL.class);

    private final SQLManager sqlManager;

    public MySQL(Main main) {
        this.main = main;
        this.sqlManager = new SQLManager(this);
    }


    public void connect(String host, int port, String database, String username, String password) {
        if (!isConnected()) {
            try {
                // conn = DriverManager.getConnection("jdbc:mysql://host:port/database", "username", "password");
                conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                // this.sqlManager.createDatabase();
                this.sqlManager.createMainTable();
                this.sqlManager.createGiveawayTable();
                this.sqlManager.createInviteTable();
                LOGGER.info("Connected to MySQL!");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                conn.close();
                conn = null;
                LOGGER.info("Disconnected from MySQL!");
            } catch (SQLException e) {
                LOGGER.warn("Disconnecting from MySQL failed!");
            }
        }
    }

    public static boolean isConnected() {
        return conn != null;
    }

    public static void update(String qry) {
        if (isConnected()) {
            try {
                conn.createStatement().executeUpdate(qry);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static ResultSet getResult(String qry) {
        if (isConnected()) {
            try {
                return conn.createStatement().executeQuery(qry);
            } catch (SQLException e) {
                LOGGER.warn("Getting Result from MySQL failed");
            }
        }
        return null;
    }

}
