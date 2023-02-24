# Meal Planner

This is a simple meal planner that I wrote to help me plan my meals for the week. It's a command line tool that
allows you to add meals to a database and then plan them for the week.

## Get started

### Requirements

Make sure you have the following installed on your machine:

- Java 17
- Gradle 7.2
- PostgreSQL 14


1. Clone the repo

```shell
git clone https://github.com/dan-koller/meal-planner
```

2. Set up the database (Check out the [database setup](#database-setup) section for more details)
   _For example_, if you're using Homebrew on Apple Silicon, you can use the following command:

```shell
pg_ctl -D /opt/homebrew/var/postgresql@14 start
```

2. Build the project

```shell
./gradlew build
```

3. Run the project

```shell
./gradlew run
```

_You can also run the project from your IDE of choice._

## Usage

You can add new meals, show meals for a category, plan meals for the week, and create a shopping list.

### Add a new meal

```shell
What would you like to do (add, show, plan, save, exit)?
> add
Which meal do you want to add (breakfast, lunch, dinner)?
> lunch
Input the meal's name:
> salad
Input the ingredients:
> lettuce, tomato, onion, cheese, olives
The meal has been added!
```

### Show meals for a category

```shell
What would you like to do (add, show, plan, save, exit)?
> show
Which category do you want to print (breakfast, lunch, dinner)?
> breakfast
Category: breakfast
Name: oatmeal
Ingredients:
oats
milk
banana
peanut butter
```

### Plan meals for the week

```shell
What would you like to do (add, show, plan, save, exit)?
> plan
Monday
oatmeal
sandwich
scrambled eggs
yogurt
Choose the breakfast for Monday from the list above:
> yogurt
avocado egg salad
chicken salad
sushi
tomato salad
wraps
Choose the lunch for Monday from the list above:
> tomato salad
beef with broccoli
pesto chicken
pizza
ramen
tomato soup
Choose the dinner for Monday from the list above:
> spaghetti
This meal doesn’t exist. Choose a meal from the list above.
> ramen
Yeah! We planned the meals for Monday.

<... A bunch of other days ...>

Sunday
oatmeal
sandwich
scrambled eggs
yogurt
Choose the breakfast for Sunday from the list above:
> scrambled eggs
avocado egg salad
chicken salad
sushi
tomato salad
wraps
Choose the lunch for Sunday from the list above:
> tomato salad
beef with broccoli
pesto chicken
pizza
ramen
tomato soup
Choose the dinner for Sunday from the list above:
> beef with broccoli
Yeah! We planned the meals for Sunday.

Monday
Breakfast: yogurt
Lunch: tomato salad
Dinner: ramen

<... A bunch of other days ...>

Sunday
Breakfast: scrambled eggs
Lunch: tomato salad
Dinner: beef with broccoli
```

### Create a shopping list

```shell
What would you like to do (add, show, plan, save, exit)?
> save
Input a filename:
> shoppinglist.txt
Saved!
```

## Database setup

Make sure to create meals_db database and add the url, username, and password to your .env file before running the app.

1. Download and install PostgreSQL (You can find a guide for
   macOS [here](https://gist.github.com/dan-koller/ba756dec5f9beeba02cc12fe2acf7211))
2. Create a database called meals_db
3. Create a .env from the .env.example in your resources folder
4. Add the database url, username, and password to your .env file
5. Run the app

## Testing

The application is tested using JUnit 5. The code coverage is 80% for classes, 50% for methods and 46% for lines.

Run the tests with the following command:

```shell
gradle test
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request if you have any ideas for improvements.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.