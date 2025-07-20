package ru.otusevildi.themealdbclient.ui.container.favorites

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import ru.otusevildi.themealdbclient.databinding.FragmentFavoritesBinding
import kotlin.math.abs

@AndroidEntryPoint
class FavoritesFragment: Fragment() {
    private val binding = FragmentBindingDelegate<FragmentFavoritesBinding>(this)
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: FavoritesListAdapter

    init {
        Log.i(TAG, "FavoritesFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.bind(
        container,
        FragmentFavoritesBinding::inflate
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.withBinding {

            adapter = FavoritesListAdapter(requireContext()) {
                Log.i(TAG, "Click")
            }
            viewPager.adapter = adapter
            viewPager.apply {
                clipChildren = false  // No clipping the left and right items
                clipToPadding = false  // Show the viewpager in full width without clipping the padding
                offscreenPageLimit = 3  // Render the left and right items
                (getChildAt(0) as RecyclerView).overScrollMode =
                    RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect

                viewPager.setPageTransformer(compositePageTransformer())
            }
            viewModel.recentList.observe(viewLifecycleOwner) {
                adapter.setData(it)
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
}