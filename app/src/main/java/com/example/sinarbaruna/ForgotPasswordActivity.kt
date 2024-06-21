package com.example.sinarbaruna

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.sinarbaruna.databinding.ActivityForgotPasswordBinding
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var blurView: BlurView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        blurView = findViewById(R.id.blurView)
        val rootView: ViewGroup = window.decorView.findViewById(android.R.id.content)
        blurView.setupWith(rootView)
            .setFrameClearDrawable(window.decorView.background)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(10f)
            .setBlurAutoUpdate(true)

        binding.btnKirim.setOnClickListener {
            sendEmail()
        }

    }

    private fun sendEmail() {
        val email = binding.edtEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Please your email address", Toast.LENGTH_LONG).show()
            return
        }
        // Tampilkan loading bar
        binding.loadingbar.visibility = View.VISIBLE
        blurView.visibility = View.VISIBLE
        // POST API
        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.d(ContentValues.TAG, "Request JSON: $jsonObject") // Tambahkan log untuk JSON request


        val sharedPreference =  this.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.post("https://sinarbaruna.zegion.cloud/public/api/forgot-password")
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
                            Toast.makeText(this@ForgotPasswordActivity, "Success, Check your email to see your otp code", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@ForgotPasswordActivity, VerifiedOTPActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@ForgotPasswordActivity, response.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@ForgotPasswordActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: ANError) {
                    binding.loadingbar.visibility = View.GONE
                    Log.d(ContentValues.TAG, "onError errorCode : " + error.errorCode)
                    Log.d(ContentValues.TAG, "onError errorBody : " + error.errorBody)
                    Log.d(ContentValues.TAG, "onError errorDetail : " + error.errorDetail)
                    Toast.makeText(this@ForgotPasswordActivity, "Your Email is not registered", Toast.LENGTH_SHORT).show()
                }
            })
    }
}