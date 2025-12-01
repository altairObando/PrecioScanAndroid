package com.nacatamalitosoft.precioscan

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.nacatamalitosoft.precioscan.lib.ApiService
import com.nacatamalitosoft.precioscan.lib.createRetrofit
import com.nacatamalitosoft.precioscan.lib.helpers.Result
import com.nacatamalitosoft.precioscan.lib.helpers.TokenManager
import com.nacatamalitosoft.precioscan.lib.helpers.safeApiCall
import com.nacatamalitosoft.precioscan.models.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val api by lazy { createRetrofit(this).create(ApiService::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnLogin = findViewById<Button>(R.id.btnlogin);
        btnLogin.setOnClickListener {
            lifecycleScope.launch{
                onLoginPress()
            }
        }
    }

    suspend fun onLoginPress(){
        val btnLogin = findViewById<Button>(R.id.btnlogin);
        val progress = findViewById<ProgressBar>(R.id.loginProgressBar);
        val email = findViewById<EditText>(R.id.useremail).text.toString();
        val pass  = findViewById<EditText>(R.id.userpassword).text.toString();
        if( email.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Email or password is empty", Toast.LENGTH_SHORT).show()
        }
        btnLogin.isEnabled = false;
        btnLogin.alpha = 0.5f;
        progress.visibility = ProgressBar.VISIBLE;
        val request = LoginRequest(email, pass);
        when( val response = safeApiCall { api.login(request) }){
            is Result.Success -> {
                Toast.makeText(this, "Welcome ${response.data.user.username}", Toast.LENGTH_SHORT).show()
                TokenManager.saveTokens(this, response.data.tokens.access, response.data.tokens.refresh)
            }
            is Result.Error -> {
                if(response.code == 401) TokenManager.clearTokens(this)
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            }
        }
        btnLogin.isEnabled = true;
        btnLogin.alpha = 1f;
        progress.visibility = ProgressBar.INVISIBLE;
    }
}