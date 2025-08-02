package ru.otusevildi.themealdbclient.ui.categories

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import ru.otusevildi.themealdbclient.R
import ru.otusevildi.themealdbclient.databinding.FragmentCategoriesHostBinding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.TAB_CATEGORIES
import ru.otusevildi.themealdbclient.data.TAB_FAVORITES
import ru.otusevildi.themealdbclient.ui.categories.categoriestab.CategoriesTabFragment
import ru.otusevildi.themealdbclient.ui.categories.favoritestab.FavoritesTabFragment

@AndroidEntryPoint
class CategoriesHostFragment: Fragment() {
    private val binding = FragmentBindingDelegate<FragmentCategoriesHostBinding>(this)
    private val viewModel: CategoriesHostViewModel by viewModels()
    private val onTabSelectedListener = OnTabSelectedListener()

    init {
        Log.i(TAG, "CategoriesHostFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.bind(
        container,
        FragmentCategoriesHostBinding::inflate
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(BindingDestroyer())

        lifecycleScope.launch {
            viewModel.state.observe(viewLifecycleOwner) { state ->
                when(state) {
                    CategoriesHostViewState.Loading -> Unit
                    is CategoriesHostViewState.SelectCategoriesTab -> selectTab(TAB_CATEGORIES, state.hideFavoritesTab)
                    CategoriesHostViewState.SelectFavoritesTab -> selectTab(TAB_FAVORITES, false)
                }
            }
        }

        binding.withBinding {
            tabLayout.root.addOnTabSelectedListener(onTabSelectedListener)
        }
    }

    private fun selectTab(tab: Int, hideFavorites: Boolean) {
        binding.withBinding {
            tabLayout.root.visibility = if (hideFavorites) View.GONE else View.VISIBLE

            if (tab == TAB_FAVORITES) {
                tabLayout.root.selectTab(tabLayout.root.getTabAt(TAB_FAVORITES))
                childFragmentManager.beginTransaction()
                    .replace(R.id.tabs_host,
                        FavoritesTabFragment.newInstance(
                            viewModel.favoritesPage,
                            ::onFavoritesPage
                        ))
                    //.addToBackStack(null)
                    .commit()
            } else {
                tabLayout.root.selectTab(tabLayout.root.getTabAt(TAB_CATEGORIES))
                childFragmentManager.beginTransaction()
                    .replace(R.id.tabs_host,
                        CategoriesTabFragment.newInstance(
                            viewModel.categoriesPage,
                            ::onCategoriesPage)
                    )
                    //.addToBackStack(null)
                    .commit()
            }
        }
    }

    fun onCategoriesPage(page: Int) {
        viewModel.categoriesPage = page
    }

    fun onFavoritesPage(page: Int) {
        viewModel.favoritesPage = page
    }

    private inner class BindingDestroyer : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            binding.withBinding {
                tabLayout.root.removeOnTabSelectedListener(onTabSelectedListener)
            }
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
}