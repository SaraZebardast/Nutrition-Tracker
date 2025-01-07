package com.group20.nutritiontrackingapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.group20.nutritiontrackingapp.util.Constants

@Dao
interface MealDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeal(meal: Meal):Long

    @Update
    fun updateMeal(meal: Meal):Int

    @Delete
    fun deleteMeal(meal: Meal):Int

    @Query("DELETE FROM ${Constants.MEAL_TABLE}")
    fun deleteAllMeals()

    @Query("SELECT * FROM ${Constants.MEAL_TABLE} ORDER BY id DESC")
    fun getAllMeals():MutableList<Meal>

    @Query("SELECT * FROM ${Constants.MEAL_TABLE} WHERE id =:id")
    fun getMealById(id:Int):Meal

    @Query("SELECT * FROM ${Constants.MEAL_TABLE} WHERE name LIKE :name")
    fun getMealsByName(name:String):MutableList<Meal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMeals(meal: ArrayList<Meal>){
        meal.forEach{
            insertMeal(it)
        }
    }
}