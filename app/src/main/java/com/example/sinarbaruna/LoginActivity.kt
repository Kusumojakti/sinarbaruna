package com.example.sinarbaruna

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.sinarbaruna.databinding.ActivityLoginBinding
import com.example.sinarbaruna.ui.theme.SinarbarunaTheme
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            verifikasilogin()
        }

        binding.txtRegistered.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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
                        if (response.getString("success").equals("true")) {
                            val user = response.getJSONObject("user")
                            val role = user.getString("role")

                            if (role.equals("admin")) {
                                Log.d("Login Admin Successfully", response.toString())
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Admin Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent =
                                    Intent(this@LoginActivity, DashboardsActivity::class.java)
                                intent.putExtra("role", "admin")
                                startActivity(intent)
                                finish()

                                //sharetoken
                                val shareToken = response.getString("token")
                                val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",
                                    Context.MODE_PRIVATE)
                                val editorToken = sharedPreference.edit()
                                editorToken.putString("token",shareToken)
                                editorToken.commit()

                            } else if (role.equals("manajer")) {
                                Log.d("Login Manajer Successfully", response.toString())
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Manajer Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent =
                                    Intent(this@LoginActivity, DashboardsActivity::class.java)
                                startActivity(intent)
                                finish()

                                //sharetoken
                                val shareToken = response.getString("token")
                                val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
                                val editorToken = sharedPreference.edit()
                                editorToken.putString("token",shareToken)
                                editorToken.commit()
                            }
                            else if (role.equals("kepala bagian")) {
                                Log.d("Login Kepala Bagian Successfully", response.toString())
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Kepala Bagian Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent =
                                    Intent(this@LoginActivity, DashboardsActivity::class.java)
                                startActivity(intent)
                                finish()

                                //sharetoken
                                val shareToken = response.getString("token")
                                val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
                                val editorToken = sharedPreference.edit()
                                editorToken.putString("token",shareToken)
                                editorToken.commit()
                            }
                            else {
                                Log.d("Login Karyawan Successfully", response.toString())
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Karyawan Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent =
                                    Intent(this@LoginActivity, ReviewDataJadwalActivity::class.java)
                                startActivity(intent)
                                finish()

                                //sharetoken
                                val shareToken = response.getString("token")
                                val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
                                val editorToken = sharedPreference.edit()
                                editorToken.putString("token",shareToken)
                                editorToken.commit()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@LoginActivity, "kesalahan", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onError(error: ANError) {
                    Log.d("Login gagal", error.toString())
                }
            })
    }
}