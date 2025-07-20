package ru.otusevildi.themealdbclient.ui.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import ru.otusevildi.themealdbclient.R
import ru.otusevildi.themealdbclient.data.TAB_CATEGORIES
import ru.otusevildi.themealdbclient.data.TAB_FAVORITES
import ru.otusevildi.themealdbclient.databinding.FragmentContainerBinding

@AndroidEntryPoint
class ContainerFragment : Fragment() {
    private val binding = FragmentBindingDelegate<FragmentContainerBinding>(this)
    private val viewModel: ContainerViewModel by viewModels()
    private var destinationChangedCallback: OnDestinationChanged? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  = binding.bind(container, FragmentContainerBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(BindingDestroyer())

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    ContainerViewState.None -> Unit
                    is ContainerViewState.SelectTab -> selectTab(state.tab)
                }
            }
        }
    }

    private fun selectTab(tab: Int) {
        val navController = getNavController()
        if (navController.currentDestination == null) {
            lifecycleScope.launch {
                when (tab) {
                    TAB_CATEGORIES -> createNavGraph(R.id.categoriesFragment)
                    TAB_FAVORITES -> createNavGraph(R.id.favoritesFragment)
                }
            }
            binding.withBinding {
                tabLayout.selectTab(tabLayout.getTabAt(tab))
                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        if (tab!= null) {
                            viewModel.onTabSelected(tab.position)
                        }
                    }
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                })
            }
        }
        else {
            when (tab) {
                TAB_CATEGORIES -> {
                    if (navController.currentDestination!!.id != R.id.categoriesFragment) {
                            navController.navigate(R.id.to_categoriesFragment)
                        }
                    }

                TAB_FAVORITES -> {
                    if (navController.currentDestination!!.id != R.id.favoritesFragment) {
                        navController.navigate(R.id.to_favoritesFragment)
                    }
                }
            }
        }
    }

    private fun getNavController(): NavController {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.secondary_container_view) as NavHostFragment
        return navHostFragment.navController
    }

    private fun createNavGraph(startDestId: Int) {
        val navController = getNavController()
        val navGraph = navController.navInflater.inflate(R.navigation.secondary_graph)
        navGraph.setStartDestination(startDestId)
        navController.graph = navGraph
        destinationChangedCallback = OnDestinationChanged()
        navController.addOnDestinationChangedListener(destinationChangedCallback!!)
    }

    private inner class OnDestinationChanged: NavController.OnDestinationChangedListener {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: androidx.savedstate.SavedState?
        ) {
            binding.withBinding {
                tabLayout.visibility =
                    if (destination.id == R.id.categoriesFragment ||
                        destination.id == R.id.favoritesFragment)
                        View.VISIBLE else View.GONE
            }
        }
    }

    private inner class BindingDestroyer : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            binding.withBinding {
                destinationChangedCallback?.let {
                    getNavController().removeOnDestinationChangedListener(it)
                }
            }
        }
    }
}