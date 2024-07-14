package com.example.sinarbaruna

import android.content.ContentValues
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
import com.example.sinarbaruna.databinding.ActivityEditDataUserBinding
import com.example.sinarbaruna.model.dataUsers
import org.json.JSONException
import org.json.JSONObject

class EditDataUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditDataUserBinding
    private lateinit var dropdownbag: Spinner
    private lateinit var dropdownjabatan: Spinner
    private var selectedRole: String? = null
    private var selectedJabatan: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDataUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("id", -1)
        val name = intent.getStringExtra("name")
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val bagian = intent.getStringExtra("bagian")
        val role = intent.getStringExtra("role")

        binding.edtName.setText(name.toString())
        binding.edtUsername.setText(username.toString())
        binding.edtEmail.setText(email.toString())

        setupDropdowns(bagian, role)

        binding.btnUpdate.setOnClickListener {
            if (id != null) {
                updateData(id.toString())
            } else {
                Toast.makeText(this, "ID not found", Toast.LENGTH_SHORT).show()
            }
            Log.d("data", id.toString())
        }

        binding.btnKembali.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupDropdowns(bagian: String?, role: String?) {
        // Setup bagian dropdown
        val bagianArray = resources.getStringArray(R.array.bagian)
        dropdownbag = findViewById(R.id.dropdown_menu)
        if (dropdownbag != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, bagianArray
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dropdownbag.adapter = adapter
            bagian?.let {
                val position = bagianArray.indexOf(it)
                if (position >= 0) {
                    dropdownbag.setSelection(position)
                }
            }
        }

        dropdownbag.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedRole = bagianArray[position] // Simpan nilai yang dipilih
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tidak ada tindakan
            }
        }

        // Setup jabatan dropdown
        val jabatanArray = resources.getStringArray(R.array.jabatan)
        dropdownjabatan = findViewById(R.id.dropdown_jabatan)
        if (dropdownjabatan != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, jabatanArray
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dropdownjabatan.adapter = adapter
            role?.let {
                val position = jabatanArray.indexOf(it)
                if (position >= 0) {
                    dropdownjabatan.setSelection(position)
                }
            }
        }

        dropdownjabatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedJabatan = jabatanArray[position] // Simpan nilai yang dipilih
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tidak ada tindakan
            }
        }
    }

    private fun updateData(id: String) {
        val nama = binding.edtName.text.toString().trim()
        val username = binding.edtUsername.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", nama)
            jsonObject.put("username", username)
            jsonObject.put("email", email)
            jsonObject.put("password", password)
            jsonObject.put("bagian", selectedRole)
            jsonObject.put("role", selectedJabatan)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.d(ContentValues.TAG, "Request JSON: $jsonObject")

        val token = getToken()
        Log.d(ContentValues.TAG, "Token: $token")

        val url = "https://sinarbaruna.zegion.site/api/user/$id"

        AndroidNetworking.put(url)
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
                            Toast.makeText(this@EditDataUserActivity, "Update successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@EditDataUserActivity, DataUsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, response.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(applicationContext, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: ANError) {
                    Log.d(ContentValues.TAG, "onError errorCode : " + error.errorCode)
                    Log.d(ContentValues.TAG, "onError errorBody : " + error.errorBody)
                    Log.d(ContentValues.TAG, "onError errorDetail : " + error.errorDetail)
                    val errorMessage = try {
                        val errorResponse = JSONObject(error.errorBody)
                        errorResponse.getString("message")
                    } catch (e: JSONException) {
                        "Error occurred"
                    }

                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getToken(): String {
        val sharedPreference = this.getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        return sharedPreference?.getString("token", "") ?: ""
    }
}
