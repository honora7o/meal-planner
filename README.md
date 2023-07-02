# meal-planner
Basic meal planner CLI application made as a personal learning-based project.

# Usage

Can add individual meals, show all meals by category, plan meals for the whole week and
save the ingredients needed (shopping list) to a file if needed. Will also handle basic input validation and throw needed exceptions.

Uses JDBC and Postgresql for all its database related needs. Will create tables on first startup if they do not exist.

# Examples

<details>
<summary>Some input validation and adding meals to table:</summary>

```bash
What would you like to do (add, show, plan, save, exit)?
>show
Which category do you want to print (breakfast, lunch, dinner)?
>breakfastt
Wrong meal category! Choose from: breakfast, lunch, dinner.
>breakfast
No meals found.
>What would you like to do (add, show, plan, save, exit)?
save
>Unable to save. Plan your meals first.
What would you like to do (add, show, plan, save, exit)?
Which meal do you want to add (breakfast, lunch, dinner)?
>breakfast
Input the meal's name:
>protein pancakes
Input the ingredients:
>whey protein, banana,oats ,milk,vanilla extract,butter
The meal has been added!
What would you like to do (add, show, plan, save, exit)?
```  
  
</details>

<details>
<summary>Showing meals by category:</summary>

```bash
What would you like to do (add, show, plan, save, exit)?
>show
Which category do you want to print (breakfast, lunch, dinner)?
>breakfast
Category: breakfast

Name: omelette
Ingredients:
flour
cheese
eggs
milk

Name: scrambled eggs
Ingredients:
eggs
milk
cheese

Name: fruit shake
Ingredients:
banana
avocado
milk
peanut butter
apple

Name: protein pancakes
Ingredients:
butter
whey protein
banana
oats
milk
vanilla extract

Name: oatmeal
Ingredients:
milk
honey
banana
oats
What would you like to do (add, show, plan, save, exit)?
```  
  
</details>

<details>
<summary>Planning for week ahead:</summary>

```bash
What would you like to do (add, show, plan, save, exit)?
>plan
Monday
fruit shake
oatmeal
omelette
protein pancakes
scrambled eggs
Choose the breakfast for Monday from the list above:
>omelette
ala minuta
brazilian lunch
burrito
cbr
pasta carbonara
Choose the lunch for Monday from the list above:
>ala minuta
cheeseburger
fried chicken
pizza
sushi
Choose the dinner for Monday from the list above:
>oatmeal
This meal doesn’t exist. Choose a meal from the list above.
>sushi
Yeah! We planned the meals for Monday.
Tuesday
fruit shake
oatmeal
omelette
protein pancakes
scrambled eggs
Choose the breakfast for Tuesday from the list above:
//Proceeds until sunday//
``` 
  
</details>

<details>
<summary>Saving shopping list to file:</summary>

```bash
What would you like to do (add, show, plan, save, exit)?
>save
Input a filename:
>shoppinglist
Saved!
```
```bash
#shoppinglist.txt
chicken x4
sour cream x2
beef x4
cheese x2
vanilla extract x2
apple
oil
honey x2
flour x3
beans x2
pasta
fries
salami x3
olive oil
chilli
mozarella x4
banana x5
avocado x3
whey protein x2
eggs x5
butter x2
oats x4
bread
tomato sauce x3
peanut butter
salmon x2
milk x7
wraps
rice x7
bacon
brocolli x3
pizza dough x3
ketchup
```  
  
</details>
