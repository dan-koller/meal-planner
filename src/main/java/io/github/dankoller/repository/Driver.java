package io.github.dankoller.repository;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.dankoller.entity.Meal;
import io.github.dankoller.entity.Plan;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Driver implements MealRepository {
    // SQL statements
    private static final String SQL_ADD_MEAL = "INSERT INTO meals (meal_id, meal, category) VALUES (?, ?, ?)";
    private static final String SQL_ADD_INGREDIENT = "INSERT INTO ingredients (ingredient, meal_id) VALUES (?, ?)";
    private static final String SQL_ADD_PLAN = "INSERT INTO plan (day, breakfast, lunch, dinner) VALUES (?, ?, ?, ?)";
    private static final String SQL_CLEAR_PLAN = "DELETE FROM plan";
    private static final String SQL_GET_PLAN = "SELECT * FROM plan";
    private static final String SQL_GET_MEALS_FROM_PLAN = "SELECT breakfast, lunch, dinner FROM plan";

    // Database credentials
    private static String DB_URL;
    private static String USER;
    private static String PASS;

    /**
     * The constructor of the database driver will load the database credentials from the .env file and create the
     * database if it doesn't exist yet.
     *
     * @throws SQLException If the database connection fails
     */
    public Driver() throws SQLException {
        loadConfig();
        Connection connection = getConnection();
        connection.setAutoCommit(true);
        String[] tables = {"meals", "ingredients", "plan"};
        for (String table : tables) {
            if (!isTableExist(connection, table)) {
                switch (table) {
                    case "meals" -> createMealTable();
                    case "ingredients" -> createIngredientTable();
                    case "plan" -> createPlanTable();
                }
            }
        }
    }

    /**
     * Load the database credentials from the .env file using the dotenv library.
     */
    private void loadConfig() {
        String path = System.getProperty("user.dir") +
                File.separator + "src" +
                File.separator + "main" +
                File.separator + "resources";
        Dotenv dotenv = Dotenv.configure().directory(path).load();
        DB_URL = dotenv.get("DB_URL");
        USER = dotenv.get("USER");
        PASS = dotenv.get("PASS");
    }

    /**
     * This helper method is used to establish a connection to the database.
     *
     * @return The database connection
     * @throws SQLException If the database connection fails
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    /**
     * This helper method is used by the constructor to check if a table exists in the database.
     *
     * @param connection The database connection
     * @param tableName  The name of the table
     * @return True if the table exists, false otherwise
     * @throws SQLException If the database connection fails
     */
    private boolean isTableExist(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, tableName, null);
        return resultSet.next();
    }

    /**
     * This helper method is used by the constructor to create the meals table in the database.
     *
     * @throws SQLException If the database connection fails
     */
    private void createMealTable() throws SQLException {
        Connection connection = getConnection();
        connection.setAutoCommit(true);
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE meals (" +
                "meal_id INTEGER," +
                "meal VARCHAR(1024) NOT NULL," +
                "category VARCHAR(1024) NOT NULL" +
                ")");
        statement.close();
        connection.close();
    }

    /**
     * This helper method is used by the constructor to create the ingredients table in the database.
     *
     * @throws SQLException If the database connection fails
     */
    private void createIngredientTable() throws SQLException {
        Connection connection = getConnection();
        connection.setAutoCommit(true);
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE ingredients (" +
                "ingredient_id INTEGER," +
                "ingredient VARCHAR(1024) NOT NULL," +
                "meal_id INTEGER" +
                ")");
        statement.close();
        connection.close();
    }

    /**
     * This helper method is used by the constructor to create the plan table in the database.
     *
     * @throws SQLException If the database connection fails
     */
    private void createPlanTable() throws SQLException {
        Connection connection = getConnection();
        connection.setAutoCommit(true);
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE plan (" +
                "day VARCHAR(1024) NOT NULL," +
                "breakfast VARCHAR(1024) NOT NULL," +
                "lunch VARCHAR(1024) NOT NULL," +
                "dinner VARCHAR(1024) NOT NULL" +
                ")");
        statement.close();
        connection.close();
    }

    /**
     * This method is used to add a meal to the database. The meal id is generated by fetching the number of meals
     * in the database and adding 1 to it. The meal id is used to link the ingredients to the meal.
     *
     * @param meal The meal to be added
     */
    @Override
    public void addMeal(Meal meal) {
        try (Connection connection = getConnection()) {
            String mealName = meal.name();
            String SQL_GET_MEAL_ID = "SELECT meal_id FROM meals WHERE meal = '%s'";
            PreparedStatement statement = connection.prepareStatement(SQL_ADD_MEAL);
            statement.setInt(1, getMeals(null).size() + 1);
            statement.setString(2, mealName);
            statement.setString(3, meal.category());
            statement.executeUpdate();
            statement = connection.prepareStatement(String.format(SQL_GET_MEAL_ID, mealName));
            ResultSet resultSet = statement.executeQuery();
            int mealId = resultSet.next() ? resultSet.getInt("meal_id") : -1;
            statement = connection.prepareStatement(SQL_ADD_INGREDIENT);
            for (int i = 0; i < meal.ingredients().length; i++) {
                statement.setString(1, meal.ingredients()[i]);
                statement.setInt(2, mealId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * This method is used to get a list of all the meals in the database. The category parameter is optional and
     * can be used to get a list of meals of a specific category. If the category parameter is null, all the meals
     * in the database are returned. Otherwise, only the meals of the specified category are returned.
     *
     * @param category The category of the meals to be returned (e.g. breakfast, lunch, dinner)
     * @return A list of meals
     */
    @Override
    public List<Meal> getMeals(String category) {
        List<Meal> meals = new ArrayList<>();
        String SQL_GET_MEALS = "SELECT * FROM meals";
        String SQL_GET_INGREDIENTS = "SELECT * FROM ingredients WHERE meal_id = %d";
        try (Connection connection = getConnection()) {
            if (category != null) {
                // Get meals of a specific category if specified
                SQL_GET_MEALS += String.format(" WHERE category = '%s'", category);
            }
            PreparedStatement statement = connection.prepareStatement(SQL_GET_MEALS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int mealId = resultSet.getInt("meal_id");
                String mealName = resultSet.getString("meal");
                String mealCategory = resultSet.getString("category");
                statement = connection.prepareStatement(String.format(SQL_GET_INGREDIENTS, mealId));
                ResultSet ingredientsResultSet = statement.executeQuery();
                List<String> ingredients = new ArrayList<>();
                while (ingredientsResultSet.next()) {
                    ingredients.add(ingredientsResultSet.getString("ingredient"));
                }
                String[] ingredientsArray = new String[ingredients.size()];
                ingredientsArray = ingredients.toArray(ingredientsArray);
                Meal meal = new Meal(mealName, mealCategory, ingredientsArray);
                meals.add(meal);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return meals;
    }

    /**
     * This method is used to save the meal plan to the database. The plan table is overwritten with the new plan.
     *
     * @param plans The meal plan to be saved
     */
    @Override
    public void planMeal(List<Plan> plans) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQL_CLEAR_PLAN);
            statement.executeUpdate();
            statement = connection.prepareStatement(SQL_ADD_PLAN);
            for (Plan plan : plans) {
                statement.setString(1, plan.day());
                statement.setString(2, plan.breakfast());
                statement.setString(3, plan.lunch());
                statement.setString(4, plan.dinner());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * This method is used to get a list of all the meal names from the database and sort them alphabetically.
     *
     * @param category The category of the meals to be returned (e.g. breakfast, lunch, dinner)
     * @return A list of alphabetically sorted meal names
     */
    public List<String> getMealNames(String category) {
        return getMeals(category).stream().map(Meal::name).sorted().collect(Collectors.toList());
    }

    /**
     * This method is used by the savePlan method to check if a plan already exists in the database.
     *
     * @return True if a plan exists, false otherwise
     */
    public boolean isPlanExist() {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQL_GET_PLAN);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * This method is used to get all the ingredients for a plan from the database.
     * First, all the meals from the plan are retrieved. Then, the ingredients needed for each meal are retrieved.
     *
     * @return A list of ingredients
     */
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        List<String> meals = new ArrayList<>();
        String SQL_GET_INGREDIENTS_FOR_MEAL = "SELECT * FROM ingredients WHERE meal_id = " +
                "(SELECT meal_id FROM meals WHERE meal = '%s')";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            PreparedStatement statement = connection.prepareStatement(SQL_GET_MEALS_FROM_PLAN);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                meals.add(resultSet.getString("breakfast"));
                meals.add(resultSet.getString("lunch"));
                meals.add(resultSet.getString("dinner"));
            }
            for (String meal : meals) {
                statement = connection.prepareStatement(String.format(SQL_GET_INGREDIENTS_FOR_MEAL, meal));
                ResultSet ingredientsResultSet = statement.executeQuery();
                while (ingredientsResultSet.next()) {
                    ingredients.add(ingredientsResultSet.getString("ingredient"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return ingredients;
    }
}
