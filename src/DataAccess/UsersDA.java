package DataAccess;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsersDA {


    public static boolean userExists(String username, String pswd) throws SQLException {
        username = username.trim();
        Connection connection = new ConnectionManager().getConnection();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM Users WHERE LOWER(Username) = '" + username.toLowerCase() + "' AND pswd = md5('" + pswd + "')";
        ResultSet resultSet = statement.executeQuery(query);
        return (resultSet.next());
    }

}
