import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    // Connect to your database
    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        String endpoint = env.get("db_connection");
        String firstName = "";
        String lastName = "";
        Map<Character, Integer> initialCount = new HashMap<>();

        String connectionUrl =
                "jdbc:sqlserver://" + endpoint + ";"
                        + "database=Attias;"
                        + "user=MCON364;"
                        + "password=Pesach2025;"
                        + "encrypt=true;"
                        + "trustServerCertificate=true;"
                        + "loginTimeout=30;";


        // Execute INSERT query
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement())
        {

            statement.executeQuery("SELECT * FROM People");
            ResultSet resultSet = statement.getResultSet();

            String fileName = "databaseData.txt";
            recordData(resultSet, fileName);

            firstName = JOptionPane.showInputDialog("Enter your first name");
            lastName = JOptionPane.showInputDialog("Enter your last name");

            // Read from the file
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                        if (line.equals(firstName + " " + lastName)) {
                            JOptionPane.showMessageDialog(null, "Your name is already in use.");
                            displayAllResults(initialCount, fileName);
                            System.exit(0);
                        }
                }
            } catch (IOException e) {
            }


            String insertSql = "INSERT INTO people (FirstName, LastName) VALUES ('" + firstName + "', '" + lastName + "')";
            statement.executeUpdate(insertSql);
            resultSet = statement.executeQuery("SELECT * FROM People");
            recordData(resultSet, fileName);

            displayAllResults(initialCount, fileName);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void showResultSet(String fileName) {

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            System.out.println("Result set: ");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                }
            }
        catch (IOException e) {
        }

    }

    public static int getEntryAmount(String fileName) {
        int counter = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            while (reader.readLine() != null) {
                counter++;
            }
        }
        catch (IOException e) {
        }
        return counter;
    }

    public static void getInitialCount(Map<Character, Integer> map, String fileName) throws SQLException {

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                char firstNameInitial = line.charAt(0);
                if (!map.containsKey(firstNameInitial)) {
                    map.put(firstNameInitial, 1);
                } else {
                    map.put(firstNameInitial, map.get(firstNameInitial) + 1);
                }
            }

            System.out.println("Initial count: ");
            for (Map.Entry entry : map.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
        catch (IOException e) {
        }
    }

    public static void recordData(ResultSet resultSet, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            while (resultSet.next()) {
                writer.write(resultSet.getString(1) + " " + resultSet.getString(2));
                writer.newLine();
            }
        } catch (IOException | SQLException e) {
        }
    }

    public static void displayAllResults(Map<Character, Integer> initialCount, String fileName) throws SQLException {
        showResultSet(fileName);
        System.out.println("There are " + getEntryAmount(fileName) + " entries.");
        getInitialCount(initialCount, fileName);
    }

}