package com.fitsionary.momspt.presentation.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.fitsionary.momspt.presentation.loading.view.LoadingDialogFragment
import com.fitsionary.momspt.presentation.loading.view.LoadingDialogFragment.Companion.LOADING_DIALOG_FRAGMENT_TAG
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseActivity<B : ViewDataBinding, VM : ViewModel>(@LayoutRes private val layoutResId: Int) :
    AppCompatActivity() {
    protected lateinit var binding: B
    protected abstract val viewModel: VM
    private var loadingDialogFragment: LoadingDialogFragment? = null
    protected val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this
    }

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    fun showLoading() {
        this.runOnUiThread {
            if (loadingDialogFragment != null || (loadingDialogFragment?.dialog)?.isShowing == true) {
                return@runOnUiThread
            } else {
                loadingDialogFragment = LoadingDialogFragment.newInstance()
                loadingDialogFragment?.show(supportFragmentManager, LOADING_DIALOG_FRAGMENT_TAG)
            }
        }
    }

    fun hideLoading() {
        this.runOnUiThread {
            (supportFragmentManager.findFragmentByTag(LOADING_DIALOG_FRAGMENT_TAG) as? LoadingDialogFragment)?.dismiss()
            loadingDialogFragment = null
        }
    }

    protected fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}