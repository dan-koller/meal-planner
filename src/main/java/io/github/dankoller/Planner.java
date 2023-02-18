package io.github.dankoller;

import io.github.dankoller.entity.Meal;
import io.github.dankoller.entity.Plan;
import io.github.dankoller.repository.Driver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Planner {
    private boolean isTerminated = false;
    private final Scanner scanner = new Scanner(System.in);
    private final Pattern validMealCategory = Pattern.compile("breakfast|lunch|dinner");
    private final Pattern validMealName = Pattern.compile("[a-zA-Z ]+");
    private final Pattern validIngredients = Pattern.compile("([a-zA-Z]+,? ?)+(?<!,)(?<! )");
    private Driver driver;

    /**
     * The constructor is used to initialize the database driver which is used to communicate with the database.
     */
    public Planner() {
        try {
            driver = new Driver();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * The run method is used to handle the user input and call the corresponding methods.
     */
    public void run() {
        while (!isTerminated) {
            System.out.println("What would you like to do (add, show, plan, save, exit)?");
            String command = scanner.nextLine();
            switch (command) {
                case "add" -> addMeal();
                case "show" -> showMeal();
                case "plan" -> planMeal();
                case "save" -> savePlan();
                case "exit" -> {
                    System.out.println("Bye!");
                    isTerminated = true;
                    System.exit(0);
                }
            }
        }
    }

    /**
     * This method is used to create a dialog with the user to add a new meal to the database. White spaces from the
     * user input are removed. The user can choose between breakfast, lunch and dinner. The ingredients are separated by
     * a comma and a white space and validated with a regular expression.
     */
    private void addMeal() {
        boolean isVerifiedMealCategory = false;
        boolean isVerifiedMeal = false;
        boolean isVerifiedIngredients = false;
        String mealCategory = "";
        String mealName = "";
        String[] ingredients = null;
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        while (!isVerifiedMealCategory) {
            mealCategory = scanner.nextLine();
            if (!validMealCategory.matcher(mealCategory).matches()) {
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                continue;
            }
            isVerifiedMealCategory = true;
        }
        System.out.println("Input the meal's name:");
        while (!isVerifiedMeal) {
            mealName = scanner.nextLine();
            if (!validMealName.matcher(mealName).matches()) {
                System.out.println("Wrong format. Use letters only!");
                continue;
            }
            isVerifiedMeal = true;
        }
        System.out.println("Input the ingredients:");
        while (!isVerifiedIngredients) {
            ingredients = scanner.nextLine().split(",");
            if (!validIngredients.matcher(String.join(",", ingredients)).matches()) {
                System.out.println("Wrong format. Use letters only!");
                continue;
            }
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = ingredients[i].trim();
            }
            isVerifiedIngredients = true;
        }
        Meal meal = new Meal(mealName, mealCategory, ingredients);
        driver.addMeal(meal);
        System.out.println("The meal has been added!");
    }

    /**
     * This method is used to show a certain meal category to the user. A dialog is used to ask for the category.
     * The user can choose between breakfast, lunch and dinner. After the user has chosen a category, all the meals
     * in this category are printed to the console.
     */
    private void showMeal() {
        boolean isVerifiedMealCategory = false;
        String mealCategory = "";
        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
        while (!isVerifiedMealCategory) {
            mealCategory = scanner.nextLine();
            if (!validMealCategory.matcher(mealCategory).matches()) {
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                continue;
            }
            isVerifiedMealCategory = true;
        }
        List<Meal> meals = driver.getMeals(mealCategory);
        if (meals.isEmpty()) {
            System.out.println(("No meals found."));
        } else {
            System.out.printf("Category: %s%n", mealCategory);
            for (Meal meal : meals) {
                System.out.printf("Name: %s%nIngredients:%n", meal.name());
                for (int i = 0; i < meal.ingredients().length; i++) {
                    System.out.println(meal.ingredients()[i]);
                }
                System.out.println();
            }
        }
    }

    /**
     * This method creates a dialog that helps the user to plan a meal for a week. After the planning is done,
     * the meal plan is saved to the database. The planning can only be done for a week. The latest planning is
     * always overwritten.
     */
    private void planMeal() {
        List<String> weekdays = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        List<String> mealnames;
        String category;
        Plan plan;
        List<Plan> plans = new ArrayList<>();
        for (String weekday : weekdays) {
            System.out.println(weekday);
            category = "breakfast";
            mealnames = driver.getMealNames(category);
            String breakfast = getMealOption(mealnames, category, weekday);
            category = "lunch";
            mealnames = driver.getMealNames(category);
            String lunch = getMealOption(mealnames, category, weekday);
            category = "dinner";
            mealnames = driver.getMealNames(category);
            String dinner = getMealOption(mealnames, category, weekday);
            plan = new Plan(weekday, breakfast, lunch, dinner);
            plans.add(plan);
            System.out.printf("Yeah! We planned the meals for %s.%n", weekday);
        }
        driver.planMeal(plans);
        printPlan(plans);
    }

    /**
     * This helper method is used to by the planMeal method to get the meal option from the user.
     *
     * @param mealNames The list of meal names that the user can choose from
     * @param category  The category of the meal
     * @param weekday   The weekday for which the meal is planned
     * @return The name of the meal that the user has chosen
     */
    private String getMealOption(List<String> mealNames, String category, String weekday) {
        boolean isVerifiedOption = false;
        String option = "";
        for (String mealName : mealNames) {
            System.out.println(mealName);
        }
        System.out.printf("Choose the %s for %s from the list above:%n", category, weekday);
        while (!isVerifiedOption) {
            option = scanner.nextLine();
            if (!mealNames.contains(option)) {
                System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                continue;
            }
            isVerifiedOption = true;
        }
        return option;
    }

    /**
     * This helper method is used by the planMeal method to print the meal plan to the console.
     *
     * @param plans The list of (daily) plans that should be printed
     */
    private void printPlan(List<Plan> plans) {
        for (Plan p : plans) {
            System.out.printf("%s%nBreakfast: %s%nLunch: %s%nDinner: %s%n%n",
                    p.day(), p.breakfast(), p.lunch(), p.dinner());
        }
    }

    /**
     * This method saves the ingredients for the week to a file. The file name is asked from the user.
     * The ingredients can only be saved if a meal plan has been created.
     */
    private void savePlan() {
        if (!driver.isPlanExist()) {
            System.out.println("Unable to save. Plan your meals first.");
            return;
        }
        List<String> ingredients = driver.getIngredients();
        ingredients = removeDuplicates(ingredients);
        System.out.println("Input a filename:");
        String filename = scanner.nextLine();
        saveIngredientsToFile(ingredients, filename);
        System.out.println("Saved!");
    }

    /**
     * This helper method is used by the savePlan method to remove duplicates from the list of ingredients and to add
     * the number of times an ingredient is needed to the ingredient name.
     * First, the frequency of each ingredient is counted. Then, the ingredients are added to the new list. If the
     * frequency of an ingredient is greater than 1, the ingredient name is appended with xN, where N is the frequency.
     *
     * @param list The list of ingredients
     * @return The list of ingredients without duplicates and formatted
     */
    private List<String> removeDuplicates(List<String> list) {
        List<String> newList = new ArrayList<>();
        Map<String, Integer> frequency = new HashMap<>();
        for (String ingredient : list) {
            if (frequency.containsKey(ingredient)) {
                frequency.put(ingredient, frequency.get(ingredient) + 1);
            } else {
                frequency.put(ingredient, 1);
            }
        }
        for (String ingredient : frequency.keySet()) {
            if (frequency.get(ingredient) > 1) {
                newList.add(ingredient + " x" + frequency.get(ingredient));
            } else {
                newList.add(ingredient);
            }
        }
        return newList;
    }

    /**
     * This helper method is used by the savePlan method to save the ingredients to a file.
     *
     * @param ingredients The list of ingredients
     * @param filename    The name of the file
     */
    private void saveIngredientsToFile(List<String> ingredients, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String ingredient : ingredients) {
                writer.write(ingredient + "\n");
            }
        } catch (IOException e) {
            System.out.println("An exception occurs " + e.getMessage());
        }
    }
}
