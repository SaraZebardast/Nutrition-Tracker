package com.group20.nutritiontrackingapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.group20.nutritiontrackingapp.util.Constants
import com.google.gson.annotations.SerializedName

@Entity(tableName = Constants.MEAL_TABLE)
class Meal(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("name")
    var name: String,

    @SerializedName("servingSize")
    var servingSize: Double = 1.0,  // Multiplier for recipe portions

    @SerializedName("calories")
    var calories: Int,

    @SerializedName("protein")
    var protein: Double,

    @SerializedName("carbs")
    var carbs: Double,

    @SerializedName("fat")
    var fat: Double,

    @SerializedName("mealType")
    var mealType: String? = null // e.g., "Breakfast", "Lunch", "Dinner", "Snack"
)

{
    override fun toString(): String {
        return "Meal\nName: $name\n" +
                "Calories: $calories, Protein: $protein g, Carbs: $carbs g, Fat: $fat g\n" +
                "Meal Type: $mealType, Serving Size: $servingSize"
    }
}