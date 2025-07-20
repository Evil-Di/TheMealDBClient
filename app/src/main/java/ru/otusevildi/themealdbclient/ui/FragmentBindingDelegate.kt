package ru.otus.basicarchitecture.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

/**
 * Binds fragment view-binding
 */
class FragmentBindingDelegate<VB : ViewBinding>(private val fragment: Fragment) {

    private var binding: VB? = null

    /**
     * Binds fragment view-binding
     * Put inside `onCreateView`
     * See: https://developer.android.com/topic/libraries/view-binding#fragments
     * @param container View container
     * @param inflate Binding inflater
     */
    fun bind(
         container: ViewGroup?,
         inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
    ): View {
        fragment.viewLifecycleOwner.lifecycle.addObserver(BindingDestroyer())
        binding = inflate(fragment.layoutInflater, container, false)
        return binding!!.root
    }

    /**
     * Runs [block] with binding
     */
    fun <R> withBinding(block: VB.() -> R): R {
        return checkNotNull(binding) { "Binding is not initialized" }.block()
    }

    /**
     * Destroys binding on view destroy
     */
    private inner class BindingDestroyer : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            binding = null
        }
    }
}