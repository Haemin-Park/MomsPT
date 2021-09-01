package com.fitsionary.momspt.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.fitsionary.momspt.presentation.loading.view.LoadingDialogFragment
import com.fitsionary.momspt.presentation.loading.view.LoadingDialogFragment.Companion.LOADING_DIALOG_FRAGMENT_TAG
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseFragment<B : ViewDataBinding, VM : ViewModel>(@LayoutRes private val layoutResId: Int) :
    Fragment() {
    protected lateinit var binding: B
    protected abstract val viewModel: VM
    private var loadingDialogFragment: LoadingDialogFragment? = null
    protected val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    protected fun onBackPressed() {
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        } else {
            activity?.finish()
        }
    }

    fun showLoading() {
        activity?.runOnUiThread {
            if (loadingDialogFragment != null || (loadingDialogFragment?.dialog)?.isShowing == true) {
                return@runOnUiThread
            } else {
                loadingDialogFragment = LoadingDialogFragment.newInstance()
                loadingDialogFragment?.show(childFragmentManager, LOADING_DIALOG_FRAGMENT_TAG)
            }
        }
    }

    fun hideLoading() {
        activity?.runOnUiThread {
            (childFragmentManager.findFragmentByTag(LOADING_DIALOG_FRAGMENT_TAG) as? LoadingDialogFragment)?.dismiss()
            loadingDialogFragment = null
        }
    }

    protected fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}