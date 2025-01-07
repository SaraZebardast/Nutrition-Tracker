package com.group20.nutritiontrackingapp.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.group20.nutritiontrackingapp.db.AppDatabase
import com.group20.nutritiontrackingapp.db.Person

class CustomWorkerSavePerson (
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val id = inputData.getInt("id", 0)
        val name = inputData.getString("name") ?: return Result.failure()
        val age = inputData.getInt("age", 0)
        val weight = inputData.getDouble("weight", 0.0)
        val height = inputData.getInt("height", 0)
        val sex = inputData.getString("sex")?.firstOrNull() ?: return Result.failure()
        val calorieGoal = inputData.getInt("calorieGoal", 0)
        val proteinGoal = inputData.getDouble("proteinGoal", 0.0)

        val person = Person(
            id = id,
            name = name,
            age = age,
            weight = weight,
            height = height,
            sex = sex,
            calorieGoal = calorieGoal,
            proteinGoal = proteinGoal
        )

        val db = AppDatabase.getInstance(applicationContext)
        val personDao = db.personDao()

        return try {
            personDao.updatePerson(person)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}