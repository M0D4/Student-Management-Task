package DataAccess;

import entities.Student;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class StudentDA {

    public static Student convertIntoStudent(ResultSet resultSet) throws SQLException, DataFormatException {
        String name = resultSet.getString(1);
        String department = resultSet.getString(2);
        String nationalID = resultSet.getString(3);
        String mobileNumber = resultSet.getString(4);

        return new Student(name, department, nationalID, mobileNumber);
    }

    public static ArrayList<Student> getAll() throws SQLException, DataFormatException {
        Connection connection = new ConnectionManager().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM student ORDER BY Name");

        ArrayList<Student> students = new ArrayList<>();
        for (int i = 0; resultSet.next(); i++) {
            Student student = convertIntoStudent(resultSet);
            student.setNo(i + 1);
            students.add(student);
        }
        return students;
    }

    public static ArrayList<Student> search(String s) throws SQLException, DataFormatException {
        s = s.trim();
        Connection connection = new ConnectionManager().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM student WHERE LOWER(Name) LIKE '" + s.toLowerCase() + "%' " +
                "OR NationalID LIKE '" + s + "%' ORDER BY Name");

        ArrayList<Student> students = new ArrayList<>();
        for (int i = 0; resultSet.next(); i++) {
            Student student = convertIntoStudent(resultSet);
            student.setNo(i + 1);
            students.add(student);
        }

        return students;
    }

    public static void insert(String name, String department, String nationalID, String mobileNumber) throws DataFormatException, SQLException {
        Student student = new Student(name, department, nationalID, mobileNumber);
        Connection connection = new ConnectionManager().getConnection();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO Student VALUES('" + student.getName() + "', '" + department + "', '"
                + student.getNationalID() + "', '" + student.getMobileNumber() + "')";

        statement.execute(query);
    }

    public static void delete(String nationalID) throws SQLException {
        nationalID = nationalID.trim();
        Connection connection = new ConnectionManager().getConnection();
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM student WHERE NationalID LIKE '" + nationalID + "%'");
    }

    public static void update(String oldNationalID, String name, String department, String nationalID, String mobileNumber) throws DataFormatException, SQLException {
        Student student = new Student(name, department, nationalID, mobileNumber);
        Connection connection = new ConnectionManager().getConnection();
        Statement statement = connection.createStatement();
        String query = "UPDATE Student SET "
                     + "Name = '" + student.getName() + "', "
                     + "Department = '" + department + "', "
                     + "NationalID = '" + student.getNationalID() + "', "
                     + "MobileNumber = '" + student.getMobileNumber() + "' "
                     + "WHERE NationalID = '" + oldNationalID + "'";
        statement.execute(query);
    }

}
