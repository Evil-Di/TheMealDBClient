package ru.otusevildi.themealdbclient.ui.recipes

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import ru.otusevildi.themealdbclient.databinding.FragmentRecipesBinding

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
            viewModel.state.observe(viewLifecycleOwner) { state ->
                when(state) {
                    RecipeListViewState.Loading -> Unit
                    is RecipeListViewState.SetData -> adapter.setData(state.list)
                }
            }
        }

        viewModel.selectList(category)
    }
}