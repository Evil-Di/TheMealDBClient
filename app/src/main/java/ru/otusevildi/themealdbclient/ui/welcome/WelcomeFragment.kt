package ru.otusevildi.themealdbclient.ui.welcome

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.otus.basicarchitecture.ui.FragmentBindingDelegate
import ru.otusevildi.themealdbclient.databinding.FragmentWelcomeBinding

@AndroidEntryPoint
class WelcomeFragment : Fragment() {
    private val binding = FragmentBindingDelegate<FragmentWelcomeBinding>(this)
    private val viewModel: WelcomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,savedInstanceState: Bundle?): View =
        binding.bind(container,FragmentWelcomeBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.withBinding {
            text.isVisible = false
            image.isVisible = true
            loading.isVisible = false

            lifecycleScope.launch {
                viewModel.state.collect { state ->
                    when (state) {
                        WelcomeViewState.Loading -> showLoading()
                        is WelcomeViewState.Done -> showWelcome(view, state)
                        WelcomeViewState.TimedOut -> navigateToContainer()
                    }
                }
            }
        }

        view.setOnClickListener {
            navigateToContainer()
        }
    }

    private fun showLoading() {
        binding.withBinding {
            loading.isVisible = true
            image.isVisible = true
            text.isVisible = false
        }
    }

    private fun showWelcome(view: View, data: WelcomeViewState.Done) {
        data.imgLink?.let {
            binding.withBinding {
                Glide.with(view.context)
                    .load(data.imgLink)
                    .listener(object : RequestListener<Drawable> {
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
                            showError(data.error)
                            return true
                        }
                    })
                    .centerCrop()
                    .into(image)

            }
        }

        data.error?.let {
            showError(data.error)
        }
    }

    private fun showError(e: Exception?) {
        binding.withBinding {
            loading.isVisible = false
            image.isVisible = false
            text.isVisible = false
        }
        TODO("Not yet implemented")
    }

    private fun navigateToContainer() {
        findNavController().navigate(WelcomeFragmentDirections.toContainerFragment())
    }
}