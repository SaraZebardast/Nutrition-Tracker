package com.group20.nutritiontrackingapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.group20.nutritiontrackingapp.util.Constants

@Dao
interface RecipeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(customer: Recipe):Long

    @Update
    fun updateRecipe(customer: Recipe):Int

    @Delete
    fun deleteRecipe(customer: Recipe):Int

    @Query("DELETE FROM ${Constants.RECIPE_TABLE}")
    fun deleteAllRecipes()

    @Query("SELECT * FROM ${Constants.RECIPE_TABLE} ORDER BY id DESC")
    fun getAllRecipes():MutableList<Recipe>

    @Query("SELECT * FROM ${Constants.RECIPE_TABLE} WHERE id =:id")
    fun getRecipeById(id:Int):Recipe

    @Query("SELECT * FROM ${Constants.RECIPE_TABLE} WHERE title LIKE :title")
    fun getRecipesByName(title:String):MutableList<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRecipes(recipes: List<Recipe>){
        recipes.forEach{
            insertRecipe(it)
        }
    }
}
