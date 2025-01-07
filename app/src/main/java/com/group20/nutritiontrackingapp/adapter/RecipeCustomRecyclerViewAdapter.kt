package com.group20.nutritiontrackingapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.group20.nutritiontrackingapp.R
import com.group20.nutritiontrackingapp.RecipeActivity
import com.group20.nutritiontrackingapp.db.Recipe

class RecipeCustomRecyclerViewAdapter(
    private val context: Context,
    val recyclerItemValues: MutableList<Recipe>
) : RecyclerView.Adapter<RecipeCustomRecyclerViewAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recyclerItemValues[position]

        // Bind data to views
        holder.recipeTitle.text = recipe.title
        //holder.recipeImage.setImageResource(recipe.imgId)
        Glide.with(context)
            .load(recipe.imgUrl)
            .into(holder.recipeImage)


        // Show or hide low-calorie icon
        if (recipe.totalCalories < 350) {
            holder.lowCalorieIcon.visibility = View.VISIBLE
        } else {
            holder.lowCalorieIcon.visibility = View.GONE
        }

        // Set click listener for the item
        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeActivity::class.java)
            intent.putExtra("recipe_id", recipe.id) // Pass recipe ID
            intent.putExtra("recipe_title", recipe.title)
            intent.putExtra("recipe_instructions", recipe.instructions)
            intent.putExtra("recipe_calories", recipe.totalCalories)
            //intent.putExtra("recipe_img_url", recipe.imgId)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = recyclerItemValues.size

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val lowCalorieIcon: ImageView = itemView.findViewById(R.id.lowCalorieIcon)
    }
}
