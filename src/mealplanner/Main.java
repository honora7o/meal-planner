package mealplanner;

public class Main {
    public static void main(String[] args) {
        MealDatabaseManager mealDbManager = new MealDatabaseManager();
        Menu menuUI = new Menu();
        mealDbManager.connectToDatabase();
        menuUI.start();
    }
}