import io.github.cdimascio.dotenv.Dotenv;
import io.github.dankoller.Main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MealPlannerUnitTest {

    // Test for adding a complete recipe
    @Test
    public void testAddCompleteRecipe() {
        String[] commandsForAddingRecipe = {
                "add",
                "breakfast",
                "testmeal",
                "bits, bytes, arrays"
        };
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(String.join("\n", commandsForAddingRecipe).getBytes()));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        PrintStream stdout = System.out;
        System.setOut(ps);
        executeMain();
        System.setIn(stdin);
        System.setOut(stdout);
        String output = byteArrayOutputStream.toString();
        output = output.substring(0, output.length() - 57); // Remove last line from output
        String expected = """
                What would you like to do (add, show, plan, save, exit)?
                Which meal do you want to add (breakfast, lunch, dinner)?
                Input the meal's name:
                Input the ingredients:
                The meal has been added!
                """;
        assertEquals(expected, output);
    }

    // Test for showing the meals of a category
    @Test
    public void testShowMeal() {
        String[] commandsForShowingMeal = {
                "show",
                "breakfast"
        };
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(String.join("\n", commandsForShowingMeal).getBytes()));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        PrintStream stdout = System.out;
        System.setOut(ps);
        executeMain();
        System.setIn(stdin);
        System.setOut(stdout);
        String output = byteArrayOutputStream.toString();
        output = output.substring(0, output.length() - 57);
        String expected = """
                What would you like to do (add, show, plan, save, exit)?
                Which category do you want to print (breakfast, lunch, dinner)?
                Category: breakfast
                """;
        assertTrue(output.contains(expected));
    }

    // Clean the database after each test
    @AfterEach
    public void cleanDatabase() {
        String SQL_CLEAN_MEALS_TABLE = "DELETE FROM meals WHERE meal LIKE '%testmeal%'";
        String SQL_CLEAN_INGREDIENTS_TABLE = "DELETE FROM ingredients WHERE ingredient " +
                "LIKE '%bits%' OR ingredient LIKE '%bytes%' OR ingredient LIKE '%arrays%'";
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(true);
            PreparedStatement statement = connection.prepareStatement(SQL_CLEAN_MEALS_TABLE);
            statement.executeUpdate();
            statement = connection.prepareStatement(SQL_CLEAN_INGREDIENTS_TABLE);
            statement.executeUpdate();
            statement = connection.prepareStatement("SELECT * FROM meals WHERE meal LIKE '%testmeal%'");
            ResultSet resultSet = statement.executeQuery();
            assertEquals(0, resultSet.getRow());
            statement = connection.prepareStatement("SELECT * FROM ingredients WHERE ingredient " +
                    "LIKE '%bits%' OR ingredient LIKE '%bytes%' OR ingredient LIKE '%arrays%'");
            resultSet = statement.executeQuery();
            assertEquals(0, resultSet.getRow());
        } catch (SQLException e) {
            System.err.println("Could not clean the database, please do it manually. Error: " + e.getMessage());
        }
    }

    /**
     * Executes the main method of the Main class.
     */
    private void executeMain() {
        try {
            Main.main(new String[0]);
        } catch (Exception ignored) {
        }
    }

    /**
     * Establishes a connection to the database.
     *
     * @return The connection to the database.
     * @throws SQLException If the connection could not be established.
     */
    private Connection getConnection() throws SQLException {
        String path = System.getProperty("user.dir") +
                File.separator + "src" +
                File.separator + "main" +
                File.separator + "resources";
        Dotenv dotenv = Dotenv.configure().directory(path).load();
        String DB_URL = dotenv.get("DB_URL");
        String USER = dotenv.get("USER");
        String PASS = dotenv.get("PASS");

        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
