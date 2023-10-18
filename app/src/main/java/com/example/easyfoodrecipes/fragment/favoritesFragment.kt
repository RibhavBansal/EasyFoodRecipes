package com.example.easyfoodrecipes.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.easyfoodrecipes.activity.MainActivity
import com.example.easyfoodrecipes.activity.MealActivity
import com.example.easyfoodrecipes.adapter.MealsAdapter
import com.example.easyfoodrecipes.databinding.FragmentFavoritesBinding
import com.example.easyfoodrecipes.viewModel.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class favoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var favoritesAdapter: MealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()
        observeFavorites()
        onFavoriteItemClick()

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val currMeal = favoritesAdapter.differ.currentList[pos]
                viewModel.deleteMeal(currMeal)
                Snackbar.make(requireView(),"Meal Deleted", Snackbar.LENGTH_LONG)
                    .setAction(
                        "Undo",
                        View.OnClickListener {
                            viewModel.insertMeal(currMeal)
                        }
                    ).show()
            }
        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvFavorites)
    }

    private fun onFavoriteItemClick() {
        favoritesAdapter.onItemClick = {
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID,it.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME,it.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB,it.strMealThumb)
            startActivity(intent)
        }
    }

    private fun prepareRecyclerView() {
        favoritesAdapter = MealsAdapter()
        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
            adapter = favoritesAdapter
        }
    }

    private fun observeFavorites() {
        viewModel.observeFavoritesMealsListLiveData().observe(
            requireActivity(),
            Observer { meals->
                favoritesAdapter.differ.submitList(meals)
            }
        )
    }

}