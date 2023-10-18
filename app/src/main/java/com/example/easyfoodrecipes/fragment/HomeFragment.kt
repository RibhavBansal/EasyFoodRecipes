package com.example.easyfoodrecipes.fragment
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.easyfoodrecipes.R
import com.example.easyfoodrecipes.activity.CategoryMealsActivity
import com.example.easyfoodrecipes.activity.MainActivity
import com.example.easyfoodrecipes.activity.MealActivity
import com.example.easyfoodrecipes.adapter.CategoriesAdapter
import com.example.easyfoodrecipes.adapter.MostPopularAdapter
import com.example.easyfoodrecipes.databinding.FragmentHomeBinding
import com.example.easyfoodrecipes.fragment.bottomSheet.MealBottomSheetFragment
import com.example.easyfoodrecipes.pojo.MealsByCategory
import com.example.easyfoodrecipes.pojo.Meal
import com.example.easyfoodrecipes.viewModel.HomeViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var randomMeal: Meal
    private lateinit var popularItemsAdapter: MostPopularAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    var onLongItemClick:((MealsByCategory)->Unit)? = null

    companion object{
        const val MEAL_ID = "com.example.easyfoodrecipes.fragment.idMeal"
        const val MEAL_NAME = "com.example.easyfoodrecipes.fragment.nameMeal"
        const val MEAL_THUMB = "com.example.easyfoodrecipes.fragment.thumbMeal"
        const val CATEGORY_NAME = "com.example.easyfoodrecipes.fragment.categoryName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        homeMvvm = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel = (activity as MainActivity).viewModel
        popularItemsAdapter = MostPopularAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preparePopularItemsRecyclerView()

        viewModel.getRandomMeal()
        observerRandomMeal()
        onRandomMealClick()
        onRandomMealLongClick()


        viewModel.getPopularItems()
        observerPopularItems()
        onPopularItemClick()
        onPopularItemLongClick()

        prepareCategoriesRecyclerView()
        viewModel.getCategories()
        observerCategories()
        onCategoryClick()

        onSearchIconClick()
    }

    private fun onSearchIconClick() {
        binding.imgSearch.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun onPopularItemLongClick() {
        popularItemsAdapter.onLongItemClick = {meal->
            val mealBottomSheetFragment = MealBottomSheetFragment.newInstance(meal.idMeal)
            mealBottomSheetFragment.show(childFragmentManager,"Meal info")
        }
    }

    private fun onCategoryClick() {
        categoriesAdapter.onItemClick = { category ->
            val intent = Intent(activity,CategoryMealsActivity::class.java)
            intent.putExtra(CATEGORY_NAME,category.strCategory)
            startActivity(intent)
        }
    }

    private fun prepareCategoriesRecyclerView() {
        categoriesAdapter = CategoriesAdapter()
        binding.recViewCategories.apply {
            layoutManager = GridLayoutManager(context,3,GridLayoutManager.VERTICAL,false)
            adapter = categoriesAdapter
        }
    }

    private fun observerCategories() {
        viewModel.observeCategoriesListLiveData().observe(
            viewLifecycleOwner,
            Observer { categories->
                categoriesAdapter.setCategoryList(categories)
            }
        )
    }

    private fun onPopularItemClick() {
        popularItemsAdapter.onItemClick = {
            val intent = Intent(activity,MealActivity::class.java)
            intent.putExtra(MEAL_ID,it.idMeal)
            intent.putExtra(MEAL_NAME,it.strMeal)
            intent.putExtra(MEAL_THUMB,it.strMealThumb)
            startActivity(intent)
        }
    }

    private fun preparePopularItemsRecyclerView() {
        binding.recViewMealsPopular.apply {
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
            adapter = popularItemsAdapter
        }
    }

    private fun observerPopularItems() {
        viewModel.observePopularItemsLiveData().observe(
            viewLifecycleOwner
        ) {mealList->
            popularItemsAdapter.setMeals(mealsList = mealList as ArrayList<MealsByCategory>)
        }
    }

    private fun onRandomMealClick() {
        binding.randomMealCard.setOnClickListener{
            val intent = Intent(activity,MealActivity::class.java)
            intent.putExtra(MEAL_ID,randomMeal.idMeal)
            intent.putExtra(MEAL_NAME,randomMeal.strMeal)
            intent.putExtra(MEAL_THUMB,randomMeal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun onRandomMealLongClick() {
        binding.randomMealCard.setOnLongClickListener{
            val mealBottomSheetFragment = MealBottomSheetFragment.newInstance(randomMeal.idMeal)
            mealBottomSheetFragment.show(childFragmentManager,"Meal info")
            true
        }
    }

    private fun observerRandomMeal() {
        viewModel.observeRandomMealLiveData().observe(
            viewLifecycleOwner,
            object: Observer<Meal>{
                override fun onChanged(t: Meal?) {
                    Glide.with(this@HomeFragment)
                        .load(t!!.strMealThumb)
                        .into(binding.imgRandomMeal)

                    this@HomeFragment.randomMeal = t
                }
            }
        )
    }
}