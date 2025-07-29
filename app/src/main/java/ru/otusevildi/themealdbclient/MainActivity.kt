package ru.otusevildi.themealdbclient

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.databinding.ActivityMainBinding
import ru.otusevildi.themealdbclient.ui.SearchListAdapter
import ru.otusevildi.themealdbclient.ui.categories.CategoriesFragmentDirections

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setSearchView(this)

        lifecycleScope.launch {
            viewModel.ingredients.collect { data ->
                if (data != null) {
                    setIngredients(data)
                }
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (!findNavController(R.id.nav_container_view).popBackStack()) {
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        findNavController(R.id.nav_container_view).addOnDestinationChangedListener { _, destination, _ ->
            findViewById<View>(R.id.ingredients_sheet).visibility =
                if (destination.id == R.id.recipeFragment) View.VISIBLE else View.GONE
        }
    }

    private fun setSearchView(context: Context) {
        //val searchRecyclerView = findViewById<RecyclerView>(R.id.search_recycler_view)
        //val searchView = findViewById<com.google.android.material.search.SearchView>(R.id.search_view)
        binding.apply {
            searchRecyclerView.layoutManager = LinearLayoutManager(context)
            searchRecyclerView.visibility = View.INVISIBLE
            searchRecyclerView.adapter = SearchListAdapter { recipeId ->
                searchView.hide()
                recipeId?.let {
                    findNavController(R.id.nav_container_view).navigate(
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

    private fun setIngredients(recipe: Recipe) {
        binding.ingredientsSheet.apply {
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
    }
}