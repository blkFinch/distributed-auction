package Test;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

public class DBTest {


    public static void main(String[] args) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src" + "/Test/DBtest.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
