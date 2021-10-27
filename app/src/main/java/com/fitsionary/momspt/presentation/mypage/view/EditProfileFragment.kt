package com.fitsionary.momspt.presentation.mypage.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentEditProfileBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.binding.setCircleImageFromImageUrl
import com.fitsionary.momspt.presentation.mypage.viewmodel.EditProfileViewModel
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class EditProfileFragment :
    BaseFragment<FragmentEditProfileBinding, EditProfileViewModel>(R.layout.fragment_edit_profile) {
    val safeArgs: EditProfileFragmentArgs by navArgs()
    private var originalNickname = ""
    override val viewModel: EditProfileViewModel by lazy {
        ViewModelProvider(this, EditProfileViewModel.ViewModelFactory(originalNickname)).get(
            EditProfileViewModel::class.java
        )
    }
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var selectedImg: Uri? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myPageInfo = safeArgs.myPageInfo
        originalNickname = myPageInfo.nickname
        binding.myPageInfo = myPageInfo
        binding.vm = viewModel

        viewModel.nicknameValidationCheck()

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    selectedImg = data?.data
                    setCircleImageFromImageUrl(binding.ivEditMypageProfile, selectedImg.toString())
                }
            }

        binding.ivEditMypageProfile.setOnClickListener {
            selectImgFromGallery()
        }

        binding.etEditMypageNickname.setRightTextClickListener {
            viewModel.nicknameValidationCheck()
        }

        binding.containerEdit.setOnTouchListener { _, _ ->
            hideKeyboard(activity)
            true
        }

        // 닉네임 수정 시 다시 유효성 검사 하도록
        viewModel.nickname.observe(viewLifecycleOwner, {
            viewModel.validationResultText.value = ""
        })

        binding.btnEditFinish.setOnClickListener {
            selectedImg?.let {
                val disposable = Single.just(createCopyAndReturnRealPath(it))
                    .subscribe({ path ->
                        path?.let { file ->
                            viewModel.editProfileImage(File(file))
                        }
                    }, {})
                addDisposable(disposable)
            }
            viewModel.editProfile(
                binding.etEditMypageNickname.getInputText(),
                binding.etEditMypageBabyName.getInputText()
            )
        }

        viewModel.event.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { editProfileResult ->
                if (editProfileResult) {
                    showToast(getString(R.string.edit_profile_success))
                    findNavController().navigateUp()
                } else {
                    showToast(getString(R.string.edit_profile_fail))
                }
            }
        })
    }

    private fun selectImgFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        resultLauncher.launch(intent)
    }

    private fun createCopyAndReturnRealPath(
        uri: Uri
    ): String? {
        val contentResolver: ContentResolver = requireContext().contentResolver ?: return null

        // Create file path inside app's data dir
        val filePath: String = (requireContext().dataDir.toString() + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return file.absolutePath
    }

    private fun hideKeyboard(activity: Activity?) {
        activity?.run {
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}