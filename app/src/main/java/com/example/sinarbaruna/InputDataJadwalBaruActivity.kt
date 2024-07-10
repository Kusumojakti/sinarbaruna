package com.example.sinarbaruna

import android.content.ContentValues
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
import com.example.sinarbaruna.databinding.ActivityInputDataJadwalBaruBinding
import com.example.sinarbaruna.databinding.ActivityInputStatusJadwalBinding
import org.json.JSONException
import org.json.JSONObject

class InputDataJadwalBaruActivity : AppCompatActivity() {
    private lateinit var binding : ActivityInputDataJadwalBaruBinding
    private var selectedKeterangan: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputDataJadwalBaruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnKembali.setOnClickListener {
            val intent = Intent(this, JadwalBaruActivity::class.java)
            startActivity(intent)
        }
        binding.btnTambah.setOnClickListener {
            tambahjadwalbaru()
        }
    }
    private fun tambahjadwalbaru(){
        val tanggal = binding.inputTanggal.text.toString().trim()
        val typemoulding = binding.typemoulding.text.toString().trim()
        val durasi = binding.durasi.text.toString().trim()
        val mulaitanggal = binding.mulaitanggal.text.toString().trim()
        val pic = binding.edtPic.text.toString().trim()
        val idmoulding = binding.inputIdmoulding.text.toString().trim()

        // POST API
        val jsonObject = JSONObject()
        try {
            jsonObject.put("tanggal", tanggal)
            jsonObject.put("type_moulding", typemoulding)
            jsonObject.put("durasi", durasi)
            jsonObject.put("mulai_tanggal", mulaitanggal)
            jsonObject.put("username", pic)
            jsonObject.put("id_moulding", idmoulding)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.d(ContentValues.TAG, "Request JSON: $jsonObject") // Log JSON request

        val sharedPreference = this.getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        val token = sharedPreference?.getString("token", "")
        Log.d(ContentValues.TAG, "Token: $token") // Log token

        AndroidNetworking.post("https://sinarbaruna.zegion..site/api/jadwal")
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
                            Toast.makeText(this@InputDataJadwalBaruActivity, "Input successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@InputDataJadwalBaruActivity, JadwalBaruActivity::class.java)
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
                    Toast.makeText(applicationContext, "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show()
                }
            })
    }
}