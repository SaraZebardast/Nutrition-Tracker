package com.group20.nutritiontrackingapp

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.group20.nutritiontrackingapp.chart.DonutChart
import com.group20.nutritiontrackingapp.databinding.ActivityReportBinding
import com.group20.nutritiontrackingapp.db.AppDatabase
import com.group20.nutritiontrackingapp.db.Meal
import com.group20.nutritiontrackingapp.util.Constants

class ReportActivity : AppCompatActivity() {
    // Variables
    private lateinit var binding: ActivityReportBinding
    private lateinit var carbGestureDetector: GestureDetector
    private lateinit var proteinGestureDetector: GestureDetector
    private lateinit var fatGestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding section
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val db: AppDatabase by lazy {
            Room.databaseBuilder(
                this,
                AppDatabase::class.java,
                Constants.DATABASE_NAME
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
        val meals = db.mealDao().getAllMeals()

        if(meals.isNotEmpty())
            getMealsGiveMacros(meals)

        // Gesture
        setupGestureDetectors()
        setupTouchListeners()

        //Listeners
        binding.reportBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun getMealsGiveMacros(meals : MutableList<Meal>) {
        var totalProtein = 0.0
        var totalCarbs = 0.0
        var totalFat = 0.0
        val carbText : TextView = findViewById(R.id.carbRate)
        val proText : TextView = findViewById(R.id.proRate)
        val fatText : TextView = findViewById(R.id.fatRate)

        for (meal in meals) {
            totalProtein += meal.protein
            totalCarbs += meal.carbs
            totalFat += meal.fat
        }

        var totalMacros = totalProtein + totalCarbs + totalFat
        val proteinPercentage = (totalProtein / totalMacros * 100).toFloat()
        val carbsPercentage = (totalCarbs / totalMacros * 100).toFloat()
        val fatPercentage = (totalFat / totalMacros * 100).toFloat()

        carbText.text = String.format("%.2f", totalCarbs) + "g"
        proText.text = String.format("%.2f", totalProtein) + "g"
        fatText.text = String.format("%.2f", totalFat) + "g"
        DonutChart.updateData(carbsPercentage,proteinPercentage,fatPercentage)
    }

    private fun setupGestureDetectors() {
        carbGestureDetector = GestureDetector(this, CustomGestureListener("Carbohydrates"))
        proteinGestureDetector = GestureDetector(this, CustomGestureListener("Protein"))
        fatGestureDetector = GestureDetector(this, CustomGestureListener("Fat"))
    }

    private fun setupTouchListeners() {
        binding.apply {
            val carbLayout = findViewById<ImageView>(R.id.imageView2) as View
            val proteinLayout = findViewById<ImageView>(R.id.imageView8) as View
            val fatLayout = findViewById<ImageView>(R.id.imageView7) as View

            carbLayout.setOnTouchListener { v, event ->
                carbGestureDetector.onTouchEvent(event)
                true
            }

            proteinLayout.setOnTouchListener { v, event ->
                proteinGestureDetector.onTouchEvent(event)
                true
            }

            fatLayout.setOnTouchListener { v, event ->
                fatGestureDetector.onTouchEvent(event)
                true
            }
        }
    }

    private inner class CustomGestureListener(private val macroType: String) : GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent): Boolean {
            val message = when (macroType) {
                "Carbohydrates" -> "Carbs are your body's main source of energy. Your daily intake is ${binding.carbRate.text}"
                "Protein" -> "Protein is essential for muscle building and repair. Your daily intake is ${binding.proRate.text}"
                "Fat" -> "Healthy fats are crucial for nutrient absorption. Your daily intake is ${binding.fatRate.text}"
                else -> ""
            }
            Toast.makeText(this@ReportActivity, message, Toast.LENGTH_LONG).show()
            return true
        }
    }
}