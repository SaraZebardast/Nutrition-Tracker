package com.group20.nutritiontrackingapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.group20.nutritiontrackingapp.util.Constants

@Database(entities = [Recipe::class, Exercise::class, Person::class, Meal::class], version = 8)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDAO
    abstract fun exerciseDao(): ExerciseDAO
    abstract fun personDao(): PersonDAO
    abstract fun mealDao(): MealDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
