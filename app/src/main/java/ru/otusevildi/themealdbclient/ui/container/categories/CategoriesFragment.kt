package ru.otusevildi.themealdbclient.ui.container.categories

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
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.ui.container.ContainerFragment

@AndroidEntryPoint
class CategoriesFragment: Fragment() {
    private val binding = FragmentBindingDelegate<FragmentCategoriesBinding>(this)
    private val viewModel: CategoriesViewModel by viewModels()
    private lateinit var adapter: CategoriesListAdapter

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
        setViewPager()
    }

    private fun setViewPager() {
        binding.withBinding {
            adapter = CategoriesListAdapter(requireContext(), ::onItemClick, ::onInfoClick)

            viewPager.adapter = adapter
            viewPager.apply {
                clipChildren = false  // No clipping the left and right items
                clipToPadding = false  // Show the viewpager in full width without clipping the padding
                offscreenPageLimit = 3  // Render the left and right items
                (getChildAt(0) as RecyclerView).overScrollMode =
                    RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect

                viewPager.setPageTransformer(compositePageTransformer())
                viewPager.registerOnPageChangeCallback(OnPageChangeCallback)
            }

            lifecycleScope.launch {
                viewModel.categories.collect { state ->
                    when(state) {
                        CategoriesViewState.None -> Unit
                        is CategoriesViewState.ListReceived -> {
                            adapter.setData(state.list)
                            viewPager.setCurrentItem(OnPageChangeCallback.pageSelected, false)
                        }
                    }
                }
            }
        }
    }

    private fun onItemClick(name: String?) {
        Log.i(TAG, "Click category $name")
        name?.let {
            findNavController().navigate(CategoriesFragmentDirections.toRecipesFragment(name))
        }
    }

    private fun onInfoClick(view: View?) {
        Log.i(TAG, "Click category info")
        binding.withBinding {
            viewPager.apply {
                (getChildAt(0) as RecyclerView)
                    .layoutManager?.findViewByPosition(OnPageChangeCallback.pageSelected)?.apply {
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
                viewPager.unregisterOnPageChangeCallback(OnPageChangeCallback)
            }
        }
    }

    object OnPageChangeCallback: ViewPager2.OnPageChangeCallback() {
        var pageSelected = 0

        override fun onPageSelected(position: Int) {
            pageSelected = position
            super.onPageSelected(position)
        }
    }
}