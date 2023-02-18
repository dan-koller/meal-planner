package io.github.dankoller.entity;

/**
 * This record represents a daily meal plan.
 *
 * @param day       The day of the week.
 * @param breakfast The meal name of the breakfast.
 * @param lunch     The meal name of the lunch.
 * @param dinner    The meal name of the dinner.
 */
public record Plan(String day, String breakfast, String lunch, String dinner) {
}
