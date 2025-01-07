package com.group20.nutritiontrackingapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.group20.nutritiontrackingapp.util.Constants

@Dao
interface PersonDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(person: Person):Long

    @Update
    fun updatePerson(person: Person):Int

    @Delete
    fun deletePerson(person: Person):Int

    @Query("SELECT * FROM ${Constants.PERSON_TABLE} WHERE id =:id")
    fun getPersonById(id:Int):Person?

    @Query("SELECT * FROM ${Constants.PERSON_TABLE} WHERE name LIKE :name")
    fun getPersonByName(name:String):MutableList<Person>
}