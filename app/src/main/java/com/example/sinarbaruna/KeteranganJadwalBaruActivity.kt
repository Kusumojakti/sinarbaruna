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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.sinarbaruna.databinding.ActivityKeteranganJadwalBaruBinding
import com.example.sinarbaruna.databinding.ActivityRegisterBinding
import org.json.JSONException
import org.json.JSONObject

class KeteranganJadwalBaruActivity : AppCompatActivity() {
    private lateinit var dropdown: Spinner
    private var selectedKet: String? = null
    private lateinit var binding: ActivityKeteranganJadwalBaruBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeteranganJadwalBaruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keterangan = resources.getStringArray(R.array.keterangan)
        dropdown = findViewById(R.id.dropdown_menu)
        if (dropdown != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, keterangan
            )
            dropdown.adapter = adapter
        }

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedKet = keterangan[position] // Simpan nilai yang dipilih
                Toast.makeText(this@KeteranganJadwalBaruActivity, "Pilih Keterangan" + " " + selectedKet, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tidak ada tindakan
            }
        }
        binding.btnTambah.setOnClickListener {
            tambah()
        }
    }
    private fun tambah() {
        val idmoulding = binding.inputIdmoulding.text.toString().trim()
        val pic = binding.edtPic.text.toString().trim()
        val typemoulding = binding.inputType.text.toString().trim()
        val mulaitanggal = binding.edtMulaitanggal.text.toString().trim()
        val durasi = binding.inputDurasi.text.toString().trim()
        val tanggal = binding.inputTanggal.text.toString().trim()

        // Validasi input
//        if (nama.isEmpty() || password.isEmpty() || selectedRole.isNullOrEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
//            return
//        }

        // POST API
        val jsonObject = JSONObject()
        try {
            jsonObject.put("id_moulding", idmoulding)
            jsonObject.put("username", pic)
            jsonObject.put("tanggal",tanggal )
            jsonObject.put("type_moulding", typemoulding)
            jsonObject.put("durasi", durasi)
            jsonObject.put("mulai_tanggal", mulaitanggal)
            jsonObject.put("keterangan", selectedKet)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.d(ContentValues.TAG, "Request JSON: $jsonObject") // Tambahkan log untuk JSON request

        val sharedPreference = this.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token", "")
        Log.d(ContentValues.TAG, "Token: $token") // Tambahkan log untuk token

        AndroidNetworking.post("https://sinarbaruna.zegion.site/api/jadwal")
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
                            Toast.makeText(applicationContext, "Input successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, JadwalBaruActivity::class.java)
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
                    Toast.makeText(applicationContext, "Network error: " + error.errorDetail, Toast.LENGTH_SHORT).show()
                }
            })
    }

}