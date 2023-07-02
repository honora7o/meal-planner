package mealplanner;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Menu {
    private static final String[] MEAL_CATEGORIES = {"breakfast", "lunch", "dinner"};
    private static final String[] WEEK_DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static final String WRONG_FORMAT = "Wrong format. Use letters only!";
    private static final String WRONG_MEAL_CATEGORY = "Wrong meal category! Choose from: breakfast, lunch, dinner.";
    private static final String NO_PLANS_DEFINED = "Unable to save. Plan your meals first.";
    private static final String NON_EXIST_MEAL = "This meal doesn’t exist. Choose a meal from the list above.";
    private final MealDatabaseManager mealDbManager = new MealDatabaseManager();
    private final FileManager fileManager = new FileManager();

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        while (run) {
            System.out.println("What would you like to do (add, show, plan, save, exit)?");
            String command = scanner.nextLine();
            switch (command) {
                case ("add") -> addMeal(scanner);
                case ("show") -> showMealsByCategory(scanner);
                case ("plan") -> buildPlan(scanner);
                case ("save") -> savePlanUI(scanner);
                case ("exit") -> run = false;
            }
        }
        System.out.println("Bye!");
    }

    private void addMeal(Scanner scanner) {
        String mealCategory;
        String mealName;
        ArrayList<String> ingredients = new ArrayList<>();

        while (true) {
            System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
            String input = scanner.nextLine();

            if (input.matches("^(breakfast|lunch|dinner)$")) {
                mealCategory = input;
                break;
            }
            System.out.println(WRONG_MEAL_CATEGORY);
        }

        while (true) {
            System.out.println("Input the meal's name:");
            String input = scanner.nextLine();

            boolean containsNonAlphabeticCharacters = containsNonAlphabeticCharacters(input);

            if (!containsNonAlphabeticCharacters && !input.isBlank()) {
                mealName = input;
                break;
            }
            System.out.println(WRONG_FORMAT);
        }

        while (true) {
            System.out.println("Input the ingredients:");
            String[] inputSplit = scanner.nextLine().trim().split(",");

            boolean ingredientValidFormat = true;
            for (String ingredient : inputSplit) {
                if (containsNonAlphabeticCharacters(ingredient.trim())
                        || ingredient.isEmpty()
                        || ingredient.endsWith(",")) {
                    ingredientValidFormat = false;
                    break;
                }
            }

            if (ingredientValidFormat) {
                for (String ingredient : inputSplit) {
                    ingredients.add(ingredient.trim());
                }
                break;
            }
            System.out.println(WRONG_FORMAT);
        }
        mealDbManager.addMealToDB(new Meal(mealName, mealCategory, ingredients));
    }

    private void showMealsByCategory(Scanner scanner) {
        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");

        while (true) {
            String mealCategory = scanner.nextLine();
            if (mealCategory.matches("^(breakfast|lunch|dinner)$")) {
                mealDbManager.showMealsByCategory(mealCategory);
                break;
            } else {
                System.out.println(WRONG_MEAL_CATEGORY);
            }
        }
    }

    private void buildPlan(Scanner scanner) {
        String currMeal;

        for (String day : WEEK_DAYS) {
            System.out.println(day);
            for (String mealCategory : MEAL_CATEGORIES) {
                ResultSet currCategoryMealSet = mealDbManager.getMealsByCategory(mealCategory);
                mealDbManager.printMealNamesInSet(currCategoryMealSet);
                System.out.printf("Choose the %s for %s from the list above:\n", mealCategory, day);
                while (true) {
                    String chosenMeal = scanner.nextLine().trim();
                    if (setHasChosenMeal(currCategoryMealSet, chosenMeal)) {
                        currMeal = chosenMeal;
                        ArrayList<String> mealInfo = mealDbManager.getMealInfoByName(currMeal);
                        mealDbManager.addMealToPlanTable(mealInfo, day);
                        break;
                    } else {
                        System.out.println(NON_EXIST_MEAL);
                    }
                }
            }
            System.out.printf("Yeah! We planned the meals for %s.\n", day);
        }
        mealDbManager.printMealPlan();
    }

    private boolean containsNonAlphabeticCharacters(String input) {
        return input.matches(".*[^a-zA-Z,\\s].*");
    }

    private boolean setHasChosenMeal(ResultSet mealSet, String chosenMeal) {
        boolean hasChosenMeal = false;
        try {
            mealSet.beforeFirst();
            while (mealSet.next()) {
                String currMeal = mealSet.getString("meal");
                if (currMeal.equals(chosenMeal)) {
                    hasChosenMeal = true;
                    break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to check if set has chosen meal!");
        }
        return hasChosenMeal;
    }

    private void savePlanUI(Scanner scanner) {
        if (!mealDbManager.hasPlanToSave()) {
            System.out.println(NO_PLANS_DEFINED);
        } else {
            Map<String, Integer> ingredientCountMap = mealDbManager.getIngredientCountInPlan();

            System.out.println("Input a filename:");
            String fileName = scanner.nextLine();

            fileManager.savePlanToFile(fileName, ingredientCountMap);
            System.out.println("Saved!");
        }
    }
}