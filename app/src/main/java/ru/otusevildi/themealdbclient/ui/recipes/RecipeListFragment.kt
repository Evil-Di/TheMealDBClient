package ru.otusevildi.themealdbclient.ui.recipes

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import ru.otusevildi.themealdbclient.databinding.FragmentRecipesBinding
import ru.otusevildi.themealdbclient.ui.SearchListAdapter
import ru.otusevildi.themealdbclient.ui.categories.CategoriesFragmentDirections

@AndroidEntryPoint
class RecipeListFragment: Fragment() {
    private val category: String get() = RecipeListFragmentArgs.fromBundle(requireArguments()).category
    private val binding = FragmentBindingDelegate<FragmentRecipesBinding>(this)
    private val viewModel: RecipeListViewModel by viewModels()

    init {
        Log.i(TAG, "RecipesFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.bind(
        container,
        FragmentRecipesBinding::inflate
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSearchView()

        val adapter = RecipeListAdapter(requireContext()) { recipeId ->
            Log.i(TAG, "Click receipt $recipeId")
            recipeId?.let {
                findNavController().navigate(RecipeListFragmentDirections.toRecipeFragment(it))
            }
        }

        binding.withBinding {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
            text.text = category
        }

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when(state) {
                    RecipeListViewState.Loading -> Unit
                    is RecipeListViewState.SetData -> adapter.setData(state.list)
                }
            }
        }

        viewModel.selectList(category)
    }

    private fun setSearchView() {
        binding.withBinding {
            searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            searchRecyclerView.visibility = View.INVISIBLE
            searchRecyclerView.adapter = SearchListAdapter { recipeId ->
                searchView.hide()
                recipeId?.let {
                    findNavController().navigate(
                        CategoriesFragmentDirections.toRecipeFragment(
                            recipeId
                        )
                    )
                }
            }

            lifecycleScope.launch {
                viewModel.searchSuggestions.collect {
                    if (it.isNotEmpty() && searchView.editText.text.isNotEmpty()) {
                        (searchRecyclerView.adapter as SearchListAdapter).setData(it)
                    }
                    else {
                        (searchRecyclerView.adapter as SearchListAdapter).setData(emptyList())
                    }
                    searchRecyclerView.visibility = View.VISIBLE
                }
            }

            searchView.editText.doAfterTextChanged {
                if (searchView.editText.text.isNotEmpty()) {
                    viewModel.onSearch(it.toString())
                }
                else {
                    searchRecyclerView.visibility = View.INVISIBLE
                }
            }
        }
    }
}