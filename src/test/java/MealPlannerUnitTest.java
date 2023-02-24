import io.github.dankoller.Main;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MealPlannerUnitTest {

    /**
     * This app is a cli app that allows the user to add meals and meal plans. In order to test this,
     * we need to mimic the user's input. We can do this by using the Scanner class.
     * 1. Start the app
     * 2. Read the prompt
     * 3. Send the user's input
     * 4. Read the output
     * 5. Compare the output to the expected output
     * These are only some small tests to prove that the basic functionality works.
     */

    private final Scanner scanner = new Scanner(System.in);

    @Test
    public void startApp() {
        try {
            Main.main(null);
            String input = scanner.nextLine();
            assertEquals("What would you like to do (add, show, plan, save, exit)?", input);
            System.setIn(new ByteArrayInputStream("exit".getBytes()));
            input = scanner.nextLine();
            assertEquals("Bye!", input);
        } catch (Exception e) {
            System.err.println("Could not start app: " + e.getMessage());
        }
    }

    @Test
    public void testAddMeal() {
        try {
            Main.main(null);
            String input = scanner.nextLine();
            assertEquals("What would you like to do (add, show, plan, save, exit)?", input);
            System.setIn(new ByteArrayInputStream("add".getBytes()));
            input = scanner.nextLine();
            assertEquals("Which meal do you want to add (breakfast, lunch, dinner)?", input);
            System.setIn(new ByteArrayInputStream("breakfast".getBytes()));
            input = scanner.nextLine();
            assertEquals("Input the meal's name:", input);
            System.setIn(new ByteArrayInputStream("oatmeal with berries".getBytes()));
            input = scanner.nextLine();
            assertEquals("Input the ingredients:", input);
            System.setIn(new ByteArrayInputStream("oatmeal, milk, berries".getBytes()));
            input = scanner.nextLine();
            assertEquals("Meal added!", input);
        } catch (Exception e) {
            System.err.println("Could not start app: " + e.getMessage());
        }
    }
}
