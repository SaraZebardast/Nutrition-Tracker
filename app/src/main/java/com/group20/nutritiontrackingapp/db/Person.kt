package com.group20.nutritiontrackingapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.group20.nutritiontrackingapp.util.Constants

@Entity(tableName = Constants.PERSON_TABLE)
class Person (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 1000,
    var name: String,
    var age: Int,
    var weight: Double,
    var height: Int,
    var sex: Char,
    var calorieGoal: Int,
    var proteinGoal: Double
){
}