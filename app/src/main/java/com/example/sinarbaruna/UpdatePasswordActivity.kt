package com.example.sinarbaruna

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.sinarbaruna.databinding.ActivityUpdatePasswordBinding
import org.json.JSONException
import org.json.JSONObject

class UpdatePasswordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUpdatePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnKirim.setOnClickListener {
            updatepass()
        }
    }

    private fun updatepass(){
        val updatepass = binding.edtUbahpassword.text.toString()
        val confirmpass = binding.edtConfirmpassword.text.toString()
        val email = intent.getStringExtra("email")
        val otp = intent.getStringExtra("otp")

        if(updatepass != confirmpass) {
            Toast.makeText(this@UpdatePasswordActivity, "Password tidak sesuai", Toast.LENGTH_LONG).show()
            return
        }

        // POST API
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
            jsonObject.put("token", otp)
            jsonObject.put("password", updatepass)
            jsonObject.put("password_confirmation", confirmpass)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.d(ContentValues.TAG, "Request JSON: $jsonObject")


        val sharedPreference =  this.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.post("https://sinarbaruna.zegion.site/api/update-password")
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
                            Toast.makeText(this@UpdatePasswordActivity, "Success", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@UpdatePasswordActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@UpdatePasswordActivity, response.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@UpdatePasswordActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: ANError) {
                    Log.d(ContentValues.TAG, "onError errorCode : " + error.errorCode)
                    Log.d(ContentValues.TAG, "onError errorBody : " + error.errorBody)
                    Log.d(ContentValues.TAG, "onError errorDetail : " + error.errorDetail)
                    Toast.makeText(this@UpdatePasswordActivity, "Network error: " + error.errorDetail, Toast.LENGTH_SHORT).show()
                }
            })
    }
}