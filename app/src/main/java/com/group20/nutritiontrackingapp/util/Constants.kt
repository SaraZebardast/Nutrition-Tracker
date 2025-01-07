package com.group20.nutritiontrackingapp.util

object Constants {
    const val DATABASE_NAME = "nutrition_db"
    const val PERSON_TABLE = "person"
    const val MEAL_TABLE = "meal"
    const val RECIPE_TABLE = "recipe"
    const val EXERCISE_TABLE = "exercise"

    // Retrofit for meals
    var baseUrl: String = "https://www.jsonkeeper.com/b/"

    //Retrofit for recipe
    var recipeUrl: String = "https://jsonkeeper.com/b/"
}
