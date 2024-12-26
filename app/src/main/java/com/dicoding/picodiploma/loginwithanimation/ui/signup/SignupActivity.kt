package com.dicoding.picodiploma.loginwithanimation.ui.signup


import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signView: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signView = ViewModelProvider(
            this, ViewModelFactory(this)
        )[SignupViewModel::class.java]

        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || email.length < 8) {
                binding.passwordEditText.error = "Email format is not correct!"
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 8) {
                binding.passwordEditText.error = "Password must be at least 8 characters!"
                return@setOnClickListener
            }

            loading(true)

            signView.register(name, email, password) { success, message ->
                loading(false)
                if (success) {
                    Toast.makeText(this, "Registration successful: $message", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Registration failed: $message", Toast.LENGTH_SHORT).show()
                }


                if (success && !isFinishing && !isDestroyed) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("You have successfully created an account, now it's time to login!.")
                        setPositiveButton("Login Now") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.signupButton.isEnabled = !isLoading
    }

}