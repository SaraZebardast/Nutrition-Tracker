package com.group20.nutritiontrackingapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.group20.nutritiontrackingapp.adapter.MealCustomRecyclerViewAdapter
import com.group20.nutritiontrackingapp.databinding.ActivityMealBinding
import com.group20.nutritiontrackingapp.db.AppDatabase
import com.group20.nutritiontrackingapp.db.Meal
import com.group20.nutritiontrackingapp.retrofit.ApiClient
import com.group20.nutritiontrackingapp.retrofit.MealService
import com.group20.nutritiontrackingapp.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealActivity : AppCompatActivity() {

    lateinit var binding: ActivityMealBinding
    private lateinit var mealService: MealService
    private lateinit var mealAdapter: MealCustomRecyclerViewAdapter
    private var mealList: MutableList<Meal> = mutableListOf()
    private val db: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Binding section
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting title
        val mealTitle: String = intent.getStringExtra("MEAL_TYPE").toString()
        binding.tvTitle.text = mealTitle

        // Set up recycler view
        mealAdapter = MealCustomRecyclerViewAdapter(this, mealList, db)
        binding.recyclerView.apply {
            adapter = mealAdapter
            layoutManager = LinearLayoutManager(this@MealActivity)
        }

        // Initialize Retrofit service
        mealService = ApiClient.getClient().create(MealService::class.java)
        fetchMeals()

        // Search box listener
        binding.searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mealAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Listeners
        binding.addMealButton.setOnClickListener {
            val selectedMeal = mealAdapter.getSelectedMeal()
            if (selectedMeal != null) {
                selectedMeal.mealType = mealTitle
                db.mealDao().insertMeal(selectedMeal)
                Toast.makeText(
                    this,
                    "${selectedMeal.name} added",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Please select a meal first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchMeals() {
        val request = mealService.getMeals()
        request.enqueue(object : Callback<List<Meal>> {
            override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch meals", t)
                Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                if (response.isSuccessful) {
                    response.body()?.let { meals ->
                        mealList.clear()
                        mealList.addAll(meals)
                        mealAdapter.setData(mealList) // İlk veriyi RecyclerView'a yansıt
                        Log.d("API_SUCCESS", "Fetched ${meals.size} meals")
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }
}
