package com.example.easyfoodrecipes.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.easyfoodrecipes.R
import com.example.easyfoodrecipes.activity.MainActivity
import com.example.easyfoodrecipes.activity.MealActivity
import com.example.easyfoodrecipes.adapter.MealsAdapter
import com.example.easyfoodrecipes.databinding.FragmentSearchBinding
import com.example.easyfoodrecipes.viewModel.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var searchRecyclerviewAdapter: MealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prepareRecyclerView()
        onFavoriteItemClick()
        binding.imgSearchArrow.setOnClickListener {
            searchedMeals()
        }

        observeSearchedMealsLiveData()

        var searchJob: Job? = null
        binding.edSearchBox.addTextChangedListener {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch{
                delay(500)
                viewModel.searchMeals(it.toString())
            }
        }
    }

    private fun searchedMeals(){
        val searchQuery = binding.edSearchBox.text.toString()
        if(searchQuery.isNotEmpty()){
            viewModel.searchMeals(searchQuery)
        }
    }

    private fun prepareRecyclerView() {
        searchRecyclerviewAdapter = MealsAdapter()
        binding.rvSearchedMeals.apply { 
            layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
            adapter = searchRecyclerviewAdapter
        }
    }

    private fun onFavoriteItemClick() {
        searchRecyclerviewAdapter.onItemClick = {
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID,it.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME,it.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB,it.strMealThumb)
            startActivity(intent)
        }
    }

    private fun observeSearchedMealsLiveData(){
        viewModel.observedSearchedMealsLiveData().observe(
            viewLifecycleOwner,
            Observer {
                searchRecyclerviewAdapter.differ.submitList(it)
            }
        )
    }
}