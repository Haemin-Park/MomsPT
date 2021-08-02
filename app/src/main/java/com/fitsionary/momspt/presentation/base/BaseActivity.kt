package com.fitsionary.momspt.presentation.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.fitsionary.momspt.presentation.loading.view.LoadingFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(@LayoutRes private val layoutResId: Int) :
    AppCompatActivity() {
    protected lateinit var binding: B
    protected abstract val viewModel: VM
    private var loadingFragment: LoadingFragment? = null
    protected val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    fun showLoading() {
        this.runOnUiThread {
            if (loadingFragment != null || (loadingFragment?.dialog)?.isShowing == true) {
                return@runOnUiThread
            } else {
                loadingFragment = LoadingFragment.newInstance()
                loadingFragment?.show(supportFragmentManager, "LOADING_FRAGMENT")
            }
        }

        this.runOnUiThread {
            if (loadingFragment != null || (loadingFragment?.dialog)?.isShowing == true) {
                return@runOnUiThread
            } else {
                loadingFragment = LoadingFragment.newInstance()
                loadingFragment?.show(supportFragmentManager, "LOADING_FRAGMENT")
            }
        }
    }

    fun hideLoading() {
        this.runOnUiThread {
            (supportFragmentManager.findFragmentByTag("LOADING_FRAGMENT") as? LoadingFragment)?.dismiss()
            loadingFragment = null
        }
    }

    protected fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}