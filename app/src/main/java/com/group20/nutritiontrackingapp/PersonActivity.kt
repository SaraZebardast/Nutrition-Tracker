package com.group20.nutritiontrackingapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.group20.nutritiontrackingapp.databinding.ActivityPersonBinding
import com.group20.nutritiontrackingapp.db.AppDatabase
import com.group20.nutritiontrackingapp.db.Person
import com.group20.nutritiontrackingapp.db.PersonDAO
import com.group20.nutritiontrackingapp.util.Constants
import com.group20.nutritiontrackingapp.worker.CustomWorkerSavePerson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PersonActivity : AppCompatActivity() {

    // Variables
    lateinit var binding: ActivityPersonBinding
    private lateinit var person: Person
    private var isFirstTime = true

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

        // Binding Section
        binding = ActivityPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // DB Query
        person = db.personDao().getPersonById(1000)!!
        populateFields(person)

        binding.femaleRb.setOnClickListener{
            binding.imageView3.setImageResource(R.drawable.baseline_person_female_24)
        }

        binding.MaleRb.setOnClickListener{
            binding.imageView3.setImageResource(R.drawable.baseline_person_male_24)
        }

        // Back Button Listener
        binding.backBtn.setOnClickListener {
            if (validateFields()) {
                saveData()
                Toast.makeText(this@PersonActivity, "The data is updating", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    delay(1000) // 1 saniye bekleme
                    finish() // Mevcut aktiviteyi kapatmak için
                }
            }

        }

    }

    private fun populateFields(person: Person) {
        binding.nameTv.setText(person.name)
        binding.AgeTv.setText(person.age.toString())
        binding.WeightTv.setText(person.weight.toString())
        binding.HeightTv.setText(person.height.toString())
        binding.calGoalTv.setText(person.calorieGoal.toString())
        binding.proteinGoalTv.setText(person.proteinGoal.toString())

        // Set gender radio button and image
        if (person.sex == 'F') {
            binding.femaleRb.isChecked = true
            binding.imageView3.setImageResource(R.drawable.baseline_person_female_24)
        } else {
            binding.MaleRb.isChecked = true
            binding.imageView3.setImageResource(R.drawable.baseline_person_male_24)
        }
    }

    private fun validateFields(): Boolean {
        binding.apply {
            if (nameTv.text.isBlank()) {
                nameTv.error = "Name required"
                return false
            }
            if (AgeTv.text.isBlank()) {
                AgeTv.error = "Age required"
                return false
            } else if (AgeTv.text.toString().toIntOrNull() == null) {
                AgeTv.error = "Age must be a number"
                return false
            }
            if (WeightTv.text.isBlank()) {
                WeightTv.error = "Weight required"
                return false
            } else if (WeightTv.text.toString().toDoubleOrNull() == null) {
                WeightTv.error = "Weight must be a number"
                return false
            }
            if (HeightTv.text.isBlank()) {
                HeightTv.error = "Height required"
                return false
            } else if (HeightTv.text.toString().toIntOrNull() == null) {
                HeightTv.error = "Height must be a number"
                return false
            }
            if (calGoalTv.text.isBlank()) {
                calGoalTv.error = "Calorie goal required"
                return false
            } else if (calGoalTv.text.toString().toIntOrNull() == null) {
                calGoalTv.error = "Calorie goal must be a number"
                return false
            }
            if (proteinGoalTv.text.isBlank()) {
                proteinGoalTv.error = "Protein goal required"
                return false
            } else if (proteinGoalTv.text.toString().toDoubleOrNull() == null) {
                proteinGoalTv.error = "Protein goal must be a number"
                return false
            }
            if (!femaleRb.isChecked && !MaleRb.isChecked) {
                Toast.makeText(this@PersonActivity, "Please select gender", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun saveData() {
        val person = Person(
            id = 1000,
            name = binding.nameTv.text.toString(),
            age = binding.AgeTv.text.toString().toInt(),
            weight = binding.WeightTv.text.toString().toDouble(),
            height = binding.HeightTv.text.toString().toInt(),
            sex = if (binding.femaleRb.isChecked) 'F' else 'M',
            calorieGoal = binding.calGoalTv.text.toString().toInt(),
            proteinGoal = binding.proteinGoalTv.text.toString().toDouble()
        )
        val workManager = WorkManager.getInstance(this)
        val inputData = Data.Builder()
            .putInt("id", person.id)
            .putString("name", person.name)
            .putInt("age", person.age)
            .putDouble("weight", person.weight)
            .putInt("height", person.height)
            .putString("sex", person.sex.toString())
            .putInt("calorieGoal", person.calorieGoal)
            .putDouble("proteinGoal", person.proteinGoal)
            .build()
        val savePersonWork = OneTimeWorkRequestBuilder<CustomWorkerSavePerson>()
            .setInputData(inputData)
            .build()

        workManager.enqueue(savePersonWork)
        Log.d("PersonActivity", "Worker triggered to save person")
    }
}