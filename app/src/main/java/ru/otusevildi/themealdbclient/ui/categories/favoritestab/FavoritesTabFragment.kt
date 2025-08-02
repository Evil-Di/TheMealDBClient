package ru.otusevildi.themealdbclient.ui.categories.favoritestab

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import kotlin.math.abs
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.databinding.TabFavoritesBinding
import ru.otusevildi.themealdbclient.ui.categories.CategoriesHostFragmentDirections

@AndroidEntryPoint
class FavoritesTabFragment: Fragment() {
    private val binding = FragmentBindingDelegate<TabFavoritesBinding>(this)
    private lateinit var favoritesAdapter: FavoritesListAdapter
    private lateinit var onPageChangeCallback: OnFavoritePageChangeCallback
    private val viewModel: FavoritesTabViewModel by viewModels()
    private var stateJob: Job? = null

    private var onPageSelectedCallback: (page: Int)->Unit = {}

    init {
        Log.i(TAG, "FavoritesFragment")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.pageSelected = it.getInt(SET_PAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.bind(
        container,
        TabFavoritesBinding::inflate
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(BindingDestroyer())
        setFavoritesViewPager()

        stateJob?.cancel()
        stateJob = lifecycleScope.launch {
            viewModel.state.observe(viewLifecycleOwner) { state ->
                when(state) {
                    FavoritesTabViewState.Loading -> Unit
                    is FavoritesTabViewState.SetData -> setData(state.recipes)
                }
            }
        }
    }

    private fun setFavoritesViewPager() {
        binding.withBinding {
            //root.visibility = View.INVISIBLE
            favoritesAdapter = FavoritesListAdapter(requireContext()) { recipeId ->
                Log.i(TAG, "Click receipt $recipeId")
                recipeId?.let {
                    findNavController().navigate(CategoriesHostFragmentDirections.toRecipeFragment(it))
                }
            }
            viewPager.adapter = favoritesAdapter
            viewPager.apply {
                clipChildren = false  // No clipping the left and right items
                clipToPadding = false  // Show the viewpager in full width without clipping the padding
                offscreenPageLimit = 3  // Render the left and right items
                (getChildAt(0) as RecyclerView).overScrollMode =
                    RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect

                onPageChangeCallback = OnFavoritePageChangeCallback()

                viewPager.setPageTransformer(compositePageTransformer())
                viewPager.registerOnPageChangeCallback(onPageChangeCallback)
                viewPager.setCurrentItem(viewModel.pageSelected, false)
            }
        }
    }

    private fun setData(favorites: List<Recipe>) {
        favoritesAdapter.setData(favorites)
        binding.withBinding {
            viewPager.setCurrentItem(viewModel.pageSelected, false)
        }
    }

    fun setOnPageSelectedCallback(onPageSelected: (page: Int)->Unit) {
        this.onPageSelectedCallback = onPageSelected
    }

    private fun compositePageTransformer(): CompositePageTransformer {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer((20 * Resources.getSystem().displayMetrics.density).toInt()))
        transformer.addTransformer { page, position ->
            page.apply {
                val absPos = abs(position)
                scaleY = (0.80f + (1-absPos) * 0.20f)
                translationY = absPos * 120f
                alpha = when {
                    position < -1 -> 0.4f
                    position <= 1 -> 0.5f.coerceAtLeast(1 - absPos)
                    else -> 0.4f
                }
            }
        }
        return transformer
    }

    private inner class OnFavoritePageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.pageSelected = position
            onPageSelectedCallback(viewModel.pageSelected)
        }
    }

    private inner class BindingDestroyer : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            binding.withBinding {
                viewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
            }
            stateJob?.cancel()
            stateJob = null
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(page: Int, onPageSelected: (page: Int)->Unit) =
            FavoritesTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(SET_PAGE, page)
                }
                setOnPageSelectedCallback(onPageSelected)
            }
    }
}

private const val SET_PAGE = "setPage"