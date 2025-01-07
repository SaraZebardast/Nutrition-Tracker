package com.group20.nutritiontrackingapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.group20.nutritiontrackingapp.util.Constants

@Entity(tableName = Constants.RECIPE_TABLE)
class Recipe(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("title")
    var title: String,
    @SerializedName("instructions")
    var instructions: String,
    @SerializedName("totalCalories")
    var totalCalories: Int,
    @SerializedName("totalProtein")
    var totalProtein: Double,
    @SerializedName("totalCarbs")
    var totalCarbs: Double,
    @SerializedName("totalFat")
    var totalFat: Double,
    @SerializedName("prepTime")
    var prepTime: Int,
    @SerializedName("imgUrl")
    var imgUrl: String
)
{
    override fun toString(): String {
        return "Recipe(id=$id, title='$title', instructions='$instructions', totalCalories=$totalCalories, totalProtein=$totalProtein, totalCarbs=$totalCarbs, totalFat=$totalFat, prepTime=$prepTime, imgUrl=$imgUrl)"
    }
}

