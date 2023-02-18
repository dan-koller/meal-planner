package io.github.dankoller.repository;

import io.github.dankoller.entity.Meal;
import io.github.dankoller.entity.Plan;

import java.util.List;

/**
 * This interface represents a repository for meals and meal plans.
 */
public interface MealRepository {
    void addMeal(Meal meal);

    List<Meal> getMeals(String category);

    void planMeal(List<Plan> plans);
}
