package com.group20.nutritiontrackingapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.group20.nutritiontrackingapp.util.Constants

@Entity(tableName = Constants.EXERCISE_TABLE)
class Exercise(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var caloriesPerHour: Int,
    var category: String,
    var description: String
)
