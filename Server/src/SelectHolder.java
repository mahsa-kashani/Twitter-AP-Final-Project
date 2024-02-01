import java.sql.*;

public class SelectHolder {
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet table;

    public SelectHolder(Connection connection, PreparedStatement statement, ResultSet table) {
        this.connection = connection;
        this.statement = statement;
        this.table = table;
    }

    public Connection getConnection() {
        return connection;
    }

    public PreparedStatement getStatement() {
        return statement;
    }

    public ResultSet getTable() {
        return table;
    }
}
