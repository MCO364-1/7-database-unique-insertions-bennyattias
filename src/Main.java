public class Main {
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
            ResultSet resultSet = statement.getResultSet();

            firstName = JOptionPane.showInputDialog("Enter your first name");
            lastName = JOptionPane.showInputDialog("Enter your last name");

            while (resultSet.next()) {
                if (resultSet.getString("FirstName").equals(firstName) && resultSet.getString("LastName").equals(lastName)) {
                    JOptionPane.showMessageDialog(null, "Your name is already in use.");
                    showResultSet(resultSet);
                    System.out.println("There are " + getEntryAmount(statement.getResultSet()) + " entries.");
                    getInitialCount(initialCount, statement.getResultSet());
                    System.exit(0);
                }
            }

            String insertSql = "INSERT INTO people (FirstName, LastName) VALUES ('" + firstName + "', '" + lastName + "')";
            statement.executeUpdate(insertSql);

            showResultSet(statement.getResultSet());
            System.out.println("There are " + getEntryAmount(statement.getResultSet()) + " entries.");
            getInitialCount(initialCount, statement.getResultSet());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void showResultSet(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            System.out.println("Result set: \n" + resultSet.getString("FirstName") + " "
                    + resultSet.getString("LastName"));
        }
    }

    public static int getEntryAmount(ResultSet resultSet) throws SQLException {
        int counter = 0;
        while (resultSet.next()) {
            counter++;
        }
        return counter;
    }

    public static void getInitialCount(Map<Character, Integer> map, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            char firstNameInitial = resultSet.getString("FirstName").charAt(0);
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

}