package mealplanner;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MealDatabaseManager {
    private static final String DB_URL = "jdbc:postgresql:meals_db";
    private static final String USER = "postgres";
    private static final String PASS = "1111";
    private static Connection connection;
    private static final String[] MEAL_CATEGORIES = {"breakfast", "lunch", "dinner"};
    private static final String[] WEEK_DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};


    public void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();

            String createMealsTableSQL = "CREATE TABLE IF NOT EXISTS meals (category VARCHAR, meal VARCHAR, meal_id INTEGER)";
            String createIngredientsTableSQL = "CREATE TABLE IF NOT EXISTS ingredients (ingredient VARCHAR, ingredient_id INTEGER, meal_id INTEGER)";
            String createPlanTableSQL = "CREATE TABLE IF NOT EXISTS plan (category VARCHAR, meal_id INTEGER, day VARCHAR)";

            statement.executeUpdate(createMealsTableSQL);
            statement.executeUpdate(createIngredientsTableSQL);
            statement.executeUpdate(createPlanTableSQL);
            //System.out.println("Connected to the database and created needed tables!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database!");
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        return connection;
    }

    public void addMealToDB(Meal meal) {
        try {
            Connection connection = this.getConnection();
            connection.setAutoCommit(false); // start a transaction to make sure both statements go through at once (or at all)

            int mealId = meal.hashCode();
            insertMeal(connection, meal, mealId);
            insertIngredients(connection, meal, mealId);

            connection.commit(); // commit transaction
            connection.setAutoCommit(true); // reset to default state
            System.out.println("The meal has been added!");
        } catch (SQLException e) {
            System.err.println("Failed to add the meal and ingredients to the database!");
            e.printStackTrace();
        }
    }

    private void insertMeal(Connection connection, Meal meal, int mealId) throws SQLException {
        PreparedStatement mealStatement = connection.prepareStatement(
                "INSERT INTO meals (meal, category, meal_id) VALUES (?, ?, ?)"
        );
        mealStatement.setString(1, meal.getName());
        mealStatement.setString(2, meal.getMealCategory());
        mealStatement.setInt(3, mealId);
        mealStatement.executeUpdate();
    }

    private void insertIngredients(Connection connection, Meal meal, int mealId) throws SQLException {
        PreparedStatement ingredientStatement = connection.prepareStatement(
                "INSERT INTO ingredients (ingredient, ingredient_id, meal_id) VALUES (?, ?, ?)"
        );

        for (String ingredient : meal.getIngredients()) {
            ingredientStatement.setString(1, ingredient);
            ingredientStatement.setInt(2, ingredient.hashCode());
            ingredientStatement.setInt(3, mealId);
            ingredientStatement.executeUpdate();
        }
    }

    public void showMealsByCategory(String mealCategory) {
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT m.meal, m.category, i.ingredient " +
                            "FROM meals m " +
                            "JOIN ingredients i ON m.meal_id = i.meal_id " +
                            "WHERE m.category = ? " +
                            "ORDER BY m.meal_id"
            );
            statement.setString(1, mealCategory);
            ResultSet resultSet = statement.executeQuery();

            ArrayList<String> ingredients = new ArrayList<>();

            boolean mealsExist = resultSet.next();
            if (!mealsExist) {
                System.out.println("No meals found.");
            } else {
                System.out.println("Category: " + mealCategory);
                String currentMeal = "";

                do {
                    String meal = resultSet.getString("meal");
                    String ingredient = resultSet.getString("ingredient");

                    if (!meal.equals(currentMeal)) {
                        if (!currentMeal.isEmpty()) {
                            Meal mealObj = new Meal(currentMeal, mealCategory, ingredients);
                            printFullMeal(mealObj);
                            ingredients.clear();
                        }
                        currentMeal = meal;
                    }

                    ingredients.add(ingredient);
                } while (resultSet.next());

                Meal mealObj = new Meal(currentMeal, mealCategory, ingredients);
                printFullMeal(mealObj);
            }

        } catch (SQLException e) {
            System.err.println("Failed to retrieve meals from the database!");
            e.printStackTrace();
        }
    }

    private void printFullMeal(Meal meal) {
        System.out.println("\nName: " + meal.getName() + "\nIngredients:");
        meal.getIngredients().forEach(System.out::println);
    }

    public ResultSet getMealsByCategory(String mealCategory) {
        ResultSet mealSet = null;
        try {
            Connection connection = this.getConnection();
            String selectMealsByCategoryInAscOrderSQL = "SELECT m.meal, m.category, m.meal_id " +
                                                                "FROM meals m " +
                                                                "WHERE m.category = ? " +
                                                                "ORDER BY m.meal ASC";
            PreparedStatement statement = connection.prepareStatement(selectMealsByCategoryInAscOrderSQL
                    , ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY
            );
            statement.setString(1, mealCategory);
            mealSet = statement.executeQuery();

        } catch (SQLException e) {
            System.out.println("Failed to get names from the database!");
            e.printStackTrace();
        }

        return mealSet;
    }

    public void printMealNamesInSet(ResultSet mealSet){
        try {
            while (mealSet.next()) {
                String mealName = mealSet.getString("meal");
                System.out.println(mealName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getMealInfoByName(String mealName) {
        ArrayList<String> mealInfo = new ArrayList<>();
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT m.category, m.meal_id " +
                            "FROM meals m " +
                            "WHERE m.meal = ? " +
                            "ORDER BY m.meal ASC"
            );
            statement.setString(1, mealName);
            ResultSet mealSet = statement.executeQuery();

            if (mealSet.next()) {
                String mealCategory = mealSet.getString("category");
                int mealId = mealSet.getInt("meal_id");

                mealInfo.add(mealCategory);
                mealInfo.add(String.valueOf(mealId));
            }

        } catch (SQLException e) {
            System.out.println("Failed to get names from the database!");
        }

        return mealInfo;
    }

    public void addMealToPlanTable(ArrayList<String> mealInfo, String currDay) {
        try {
            Connection connection = this.getConnection();
            PreparedStatement mealStatement = connection.prepareStatement(
                    "INSERT INTO plan (category, meal_id, day) VALUES (?, ?, ?)"
            );
            mealStatement.setString(1, mealInfo.get(0));
            mealStatement.setInt(2, Integer.valueOf(mealInfo.get(1)));
            mealStatement.setString(3, currDay);
            mealStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add meal to plan table.");
        }
    }

    public void printMealPlan() {
        try {
            Connection connection = this.getConnection();
            for (String day : WEEK_DAYS) {
                System.out.println(day);
                for (String category : MEAL_CATEGORIES) {
                    PreparedStatement mealPlanStatement = connection.prepareStatement(
                            "SELECT m.meal, p.meal_id, p.category, p.day " +
                                    "FROM plan p " +
                                    "JOIN meals m ON p.meal_id = m.meal_id " +
                                    "WHERE p.day = ? AND p.category = ? "
                    );
                    mealPlanStatement.setString(1, day);
                    mealPlanStatement.setString(2, category);
                    ResultSet planSet = mealPlanStatement.executeQuery();
                    while (planSet.next()) {
                        String currMeal = planSet.getString("meal");
                        System.out.printf("%s: %s\n", category.substring(0, 1).toUpperCase() + category.substring(1), currMeal);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to print meal plan!");
        }
    }

    public boolean hasPlanToSave() {
        boolean hasPlan = false;
        try {
            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT EXISTS (SELECT 1 FROM plan LIMIT 1)");
            resultSet.next();
            hasPlan = resultSet.getBoolean(1);
        } catch (SQLException e) {
            System.out.println("Failed to save plan!");
        }
        return hasPlan;
    }

    public Map<String, Integer> getIngredientCountInPlan() {
        Map<String, Integer> ingredientCountMap = new HashMap<>();
        try {
            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet ingredientsInPlanSet = statement.executeQuery(
                    "SELECT i.ingredient, COUNT(i.ingredient) AS ingredient_count " +
                            "FROM ingredients i " +
                            "JOIN plan p ON i.meal_id = p.meal_id " +
                            "GROUP BY i.ingredient"
            );

            while (ingredientsInPlanSet.next()) {
                String currIngredient = ingredientsInPlanSet.getString("ingredient");
                int currIngredientCount = ingredientsInPlanSet.getInt("ingredient_count");
                ingredientCountMap.put(currIngredient, currIngredientCount);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get ingredient count in plan!");
        }
        return ingredientCountMap;
    }
}