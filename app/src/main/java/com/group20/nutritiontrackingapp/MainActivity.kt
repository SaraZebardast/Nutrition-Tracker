package com.group20.nutritiontrackingapp

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.text.Editable
import android.text.TextWatcher
import com.github.jinatonic.confetti.CommonConfetti
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.group20.nutritiontrackingapp.adapter.ExerciseCustomRecyclerViewAdapter
import com.group20.nutritiontrackingapp.adapter.RecipeCustomRecyclerViewAdapter
import com.group20.nutritiontrackingapp.databinding.ActivityMainBinding
import com.group20.nutritiontrackingapp.databinding.ExerciseDialogBinding
import com.group20.nutritiontrackingapp.db.AppDatabase
import com.group20.nutritiontrackingapp.db.Exercise
import com.group20.nutritiontrackingapp.db.Meal
import com.group20.nutritiontrackingapp.db.Person
import com.group20.nutritiontrackingapp.db.Recipe
import com.group20.nutritiontrackingapp.retrofitRecipe.ApiClient2
import com.group20.nutritiontrackingapp.retrofitRecipe.RecipeService
import com.group20.nutritiontrackingapp.util.Constants
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.max
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(),ExerciseCustomRecyclerViewAdapter.ExerciseAdapterInterface {

    // Variables
    private lateinit var customDialog: Dialog
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingForDialog: ExerciseDialogBinding
    private var adapter: RecipeCustomRecyclerViewAdapter?=null
    var exerciseAdapter: ExerciseCustomRecyclerViewAdapter? = null
    private var selectedExercise: Exercise? = null
    private var exerciseTimeMinutes: Int = 0
    private var totalCaloriesBurned: Int = 0
    private var activeMinutes: Int = 0
    private var waterCount = 0
    private lateinit var waterGlasses: List<ImageView>
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var progress = 0
    private var maxProg = 0
    private var caloricGoal = 2000
    private val PREFS_NAME = "ExercisePrefs"
    private val KEY_CALORIES_BURNED = "caloriesBurned"
    private val KEY_ACTIVE_MINUTES = "activeMinutes"
    private val KEY_LAST_EXERCISE_DATE = "lastExerciseDate"
    private lateinit var waterSound: MediaPlayer
    private lateinit var recipeService : RecipeService
    private val MEAL_PREFS_NAME = "MealPrefs"
    private val KEY_LAST_MEAL_DATE = "lastMealDate"


    // Database
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
        bindingForDialog = ExerciseDialogBinding.inflate(layoutInflater)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndResetDailyValues()

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = RecipeCustomRecyclerViewAdapter(this, mutableListOf())
        binding.recipeRecyclerView.adapter = adapter
        if(db.recipeDao().getAllRecipes().isEmpty())
            fetchRecipes()
        else getData()

        waterSound = MediaPlayer.create(this, R.raw.water)

        //Listeners
        binding.animatedText.setOnClickListener{
            YoYo.with(Techniques.Bounce)  // You can change Bounce to any other animation
                .duration(700)            // Duration in milliseconds
                .playOn(binding.animatedText)
        }

        prepareUSerData()

        checkCalorieGoal()

        //Seekbar Logic
        binding.calorieSeekBar.isEnabled = false
        binding.calorieSeekBar.thumb = null
        val seekbarDuration = 1
        maxProg = getCalories(db.mealDao().getAllMeals())
        handler = Handler(Looper.getMainLooper())

        runnable = object : Runnable {
            override fun run() {
                if(progress < maxProg) {
                    binding.calorieSeekBar.progress = progress
                    binding.calorieText.text = "${maxProg} / $caloricGoal kcal"
                    progress += 3

                    // I Added this check for confetti
                    if (progress >= maxProg && maxProg >= caloricGoal) {
                        CommonConfetti.rainingConfetti(
                            binding.root,
                            intArrayOf(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        ).oneShot()
                    }

                    handler.postDelayed(this, (seekbarDuration / maxProg).toLong())
                }
            }
        }
        handler.post(runnable)


        binding.profileButton.setOnClickListener {
            val intent = Intent(this, PersonActivity::class.java)
            startActivity(intent)
        }

        binding.reportButton.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.layoutBreakfast -> openMealActivity("Breakfast")
                R.id.layoutLunch -> openMealActivity("Lunch")
                R.id.layoutDinner -> openMealActivity("Dinner")
                R.id.layoutSnack -> openMealActivity("Snack")
            }
        }

        binding.layoutBreakfast.setOnClickListener(clickListener)
        binding.layoutLunch.setOnClickListener(clickListener)
        binding.layoutDinner.setOnClickListener(clickListener)
        binding.layoutSnack.setOnClickListener(clickListener)

        binding.addWaterButton.setOnClickListener {

            if (waterCount < 8) {
                val nextGlass = waterGlasses[waterCount]

                // Sound File
                waterSound.start()

                // Animation
                YoYo.with(Techniques.Bounce)
                    .duration(1000)  // Increased duration
                    .withListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {}
                        override fun onAnimationEnd(animation: Animator) {
                            YoYo.with(Techniques.RubberBand)
                                .duration(800)
                                .playOn(nextGlass)
                        }

                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    })
                    .playOn(nextGlass)

                if (waterCount < 8) {
                    waterCount++
                    updateWaterDisplay()
                    saveWaterCount()
                }
            }
        }

        binding.addWaterButton.setOnLongClickListener { // Long click to reset it just in case
            waterCount = 0
            createToastMessage("Reset Water Counter")
            updateWaterDisplay()
            saveWaterCount()
            true
        }

        binding.addExerciseButton.setOnLongClickListener { // Long click to reset it just in case
            totalCaloriesBurned = 0
            activeMinutes = 0
            createToastMessage("Reset Exercise Counter")
            updateExerciseStats()
            true
        }

        //Dialog
        binding.addExerciseButton.setOnClickListener{
            customDialog.show()
        }
        createDialog()

        // Water
        // shared pref - >  like a mini database for simple values -> used to save
        val prefs = getSharedPreferences("WaterPrefs", Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        val lastDate = prefs.getString("lastDate", "")

        // Reset count if it's a new day
        waterCount = if (today == lastDate) {
            prefs.getInt("waterCount", 0)
        } else {
            0
        }

        // Initialize water glass ImageViews
        waterGlasses = listOf(
            findViewById(R.id.glass1),
            findViewById(R.id.glass2),
            findViewById(R.id.glass3),
            findViewById(R.id.glass4),
            findViewById(R.id.glass5),
            findViewById(R.id.glass6),
            findViewById(R.id.glass7),
            findViewById(R.id.glass8)
        )
        updateWaterDisplay()

        // Title
        // Today's date formatted
        val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        val currentDate = sdf.format(Date())

        // Set the title
        binding.currentDate.text = currentDate

        loadExerciseStats()

    }

    override fun onResume() {
        super.onResume()

        checkAndResetDailyValues()

        val mealList = db.mealDao().getAllMeals()
        maxProg = getCalories(mealList)
        progress = 0
        setCaloriesForMeals(mealList)
        setMacroTypeForMeals(mealList)
        checkCalorieGoal()
        handler.post(runnable)
    }

    fun openMealActivity(mealType: String) {
        val intent = Intent(this, MealActivity::class.java)
        intent.putExtra("MEAL_TYPE", mealType)
        startActivity(intent)
    }

    // Functions
    fun displayRecipes(recipes: MutableList<Recipe>) {
        adapter = RecipeCustomRecyclerViewAdapter(this,recipes)
        binding.recipeRecyclerView.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    fun getData(){
        if(db.recipeDao().getAllRecipes().isNotEmpty()){
            displayRecipes(db.recipeDao().getAllRecipes())
        }
        else{
            binding.recipeRecyclerView.adapter = null
        }
    }

    private fun fetchRecipes() {
        recipeService = ApiClient2.getClient().create(RecipeService::class.java)
        val request = recipeService.getRecipes()
        request.enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful && response.body() != null) {
                    val recipes = response.body()!!
                    db.recipeDao().insertAllRecipes(recipes) // Save to Room
                    getData()
                    //updateRecyclerView(db.recipeDao().getAllRecipes()) // Load from Room
                } else {
                    showErrorToast("Failed to fetch recipes. Error code: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                showErrorToast("Failed to fetch recipes: ${t.message}")
            }
        })
    }
    private fun updateRecyclerView(recipes: List<Recipe>) {
        adapter?.apply {
            recyclerItemValues.clear()
            recyclerItemValues.addAll(recipes)
            notifyDataSetChanged()
        }
    }
    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun createDialog() {
        customDialog = Dialog(this)
        customDialog.setContentView(bindingForDialog.root)

        // Setup RecyclerView with adapter
        val recyclerView = bindingForDialog.exerciseRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup search functionality
        bindingForDialog.searchExercise.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                exerciseAdapter?.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Display exercises from database
        if(db.exerciseDao().getAllExercises().isNotEmpty()) {
            displayExercises(db.exerciseDao().getAllExercises())
        } else {
            prepareExerciseData()
            displayExercises(db.exerciseDao().getAllExercises())
        }



        bindingForDialog.addButton.setOnClickListener {
            selectedExercise?.let { exercise ->
                exerciseTimeMinutes = 30
                val caloriesBurned = (exercise.caloriesPerHour * exerciseTimeMinutes) / 60

                // Subtract calories burned from the current maxProg
                maxProg = max(maxProg - caloriesBurned, 0)

                // Update the SeekBar and Text
                animateSeekBarChange(binding.calorieSeekBar.progress, maxProg)



                // Update totals
                totalCaloriesBurned += caloriesBurned
                activeMinutes += exerciseTimeMinutes
                saveExerciseStats()

                // Update UI
                updateExerciseStats()
                createToastMessage("Added ${exercise.name}")
                // Clear selection
                clearExerciseDialog()
            }
        }

        bindingForDialog.cancelButton.setOnClickListener {
            // Clear selection
            clearExerciseDialog()
        }

        customDialog.setOnDismissListener(){
            // Clear selection
            clearExerciseDialog()
        }
    }

    private fun setCaloriesForMeals(mealList: List<Meal>){
        var sumBreakfast = 0
        var sumLunch = 0
        var sumDinner = 0
        var sumSnack = 0
        for(meal in mealList){
            when(meal.mealType){
                "Breakfast" -> sumBreakfast += meal.calories;
                "Lunch" -> sumLunch += meal.calories;
                "Dinner" -> sumDinner += meal.calories;
                "Snack" -> sumSnack += meal.calories;
            }
        }
        binding.breakfastCalories.text = sumBreakfast.toString() + " kcal"
        binding.lunchCalories.text = sumLunch.toString() + " kcal"
        binding.dinnerCalories.text = sumDinner.toString() + " kcal"
        binding.snackCalories.text = sumSnack.toString() + " kcal"
    }

    private fun setMacroTypeForMeals(mealList: List<Meal>){
        var sumPro = 0.0
        var sumCarb = 0.0
        var sumFat = 0.0
        for(meal in mealList){
            sumPro += meal.protein
            sumCarb += meal.carbs
            sumFat += meal.fat
        }
        binding.carbStats.text = "Carb: %.2f g".format(sumCarb)
        binding.proteinStats.text = "Protein: %.2f g".format(sumPro)
        binding.fatStats.text = "Fat: %.2f g".format(sumFat)
    }

    private fun clearExerciseDialog(){
        ExerciseCustomRecyclerViewAdapter.setSelectedPosition()
        bindingForDialog.searchExercise.setText("")
        selectedExercise = null
        customDialog.dismiss()
    }

    private fun prepareUSerData(){
        if(db.personDao().getPersonById(1000) == null) {
            val user = Person(1000, "Toprak Kayis", 22, 90.0, 187, 'M', 2500, 85.0)
            db.personDao().insertPerson(user)
        }
    }

    private fun checkCalorieGoal(){
        val person = db.personDao().getPersonById(1000)
            if (person != null) {
                caloricGoal = person.calorieGoal
        }
        binding.calorieText.text = "$maxProg / $caloricGoal kcal"
        binding.calorieSeekBar.max = caloricGoal
    }

    private fun prepareExerciseData() {
        val exercises = ArrayList<Exercise>()
        exercises.add(Exercise(
            name = "Running",
            caloriesPerHour = 600,
            category = "Intense",
            description = "High-impact cardio exercise"
        ))
        exercises.add(Exercise(
            name = "Walking",
            caloriesPerHour = 200,
            category = "Light",
            description = "Low-impact cardio exercise"
        ))
        exercises.add(Exercise(
            name = "Swimming",
            caloriesPerHour = 400,
            category = "Moderate",
            description = "Full-body workout in water"
        ))
        exercises.add(Exercise(
            name = "Cycling",
            caloriesPerHour = 450,
            category = "Moderate",
            description = "Low-impact cardio on bike"
        ))
        exercises.add(Exercise(
            name = "HIIT",
            caloriesPerHour = 700,
            category = "Intense",
            description = "High-intensity interval training"
        ))
        exercises.add(Exercise(
            name = "Yoga",
            caloriesPerHour = 250,
            category = "Light",
            description = "Mind-body exercise for flexibility"
        ))

        db.exerciseDao().insertAllExercises(exercises)
    }

    override fun displayExercise(exercise: Exercise) {
        selectedExercise = exercise
        bindingForDialog.addButton.isEnabled = true
    }

    override fun displayExercises(exercises: MutableList<Exercise>) {
        exerciseAdapter = ExerciseCustomRecyclerViewAdapter(this, exercises)
        bindingForDialog.exerciseRecyclerView.adapter = exerciseAdapter
        exerciseAdapter?.notifyDataSetChanged()
    }

    private fun updateExerciseStats() {
        // Update the exercise section texts
        binding.caloriesBurnedText.text = "Calories Burned: $totalCaloriesBurned kcal"
        binding.activeMinutesText.text = "Active Minutes: $activeMinutes min"
        saveExerciseStats()
    }

    private fun animateSeekBarChange(startValue: Int, endValue: Int, duration: Long = 1000) {
        val step = if (endValue > startValue) (endValue - startValue) / 50 else (startValue - endValue) / 50
        val delay = duration / 50

        val handler = Handler(Looper.getMainLooper())
        var currentValue = startValue

        val runnable = object : Runnable {
            override fun run() {
                if ((endValue > startValue && currentValue < endValue) || (endValue < startValue && currentValue > endValue)) {
                    currentValue = if (endValue > startValue) currentValue + step else currentValue - step
                    if ((endValue > startValue && currentValue > endValue) || (endValue < startValue && currentValue < endValue)) {
                        currentValue = endValue
                    }
                    binding.calorieSeekBar.progress = currentValue
                    binding.calorieText.text = "$currentValue / $caloricGoal kcal"
                    handler.postDelayed(this, delay)
                }
            }
        }
        handler.post(runnable)
    }




    private fun updateWaterDisplay() {
        waterGlasses.forEachIndexed { index, glass ->
            glass.setImageResource(
                if (index < waterCount) R.drawable.baseline_fill_water_drop
                else R.drawable.baseline_empty_water_drop
            )
        }
    }

    private fun saveWaterCount() {
        getSharedPreferences("WaterPrefs", Context.MODE_PRIVATE).edit().apply {
            putInt("waterCount", waterCount)
            putString("lastDate", LocalDate.now().toString())
            apply()
        }
    }

    // Save exercise stats
    private fun saveExerciseStats() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
            putInt(KEY_CALORIES_BURNED, totalCaloriesBurned)
            putInt(KEY_ACTIVE_MINUTES, activeMinutes)
            putString(KEY_LAST_EXERCISE_DATE, LocalDate.now().toString())
            apply()
        }
    }

    // Load exercise stats
    private fun loadExerciseStats() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        val lastDate = prefs.getString(KEY_LAST_EXERCISE_DATE, "") ?: ""

        if (today == lastDate) {
            // Load saved values if it's the same day
            totalCaloriesBurned = prefs.getInt(KEY_CALORIES_BURNED, 0)
            activeMinutes = prefs.getInt(KEY_ACTIVE_MINUTES, 0)
        } else {
            // Reset values if it's a new day
            totalCaloriesBurned = 0
            activeMinutes = 0
        }

        // Update UI with loaded/reset values
        updateExerciseStats()
    }

    // Get meal Calories
    private fun getCalories(meals : MutableList<Meal>):Int{
        var sumOfCal = 0
        if(meals.isNotEmpty())
            for (meal in meals) {
                sumOfCal += meal.calories
            }
        return sumOfCal
    }

    // Create Toast Message
    private fun createToastMessage(str:String) {
        Toast.makeText(this@MainActivity, str, Toast.LENGTH_SHORT).show()
    }

    private fun checkAndResetDailyValues() {
        val prefs = getSharedPreferences(MEAL_PREFS_NAME, Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        val lastDate = prefs.getString(KEY_LAST_MEAL_DATE, "") ?: ""

        if (today != lastDate) {
            // Reset seekbar progress
            progress = 0
            maxProg = 0
            binding.calorieSeekBar.progress = 0
            binding.calorieText.text = "0 / $caloricGoal kcal"

            // Reset meal TextViews
            binding.breakfastCalories.text = "0 kcal"
            binding.lunchCalories.text = "0 kcal"
            binding.dinnerCalories.text = "0 kcal"
            binding.snackCalories.text = "0 kcal"

            // Reset macro stats
            binding.carbStats.text = "Carb: 0.00 g"
            binding.proteinStats.text = "Protein: 0.00 g"
            binding.fatStats.text = "Fat: 0.00 g"

            // Clear all meals from the database for the previous day
            db.mealDao().deleteAllMeals()

            // Save the current date
            prefs.edit().putString(KEY_LAST_MEAL_DATE, today).apply()
        }
    }
}
