package com.group20.nutritiontrackingapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.group20.nutritiontrackingapp.util.Constants

@Dao
interface ExerciseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercise(exercise: Exercise):Long

    @Update
    fun updateExercise(exercise: Exercise):Int

    @Delete
    fun deleteExercise(exercise: Exercise):Int

    @Query("DELETE FROM ${Constants.EXERCISE_TABLE}")
    fun deleteAllExercises()

    @Query("SELECT * FROM ${Constants.EXERCISE_TABLE} ORDER BY id DESC")
    fun getAllExercises():MutableList<Exercise>

    @Query("SELECT * FROM ${Constants.EXERCISE_TABLE} WHERE id =:id")
    fun getExerciseById(id:Int):Exercise

    @Query("SELECT * FROM ${Constants.EXERCISE_TABLE} WHERE name LIKE :name")
    fun getExercisesByName(name:String):MutableList<Exercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllExercises(exercise: ArrayList<Exercise>){
        exercise.forEach{
            insertExercise(it)
        }
    }
}