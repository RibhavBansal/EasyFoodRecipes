package com.example.easyfoodrecipes.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.easyfoodrecipes.R
import com.example.easyfoodrecipes.adapter.CategoryMealsAdapter
import com.example.easyfoodrecipes.databinding.ActivityCategoryMealsBinding
import com.example.easyfoodrecipes.fragment.HomeFragment
import com.example.easyfoodrecipes.viewModel.CategoryMealsViewModel

class CategoryMealsActivity : AppCompatActivity() {
    lateinit var binding: ActivityCategoryMealsBinding
    lateinit var categoryMealsMvvm: CategoryMealsViewModel
    lateinit var categoryMealsAdapter: CategoryMealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryMealsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryMealsAdapter = CategoryMealsAdapter()
        prepareRecyclerView()

        categoryMealsMvvm = ViewModelProvider(this)[CategoryMealsViewModel::class.java]
        categoryMealsMvvm.getMealsByCategory(intent.getStringExtra(HomeFragment.CATEGORY_NAME)!!)
        categoryMealsMvvm.observeMealsLiveData().observe(this, Observer { mealsList ->
            binding.tvCategoryCount.text = "Count : ${mealsList.size.toString()}"
            categoryMealsAdapter.setMealsList(mealsList)
        })

        onCategoryItemClick()
    }

    private fun onCategoryItemClick() {
        categoryMealsAdapter.onItemClick = {
            val intent = Intent(this,MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID,it.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME,it.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB,it.strMealThumb)
            startActivity(intent)
        }
    }


    private fun prepareRecyclerView() {
        binding.rvMeals.apply {
            layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
            adapter = categoryMealsAdapter
        }
    }
}