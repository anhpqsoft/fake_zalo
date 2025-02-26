package com.fake.zalo.activities.signin

import android.content.Intent
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
import com.fake.zalo.activities.chat.ChatActivity
import com.fake.zalo.databinding.ActivitySigninBinding
import com.fake.zalo.ultis.getNavigationBarHeight
import com.fake.zalo.ultis.getStatusBarHeight
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private var _binding: ActivitySigninBinding? = null
    private val binding: ActivitySigninBinding
        get() = requireNotNull(_binding)

    private var isShowPassword: Boolean = false
    private var isRunningSignIn: Boolean = false

    private val statusBarHeight: Int by lazy {
        getStatusBarHeight(this)
    }
    private val navigationBarHeight: Int by lazy {
        getNavigationBarHeight(this, binding.root)
    }
    private var heightStatusBar: Int = -1

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
        binding.ivNext.isEnabled = false
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

                val phone = binding.edtPhone.text.toString().trim()
                val password = binding.edtPassword.text.toString().trim()

                if (phone.isNotEmpty() && password.isNotEmpty()) {
                    binding.ivNext.isEnabled = true
                    binding.ivNext.setImageResource(R.drawable.ic_next_active)
                } else {
                    binding.ivNext.isEnabled = false
                    binding.ivNext.setImageResource(R.drawable.ic_next_default)
                }
            }
        }
        binding.edtPassword.doAfterTextChanged {
            val phone = binding.edtPhone.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (phone.isNotEmpty() && password.isNotEmpty()) {
                binding.ivNext.isEnabled = true
                binding.ivNext.setImageResource(R.drawable.ic_next_active)
            } else {
                binding.ivNext.isEnabled = false
                binding.ivNext.setImageResource(R.drawable.ic_next_default)
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
            if (heightStatusBar == -1) {
                heightStatusBar = statusBarHeight
                Log.d("GT45_x", "height status bar = $heightStatusBar")
                binding.header.setPadding(0, heightStatusBar, 0, 0)
            }

            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                binding.ivNext.translationY = -keypadHeight.toFloat() + navigationBarHeight
            } else {
                binding.ivNext.translationY = 0f
            }
        }

        binding.ivNext.setOnClickListener {
            if (isRunningSignIn) return@setOnClickListener

            binding.tvError.isVisible = false
            val phone = binding.edtPhone.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (phone.isEmpty() || password.isEmpty()) return@setOnClickListener
            if (phone.length < 10) {
                isRunningSignIn = false
                setError("Số điện thoại không hợp lệ\nVui lòng kiểm tra và thử lai.(2001)")
                return@setOnClickListener
            }
            isRunningSignIn = true
            val db = Firebase.firestore
            db.collection("users")
                .whereEqualTo("phone", phone)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    isRunningSignIn = false
                    if (result.isEmpty) {
                        setError("Mật khẩu không đúng\nVui lòng kiểm tra và thử lại.(2017)")
                    } else {
                        val id = result.documents.firstOrNull()?.id
                        val intent = Intent(this, ChatActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("current_id", id)
                        intent.putExtra("phone", phone)
                        startActivity(intent)
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    isRunningSignIn = false
                    setError("Mật khẩu không đúng\nVui lòng kiểm tra và thử lại.(2017)")
                }
        }
    }

    private fun setError(msg: String) {
        binding.tvError.isVisible = true
        binding.edtPhone.requestFocus()
        binding.edtPhone.setSelection(binding.edtPhone.text.trim().length)
        binding.tvError.text = msg
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}