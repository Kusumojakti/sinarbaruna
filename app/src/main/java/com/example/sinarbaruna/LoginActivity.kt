package com.example.sinarbaruna

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.sinarbaruna.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference.getString("token", null)
        val role = sharedPreference.getString("role", null)

        Log.d("LoginActivity", "Token: $token, Role: $role")

        if (token != null && role != null) {
            navigateToRoleActivity(role)
        }

        binding.btnLogin.setOnClickListener {
            verifikasilogin()
        }
    }

    private fun verifikasilogin() {
        val username = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        val jsonObject = JSONObject()
        try {
            jsonObject.put("username", username)
            jsonObject.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        AndroidNetworking.post("http://sinarbaruna.d2l.my.id/api/login")
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        Log.d(ContentValues.TAG, response.toString())
                        if (response.getString("success") == "true") {
                            val user = response.getJSONObject("user")
                            val role = user.getString("role")
                            val shareToken = response.getString("token")

                            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                            val editor = sharedPreference.edit()
                            editor.putString("token", shareToken)
                            editor.putString("role", role)
                            editor.apply()

                            Log.d("LoginActivity", "Saved Token: $shareToken, Role: $role")

                            navigateToRoleActivity(role)
                        } else {
                            Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@LoginActivity, "Error parsing response", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: ANError) {
                    Log.d("LoginActivity", "Login failed: $error")
                    Toast.makeText(this@LoginActivity, "Login failed: $error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun navigateToRoleActivity(role: String) {
        val intent = when (role) {
            "admin" -> {
                val intent = Intent(this, DashboardsActivity::class.java)
                intent.putExtra("role", "admin")
            }
            "manajer" -> {
                val intent = Intent(this, DashboardsActivity::class.java)
                intent.putExtra("role", "manajer")
            }
            "kepala bagian" -> {
                val intent = Intent(this, KepBagDataJadwal::class.java)
                intent.putExtra("role", "kepala bagian")
            }
            "karyawan" -> {
                val intent = Intent(this, ReviewJadwalKaryawan::class.java)
                intent.putExtra("role", "karyawan")
            }
            else -> {
                Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show()
                return
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
