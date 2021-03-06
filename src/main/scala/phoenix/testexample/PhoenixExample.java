package phoenix.testexample;

import java.sql.*;

/**
 * Created by shaines on 12/20/15.
 */
public class PhoenixExample {

    public static void main(String[] args) {
        // Create variables
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            // Connect to the database
            connection = DriverManager.getConnection("jdbc:phoenix:sandbox-hdp.hortonworks.com:2181:/hbase-unsecure");
            // Create a JDBC statement
            statement = connection.createStatement();

            // Execute our statements
            statement.executeUpdate("create table if not exists javatest (mykey integer not null primary key, mycolumn varchar)");
            statement.executeUpdate("upsert into javatest values (2,'Hello')");
            statement.executeUpdate("upsert into proxy.request values (111,'2002-05-30 09:30:10.0')");
            connection.commit();

            // Query for table
            ps = connection.prepareStatement("select * from javatest");
            rs = ps.executeQuery();
            System.out.println("Table Values");
            while(rs.next()) {
                Integer myKey = rs.getInt(1);
                String myColumn = rs.getString(2);
                System.out.println("\tRow: " + myKey + " = " + myColumn);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        finally {
            if(ps != null) {
                try {
                    ps.close();
                }
                catch(Exception e) {}
            }
            if(rs != null) {
                try {
                    rs.close();
                }
                catch(Exception e) {}
            }
            if(statement != null) {
                try {
                    statement.close();
                }
                catch(Exception e) {}
            }
            if(connection != null) {
                try {
                    connection.close();
                }
                catch(Exception e) {}
            }
        }


    }
}
