package com.dicoding.picodiploma.loginwithanimation.ui.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.Results
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginView: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginView = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[LoginViewModel::class.java]

        setupView()
        setupAction()
        playAnimation()


        loginView.getUserSession { userSession ->
            userSession?.let {
                binding.emailEditText.setText(userSession.email)
                binding.passwordEditText.setText(userSession.password)
            } ?: run {
                binding.emailEditText.setText("")
                binding.passwordEditText.setText("")
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            if (email.isEmpty() || email.length < 8) {
                binding.passwordEditText.error = "Wrong Format Email!"
                return@setOnClickListener
            }
            val password = binding.passwordEditText.text.toString()

            if (password.isEmpty() || password.length < 8) {
                binding.passwordEditText.error = "Password must be at least 8 characters"
                return@setOnClickListener
            }

            loginView.login(email, password).observe(this) { result ->
                when (result) {
                    is Results.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }

                    is Results.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Results.Success -> {
                        binding.progressBar.visibility = View.GONE
                        result.data
                        loginView.run {
                            saveToken(result.data.token)
                            saveSession(
                                UserModel(
                                    email = email,
                                    token = result.data.token,
                                    password = password,
                                    isLogin = true
                                )
                            )
                        }
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.msg_title))
                            setMessage(getString(R.string.msg_desc))
                            setPositiveButton(getString(R.string.msg_button)) { _, _ ->
                                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }
}