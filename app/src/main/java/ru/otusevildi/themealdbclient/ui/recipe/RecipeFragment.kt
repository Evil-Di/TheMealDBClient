package ru.otusevildi.themealdbclient.ui.recipe

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
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.databinding.FragmentRecipeBinding
import ru.otusevildi.themealdbclient.ui.SearchListAdapter
import ru.otusevildi.themealdbclient.ui.categories.CategoriesFragmentDirections


@AndroidEntryPoint
class RecipeFragment: Fragment() {
    private val binding = FragmentBindingDelegate<FragmentRecipeBinding>(this)
    private val viewModel: RecipeViewModel by viewModels()
    private val recipeId: String get() = RecipeFragmentArgs.fromBundle(requireArguments()).recipeId

    init {
        Log.i(TAG, "RecipeFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.bind(
        container,
        FragmentRecipeBinding::inflate
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSearchView()

        binding.withBinding {
            lifecycleScope.launch {
                viewModel.recipe.collect { state ->
                    when(state) {
                        RecipeViewState.Loading -> showLoading()
                        is RecipeViewState.Received -> state.recipe?.let { showRecipe(it) }
                    }
                }
            }
            viewModel.selectRecipe(recipeId)
            lifecycleScope.launch {
                viewModel.favorite.collect {
                    favorite.isChecked = it
                }
            }
        }
    }

    private fun showLoading() {}

    private fun showRecipe(recipe: Recipe) {
        binding.withBinding {
            Glide.with(requireContext()).load(recipe.tLink).centerCrop().into(image)
            title.text = recipe.name
            instructionsText.text = recipe.description
            ingredientsSheet.apply {
                i1.text = recipe.i1;    m1.text = recipe.m1
                i2.text = recipe.i2;    m2.text = recipe.m2
                i3.text = recipe.i3;    m3.text = recipe.m3
                i4.text = recipe.i4;    m4.text = recipe.m4
                i5.text = recipe.i5;    m5.text = recipe.m5
                i6.text = recipe.i6;    m6.text = recipe.m6
                i7.text = recipe.i7;    m7.text = recipe.m7
                i8.text = recipe.i8;    m8.text = recipe.m8
                i9.text = recipe.i9;    m9.text = recipe.m9
                i10.text = recipe.i10;  m10.text = recipe.m10
                i11.text = recipe.i11;  m11.text = recipe.m11
                i12.text = recipe.i12;  m12.text = recipe.m12
                i13.text = recipe.i13;  m13.text = recipe.m13
                i14.text = recipe.i14;  m14.text = recipe.m14
                i15.text = recipe.i15;  m15.text = recipe.m15
                i16.text = recipe.i16;  m16.text = recipe.m16
                i17.text = recipe.i17;  m17.text = recipe.m17
                i18.text = recipe.i18;  m18.text = recipe.m18
                i19.text = recipe.i19;  m19.text = recipe.m19
                i20.text = recipe.i20;  m20.text = recipe.m20
            }
            favorite.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setFavorite(recipeId, isChecked)
            }

            recipe.vLink?.let {
                val id = recipe.vLink.substringAfter('=')
                if (id.isNotEmpty()) {
                    youtubePlayer.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        youtubePlayer.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.cueVideo(id, 0F) //loadVideo(id, 0F)
                            }
                        })
                    }
                }
            }
        }
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