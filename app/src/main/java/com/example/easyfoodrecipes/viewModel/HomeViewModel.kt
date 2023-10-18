package com.example.easyfoodrecipes.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.easyfoodrecipes.db.MealDatabase
import com.example.easyfoodrecipes.pojo.*
//import com.bumptech.glide.Glide
import com.example.easyfoodrecipes.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query

class HomeViewModel(
    private val mealDatabase: MealDatabase
) : ViewModel() {
    private var randomMealLiveData = MutableLiveData<Meal>()
    private var popularItemsLiveData = MutableLiveData<List<MealsByCategory>>()
    private var categoriesLiveData = MutableLiveData<List<Category>>()
    private var favoritesMealLiveData = mealDatabase.mealDao().getAllMeals()
    private var bottomSheetMealLiveData = MutableLiveData<Meal>()
    private var searchedMealsLiveData = MutableLiveData<List<Meal>>()

    private var saveStateRandomMeal: Meal? = null
    fun getRandomMeal(){
        saveStateRandomMeal?.let {
            randomMealLiveData.postValue(it)
            return
        }

        RetrofitInstance.api.getRandomMeal().enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if(response.body() != null){
                    val randomMeal: Meal = response.body()!!.meals[0]
                    randomMealLiveData.value = randomMeal
                    saveStateRandomMeal = randomMeal
//                    Glide.with(this@HomeFragment)
//                        .load(randomMeal.strMealThumb)
//                        .into(binding.imgRandomMeal)
//
//                    Log.d("TEST","meal id: ${randomMeal.idMeal}, name: ${randomMeal.strMeal}")
                }
                else{
                    return
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("TEST",t.message.toString())
            }
        })
    }

    fun getPopularItems(){
        RetrofitInstance.api.getPopularItems("Seafood").enqueue(object : Callback<MealsByCategoryList>{
            override fun onResponse(call: Call<MealsByCategoryList>, response: Response<MealsByCategoryList>) {
                if(response.body() != null){
                    popularItemsLiveData.value = response.body()!!.meals
                }
                else{
                    return
                }
            }

            override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                Log.d("TEST",t.message.toString())
            }
        })
    }

    fun getCategories(){
        RetrofitInstance.api.getCategories().enqueue(object : Callback<CategoryList>{
            override fun onResponse(call: Call<CategoryList>, response: Response<CategoryList>) {
                response.body()?.let { categoryList ->
                    categoriesLiveData.postValue(categoryList.categories)
                }
            }

            override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                Log.e("TEST",t.message.toString())
            }
        })
    }

    fun getMealById(id: String){
        RetrofitInstance.api.getMealDetails(id).enqueue(object : Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                val meal = response.body()?.meals?.first()
                meal?.let {meal->
                    bottomSheetMealLiveData.postValue(meal)
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("TEST",t.message.toString())
            }
        })
    }

    fun searchMeals(searchQuery: String){
        RetrofitInstance.api.searchMeals(searchQuery).enqueue(
            object : Callback<MealList>{
                override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                    val mealsList = response.body()?.meals
                    mealsList?.let {
                        searchedMealsLiveData.postValue(it)
                    }
                }

                override fun onFailure(call: Call<MealList>, t: Throwable) {
                    Log.e("TEST",t.message.toString())
                }
            }
        )
    }

    fun insertMeal(meal: Meal){
//        viewModelScope.launch {
        mealDatabase.mealDao().upsert(meal)
//        }
    }

    fun deleteMeal(meal: Meal){
//        viewModelScope.launch {
        mealDatabase.mealDao().delete(meal)
//        }
    }

    fun observeRandomMealLiveData(): LiveData<Meal>{
        return randomMealLiveData
    }

    fun observePopularItemsLiveData(): LiveData<List<MealsByCategory>>{
        return popularItemsLiveData
    }

    fun observeCategoriesListLiveData(): LiveData<List<Category>>{
        return categoriesLiveData
    }

    fun observeFavoritesMealsListLiveData(): LiveData<List<Meal>>{
        return favoritesMealLiveData
    }

    fun observeMealBottomSheetLiveData(): LiveData<Meal> {
        return bottomSheetMealLiveData
    }

    fun observedSearchedMealsLiveData(): LiveData<List<Meal>>{
        return searchedMealsLiveData
    }
}