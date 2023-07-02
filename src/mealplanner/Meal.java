package mealplanner;

import java.util.ArrayList;
import java.util.Objects;

public class Meal {
    private String mealCategory;
    private String name;
    private ArrayList<String> ingredients;

    public Meal(String name, String mealCategory, ArrayList<String> ingredients) {
        this.name = name;
        this.mealCategory = mealCategory;
        this.ingredients = ingredients;
    }

    public String getMealCategory() {
        return mealCategory;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.mealCategory);
    }
}