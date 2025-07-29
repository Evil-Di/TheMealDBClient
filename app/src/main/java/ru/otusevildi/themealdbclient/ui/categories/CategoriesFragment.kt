package ru.otusevildi.themealdbclient.ui.categories

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import ru.otusevildi.themealdbclient.R
import ru.otusevildi.themealdbclient.databinding.FragmentCategoriesBinding
import kotlin.math.abs
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.data.RecipeCategory
import ru.otusevildi.themealdbclient.data.TAB_CATEGORIES
import ru.otusevildi.themealdbclient.data.TAB_FAVORITES

@AndroidEntryPoint
class CategoriesFragment: Fragment() {
    private val binding = FragmentBindingDelegate<FragmentCategoriesBinding>(this)
    private val viewModel: CategoriesViewModel by viewModels()
    private lateinit var categoriesAdapter: CategoriesListAdapter
    private lateinit var favoritesAdapter: FavoritesListAdapter
    private val onTabSelectedListener = OnTabSelectedListener()

    private var stateJob: Job? = null

    init {
        Log.i(TAG, "CategoriesFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.bind(
        container,
        FragmentCategoriesBinding::inflate
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(BindingDestroyer())
        setCategoriesViewPager()
        setFavoritesViewPager()

        stateJob?.cancel()
        stateJob = lifecycleScope.launch {
            viewModel.state.collect { state ->
                when(state) {
                    CategoriesViewState.Loading -> Unit
                    //is CategoriesViewState.SetData -> setData(state.categories, state.recipes)
                    //is CategoriesViewState.SelectTab -> selectTab(state.tab)
                    is CategoriesViewState.SetData -> setData(state.categories, state.recipes, state.tab)
                }
            }
        }

        binding.withBinding {
            tabLayout.root.addOnTabSelectedListener(onTabSelectedListener)
        }
    }

    private fun setCategoriesViewPager() {
        binding.withBinding {
            tabCategories.root.visibility = View.INVISIBLE
            categoriesAdapter = CategoriesListAdapter(requireContext(), ::onCategoryClick, ::onCategoryInfoClick)
            tabCategories.apply {
                viewPager.adapter = categoriesAdapter
                viewPager.apply {
                    clipChildren = false  // No clipping the left and right items
                    clipToPadding = false  // Show the viewpager in full width without clipping the padding
                    offscreenPageLimit = 3  // Render the left and right items
                    (getChildAt(0) as RecyclerView).overScrollMode =
                        RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect

                    viewPager.registerOnPageChangeCallback(OnCategoryPageChangeCallback)
                    viewPager.setPageTransformer(compositePageTransformer())
                }
            }
        }
    }

    private fun setFavoritesViewPager() {
        binding.withBinding {
            tabFavorites.root.visibility = View.INVISIBLE
            favoritesAdapter = FavoritesListAdapter(requireContext()) { recipeId ->
                Log.i(TAG, "Click receipt $recipeId")
                recipeId?.let {
                    findNavController().navigate(CategoriesFragmentDirections.toRecipeFragment(it))
                }
            }
            tabFavorites.apply {
                viewPager.adapter = favoritesAdapter
                viewPager.apply {
                    clipChildren = false  // No clipping the left and right items
                    clipToPadding = false  // Show the viewpager in full width without clipping the padding
                    offscreenPageLimit = 3  // Render the left and right items
                    (getChildAt(0) as RecyclerView).overScrollMode =
                        RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect

                    viewPager.setPageTransformer(compositePageTransformer())
                }
            }
        }
    }

/*
    private fun setData(categories: List<RecipeCategory>, recipes: List<Recipe>) {
        categoriesAdapter.setData(categories)
        favoritesAdapter.setData(recipes)
    }

    private fun selectTab(tab: Int) {
        binding.withBinding {
            tabLayout.root.selectTab(tabLayout.root.getTabAt(tab))
            if (tab == TAB_FAVORITES) {
                tabCategories.root.visibility = View.INVISIBLE
                tabFavorites.root.visibility = View.VISIBLE
            }
            else {
                tabFavorites.root.visibility = View.INVISIBLE
                tabCategories.root.visibility = View.VISIBLE
            }
        }
    }
*/
    private fun setData(categories: List<RecipeCategory>, favorites: List<Recipe>, tab: Int) {
        binding.withBinding {
            tabLayout.root.visibility = if (favorites.isEmpty()) View.GONE else View.VISIBLE

            if (tab == TAB_FAVORITES && favorites.isNotEmpty()) {
                tabLayout.root.selectTab(tabLayout.root.getTabAt(TAB_FAVORITES))
                tabCategories.root.visibility = View.INVISIBLE
                tabFavorites.root.visibility = View.VISIBLE
                (tabFavorites.viewPager.adapter as FavoritesListAdapter).setData(favorites)
            }
            else {
                tabLayout.root.selectTab(tabLayout.root.getTabAt(TAB_CATEGORIES))
                tabFavorites.root.visibility = View.INVISIBLE
                tabCategories.root.visibility = View.VISIBLE
                (tabCategories.viewPager.adapter as CategoriesListAdapter).setData(categories)
            }
        }
    }

    private fun onCategoryClick(name: String?) {
        Log.i(TAG, "Click category $name")
        name?.let {
            findNavController().navigate(CategoriesFragmentDirections.toRecipesFragment(name))
        }
    }

    private fun onCategoryInfoClick(view: View?) {
        Log.i(TAG, "Click category info")
        binding.withBinding {
            tabCategories.viewPager.apply {
                (getChildAt(0) as RecyclerView)
                    .layoutManager?.findViewByPosition(OnCategoryPageChangeCallback.pageSelected)?.apply {
                    val info = findViewById<TextView>(R.id.info_text)
                    info.visibility = if (info.isInvisible) View.VISIBLE else View.INVISIBLE
                }
            }
        }
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

    private inner class BindingDestroyer : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            binding.withBinding {
                tabCategories.viewPager.unregisterOnPageChangeCallback(OnCategoryPageChangeCallback)
                tabLayout.root.removeOnTabSelectedListener(onTabSelectedListener)
            }
            stateJob?.cancel()
            stateJob = null
        }
    }

    private inner class OnTabSelectedListener : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (tab!= null) {
                viewModel.onTabSelected(tab.position)
            }
        }
        override fun onTabReselected(tab: TabLayout.Tab?) {}
        override fun onTabUnselected(tab: TabLayout.Tab?) {}
    }

    object OnCategoryPageChangeCallback: ViewPager2.OnPageChangeCallback() {
        var pageSelected = 0
        override fun onPageSelected(position: Int) {
            pageSelected = position
            super.onPageSelected(position)
        }
    }
}