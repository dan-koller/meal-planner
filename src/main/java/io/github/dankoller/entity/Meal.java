package io.github.dankoller.entity;

/**
 * This record represents a meal.
 *
 * @param name        The name of the meal.
 * @param category    The category of the meal (breakfast, lunch, dinner).
 * @param ingredients The ingredients of the meal.
 */
public record Meal(String name, String category, String[] ingredients) {
    public Meal(String name, String category, String[] ingredients) {
        this.category = category;
        this.name = name;
        this.ingredients = ingredients.clone();
    }
}
