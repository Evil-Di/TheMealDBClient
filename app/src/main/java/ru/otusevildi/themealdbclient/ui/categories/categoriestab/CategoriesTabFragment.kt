package ru.otusevildi.themealdbclient.ui.categories.categoriestab

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import ru.otusevildi.themealdbclient.R
import kotlin.math.abs
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.RecipeCategory
import ru.otusevildi.themealdbclient.databinding.TabCategoriesBinding
import ru.otusevildi.themealdbclient.ui.categories.CategoriesHostFragmentDirections

@AndroidEntryPoint
class CategoriesTabFragment: Fragment() {
    private val binding = FragmentBindingDelegate<TabCategoriesBinding>(this)
    private lateinit var categoriesAdapter: CategoriesListAdapter
    private lateinit var onCategoryPageChangeCallback: OnCategoryPageChangeCallback
    private val viewModel: CategoriesTabViewModel by viewModels()
    private var stateJob: Job? = null

    private var onPageSelectedCallback: (page: Int)->Unit = {}

    init {
        Log.i(TAG, "CategoriesTabFragment")
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
        TabCategoriesBinding::inflate
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(BindingDestroyer())
        setCategoriesViewPager()

        stateJob?.cancel()
        stateJob = lifecycleScope.launch {
            viewModel.state.observe(viewLifecycleOwner) { state ->
                when(state) {
                    CategoriesTabViewState.Loading -> Unit
                    is CategoriesTabViewState.SetData -> setData(state.categories)
                }
            }
        }
    }

    private fun setCategoriesViewPager() {
        binding.withBinding {

            //root.visibility = View.INVISIBLE
            categoriesAdapter = CategoriesListAdapter(requireContext(), ::onCategoryClick, ::onCategoryInfoClick)
            viewPager.adapter = categoriesAdapter
            viewPager.apply {
                clipChildren = false  // No clipping the left and right items
                clipToPadding = false  // Show the viewpager in full width without clipping the padding
                offscreenPageLimit = 3  // Render the left and right items
                (getChildAt(0) as RecyclerView).overScrollMode =
                    RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect

                onCategoryPageChangeCallback = OnCategoryPageChangeCallback(/*viewModel*/)

                viewPager.registerOnPageChangeCallback(onCategoryPageChangeCallback)
                viewPager.setPageTransformer(compositePageTransformer())
                viewPager.setCurrentItem(viewModel.pageSelected, false)

                Log.i(TAG, "CategoriesTabFragment: onViewCreated: viewModel.pageSelected=${viewModel.pageSelected}")
            }
        }
    }

    private fun setData(categories: List<RecipeCategory>) {
        categoriesAdapter.setData(categories)
        binding.withBinding {
            viewPager.setCurrentItem(viewModel.pageSelected, false)
        }
    }

    fun setOnPageSelectedCallback(onPageSelected: (page: Int)->Unit) {
        this.onPageSelectedCallback = onPageSelected
    }

    private fun onCategoryClick(name: String?) {
        Log.i(TAG, "Click category $name")
        name?.let {
            findNavController().navigate(CategoriesHostFragmentDirections.toRecipesFragment(name))
        }
    }

    private fun onCategoryInfoClick(view: View?) {
        Log.i(TAG, "Click category info")
        binding.withBinding {
            viewPager.apply {
                (getChildAt(0) as RecyclerView)
                    .layoutManager?.findViewByPosition(/*onCategoryPageChangeCallback.pageSelected*/viewModel.pageSelected)?.apply {
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
                viewPager.unregisterOnPageChangeCallback(onCategoryPageChangeCallback)
            }
            stateJob?.cancel()
            stateJob = null
        }
    }

    private inner class OnCategoryPageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.pageSelected = position
            //Log.i(TAG, "CategoriesTabFragment: setFragmentResult: PAGE_KEY=${viewModel.pageSelected}")
            onPageSelectedCallback(viewModel.pageSelected)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(page: Int, onPageSelected: (page: Int)->Unit) =
            CategoriesTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(SET_PAGE, page)
                }
                setOnPageSelectedCallback(onPageSelected)
            }
    }
}

private const val SET_PAGE = "setPage"
