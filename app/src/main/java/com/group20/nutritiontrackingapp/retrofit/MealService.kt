package com.group20.nutritiontrackingapp.retrofit

import com.group20.nutritiontrackingapp.db.Meal
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MealService {
    @GET("QQO2")
    fun getMeals(): Call<List<Meal>>
}
