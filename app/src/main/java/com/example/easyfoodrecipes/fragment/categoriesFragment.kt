package com.example.easyfoodrecipes.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.easyfoodrecipes.R
import com.example.easyfoodrecipes.activity.CategoryMealsActivity
import com.example.easyfoodrecipes.activity.MainActivity
import com.example.easyfoodrecipes.adapter.CategoriesAdapter
import com.example.easyfoodrecipes.databinding.FragmentCategoriesBinding
import com.example.easyfoodrecipes.viewModel.HomeViewModel

class categoriesFragment : Fragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()
        observeCategories()
        onCategoryClick()
    }

    private fun onCategoryClick() {
        categoriesAdapter.onItemClick = { category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(HomeFragment.CATEGORY_NAME,category.strCategory)
            startActivity(intent)
        }
    }

    private fun observeCategories() {
        viewModel.observeCategoriesListLiveData().observe(
            viewLifecycleOwner,
            Observer {
                categoriesAdapter.setCategoryList(it)
            }
        )
    }

    private fun prepareRecyclerView() {
        categoriesAdapter = CategoriesAdapter()
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
            adapter = categoriesAdapter
        }
    }
}