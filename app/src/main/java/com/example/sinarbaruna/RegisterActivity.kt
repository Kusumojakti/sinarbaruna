package com.example.sinarbaruna

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.sinarbaruna.databinding.ActivityRegisterBinding
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var dropdown: Spinner
    private lateinit var binding: ActivityRegisterBinding
    private var selectedRole: String? = null // Variabel untuk menyimpan nilai yang dipilih dari Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDaftar.setOnClickListener {
            registerUser()
        }

        binding.btnKembali.setOnClickListener {
            val intent = Intent(this, DashboardsActivity::class.java)
            startActivity(intent)
        }

        val role = resources.getStringArray(R.array.bagian)
        dropdown = findViewById(R.id.dropdown_menu)
        if (dropdown != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, role
            )
            dropdown.adapter = adapter
        }

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedRole = role[position] // Simpan nilai yang dipilih
                Toast.makeText(this@RegisterActivity, getString(R.string.selected_item) + " " + selectedRole, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tidak ada tindakan
            }
        }
    }

    private fun registerUser() {
        val nama = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()

        // Validasi input
        if (nama.isEmpty() || password.isEmpty() || selectedRole.isNullOrEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // POST API
        val jsonObject = JSONObject()
        try {
            jsonObject.put("username", nama)
            jsonObject.put("email", email)
            jsonObject.put("bagian", selectedRole)
            jsonObject.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.d(ContentValues.TAG, "Request JSON: $jsonObject") // Tambahkan log untuk JSON request


        val sharedPreference =  this.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.post("https://sinarbaruna.zegion.site/api/user/karyawan")
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Bearer $token")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        Log.d(ContentValues.TAG, response.toString())
                        if (response.getString("success") == "true") {
                            Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegisterActivity, DashboardsActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, response.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@RegisterActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: ANError) {
                    Log.d(ContentValues.TAG, "onError errorCode : " + error.errorCode)
                    Log.d(ContentValues.TAG, "onError errorBody : " + error.errorBody)
                    Log.d(ContentValues.TAG, "onError errorDetail : " + error.errorDetail)
                    Toast.makeText(this@RegisterActivity, "Network error: " + error.errorDetail, Toast.LENGTH_SHORT).show()
                }
            })
    }
}
