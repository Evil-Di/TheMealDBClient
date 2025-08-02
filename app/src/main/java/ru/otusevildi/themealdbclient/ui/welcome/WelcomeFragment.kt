package ru.otusevildi.themealdbclient.ui.welcome

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import ru.otusevildi.themealdbclient.databinding.FragmentWelcomeBinding

@AndroidEntryPoint
class WelcomeFragment : Fragment() {
    private val binding = FragmentBindingDelegate<FragmentWelcomeBinding>(this)
    private val viewModel: WelcomeViewModel by viewModels()

    init {
        Log.i(TAG, "WelcomeFragment")
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,savedInstanceState: Bundle?): View =
        binding.bind(container,FragmentWelcomeBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading()

        binding.withBinding {

            lifecycleScope.launch {
                viewModel.state.collect { state ->
                    when (state) {
                        WelcomeViewState.Loading -> Unit//showLoading()
                        is WelcomeViewState.Done -> showWelcome(view, state)
                        WelcomeViewState.TimedOut -> navigateNext()
                    }
                }
            }
        }

        view.setOnClickListener {
            navigateNext()
        }
    }

    private fun showLoading() {
        binding.withBinding {
            loading.isVisible = true
            image.isVisible = false
            text.isVisible = false
        }
    }

    private fun showWelcome(view: View, data: WelcomeViewState.Done) {
        data.imgLink?.let {
            binding.withBinding {
                Glide.with(requireContext())
                    .load(data.imgLink)
                    /*.listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            loading.isVisible = false
                            image.isVisible = true
                            text.isVisible = true
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            loading.isVisible = false
                            image.isVisible = false
                            text.isVisible = true
                            showError(data.error)
                            return true
                        }
                    })*/
                    .centerCrop()
                    .into(image)
                loading.isVisible = false
                image.isVisible = true
                text.isVisible = true
            }
        }

        data.error?.let {
            showError(data.error)
        }
    }

    private fun showError(e: Exception?) {
        TODO("Not yet implemented")
    }

    private fun navigateNext() {
        findNavController().navigate(WelcomeFragmentDirections.toCategoriesFragment())
    }
}