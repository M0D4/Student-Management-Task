package DataAccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class DepartmentDA {
    public static ArrayList<String> getAll() throws SQLException {
        Connection connection = new ConnectionManager().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Name FROM Department ORDER BY Name");

        ArrayList<String> departments = new ArrayList<>();
        for (int i = 0; resultSet.next(); i++) {
            String department = resultSet.getString(1);
            departments.add(department);
        }
        return departments;
    }

}
