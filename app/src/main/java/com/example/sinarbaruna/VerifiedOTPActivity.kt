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
import com.example.sinarbaruna.databinding.ActivityVerifiedOtpactivityBinding
import org.json.JSONException
import org.json.JSONObject

class VerifiedOTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifiedOtpactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifiedOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnKirim.setOnClickListener {
            verified()
        }

    }

    private fun verified() {
        val otp = binding.pinview.text.toString().trim()
        val email = intent.getStringExtra("email")

        // POST API
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
            jsonObject.put("token", otp)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.d(ContentValues.TAG, "Request JSON: $jsonObject")


        val sharedPreference =  this.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.post("https://sinarbaruna.zegion.cloud/public/api/verif-token")
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
                            Toast.makeText(this@VerifiedOTPActivity, "Success", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@VerifiedOTPActivity, UpdatePasswordActivity::class.java)
                            intent.putExtra("otp", otp)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@VerifiedOTPActivity, response.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@VerifiedOTPActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: ANError) {
                    Log.d(ContentValues.TAG, "onError errorCode : " + error.errorCode)
                    Log.d(ContentValues.TAG, "onError errorBody : " + error.errorBody)
                    Log.d(ContentValues.TAG, "onError errorDetail : " + error.errorDetail)
                    Toast.makeText(this@VerifiedOTPActivity, "OTP Code doesnt Match" , Toast.LENGTH_SHORT).show()
                }
            })
    }
}