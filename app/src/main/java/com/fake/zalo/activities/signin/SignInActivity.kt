package com.fake.zalo.activities.signin

import android.graphics.Rect
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.fake.zalo.R
import com.fake.zalo.databinding.ActivitySigninBinding

class SignInActivity : AppCompatActivity() {

    private var _binding: ActivitySigninBinding? = null
    private val binding: ActivitySigninBinding
        get() = requireNotNull(_binding)

    private var isShowPassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        with(binding.edtPhone) {
            setOnFocusChangeListener { v, hasFocus ->
                backgroundTintList = if (hasFocus) {
                    ContextCompat.getColorStateList(this@SignInActivity, R.color.Light_SeparatorColor2)
                } else {
                    ContextCompat.getColorStateList(this@SignInActivity, R.color.cLine1)
                }
            }
            doAfterTextChanged {
                binding.ivClear.isVisible = !it.isNullOrEmpty()
            }
        }
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.ivClear.setOnClickListener {
            binding.edtPhone.setText("")
        }
        binding.edtPassword.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.edtPassword.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.Light_SeparatorColor2)
            } else {
                binding.edtPassword.backgroundTintList = ContextCompat.getColorStateList(this, R.color.cLine1)
            }
        }
        binding.tvHideOrShowPw.setOnClickListener {
            isShowPassword = !isShowPassword
            if (isShowPassword) {
                binding.tvHideOrShowPw.text = "ẩn"
                if (binding.edtPassword.transformationMethod is PasswordTransformationMethod) {
                    binding.edtPassword.transformationMethod = null
                }
            } else {
                binding.tvHideOrShowPw.text = "hiện"
                binding.edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.edtPassword.setSelection(binding.edtPassword.text.length)
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.height
            val keypadHeight = screenHeight - rect.bottom

            Log.d("GT45_x", "keypadHeight = $keypadHeight")
            if (keypadHeight > screenHeight * 0.15) {
                binding.ivNext.translationY = -keypadHeight.toFloat() + 100F
            } else {
                binding.ivNext.translationY = 0f
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}