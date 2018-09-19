package su.logix.patreg.connection;

import org.jetbrains.annotations.Contract;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {
    private static DBConnection ourInstance = new DBConnection();
    private Connection connection;
    private String connectionString;
    private String user;
    private String password;

    private DBConnection() {
    }

    @Contract(pure = true)
    public static DBConnection getInstance() {
        return ourInstance;
    }

    public void setParams(String connectionString, String user, String password) {
        this.connectionString = connectionString;
        this.user = user;
        this.password = password;
    }

    public void setParams(String connectionString) {
        setParams(connectionString, "", "");
    }

    public void connect() throws SQLException {
        if (connection == null || !isConnected()) {
            connection = DriverManager.getConnection(connectionString, user, password);
        }
    }

    public void disconnect() throws SQLException {
        if (isConnected()) {
            connection.close();
        }
    }

    public Statement getStatement() throws SQLException {
        return connection.createStatement();
    }

    public boolean isConnected() throws SQLException {
        return connection.isValid(5000);
    }

    public class QueryStorage {
        List<String> storage = new ArrayList<>();

        public void add(String query) {
            storage.add(query);
        }

        public void send() throws SQLException {
            DBConnection dbConnection = DBConnection.getInstance();
            try (Statement stmt = dbConnection.getStatement()) {
                for (String query : storage) {
                    stmt.executeQuery(query);
                }
            }
        }

        public void clear() {
            storage.clear();
        }
    }
}

