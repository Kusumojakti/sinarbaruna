package com.example.sinarbaruna

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.sinarbaruna.databinding.ActivityInputStatusJadwalBinding
import com.example.sinarbaruna.databinding.ActivityInputStatusKepBagBinding
import com.example.sinarbaruna.model.dataJadwal
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class InputStatusKepBag : AppCompatActivity() {
    private lateinit var binding: ActivityInputStatusKepBagBinding
    private var selectedProcess: String? = null
    private var searchedId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputStatusKepBagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val statusket = resources.getStringArray(R.array.keterangan)
        val dropdown = binding.inputStatus

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, statusket
        )
        dropdown.adapter = adapter

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedProcess = statusket[position]
                Toast.makeText(
                    this@InputStatusKepBag,
                    "Pilih Keterangan $selectedProcess",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }

        binding.searchId.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val id = it.toInt()
                    searchById(id)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.btnTambah.setOnClickListener {
            if (searchedId != null) {
                updateData(searchedId.toString())
            } else {
                Toast.makeText(this, "Please search for an ID first", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnHapus.setOnClickListener {
            if (searchedId != null) {
                deleteJadwal(searchedId.toString())
            } else {
                Toast.makeText(this, "Please search for an ID first", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnKembali.setOnClickListener {
            val intent = Intent(this, KepBagDataJadwal::class.java)
            startActivity(intent)
        }
    }

    private fun searchById(id: Int) {
        val url = "https://sinarbaruna.zegion.site/api/jadwal/$id"

        val token = getToken()
        Log.d(ContentValues.TAG, "Token: $token")

        AndroidNetworking.get(url)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Bearer $token")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        if (response.getString("success") == "true") {
                            val d = response.getJSONArray("data")
                            val data = d.getJSONObject(0)
                            if (data != null ){
                                val id = data.getInt("id")
                                binding.inputId.setText(data.getString("id"))
                                binding.inputIdmoulding.setText(data.getString("id_moulding"))
                                binding.inputTanggal.setText(data.getString("tanggal"))
                                binding.inputType.setText(data.getString("type_moulding"))
                                binding.inputDurasi.setText(data.getString("durasi"))
                                binding.edtMulaitanggal.setText(data.getString("mulai_tanggal"))
                                binding.edtPic.setText(data.getString("username"))
                                searchedId = id
                            }

                        }
                    } catch (e: Exception) {
                        Log.e(ContentValues.TAG, "Error parsing data: ${e.message}")
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


    private fun updateData(id: String) {
        val tanggal = binding.inputTanggal.text.toString().trim()
        val idmoulding = binding.inputIdmoulding.text.toString().trim()
        val typemoulding = binding.inputType.text.toString().trim()
        val durasi = binding.inputDurasi.text.toString().trim()
        val mulaitanggal = binding.edtMulaitanggal.text.toString().trim()
        val pic = binding.edtPic.text.toString().trim()

        if (tanggal.isEmpty() || typemoulding.isEmpty() || durasi.isEmpty() || mulaitanggal.isEmpty() || pic.isEmpty() || selectedProcess.isNullOrEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val jsonObject = JSONObject()
        try {
            jsonObject.put("tanggal", tanggal)
            jsonObject.put("id_moulding", idmoulding)
            jsonObject.put("type_moulding", typemoulding)
            jsonObject.put("durasi", durasi)
            jsonObject.put("mulai_tanggal", mulaitanggal)
            jsonObject.put("username", pic)
            jsonObject.put("keterangan", selectedProcess)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.d(ContentValues.TAG, "Request JSON: $jsonObject")

        val token = getToken()
        Log.d(ContentValues.TAG, "Token: $token")

        val url = "https://sinarbaruna.zegion.site/api/jadwal/$id"

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
                            Toast.makeText(this@InputStatusKepBag, "Update successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@InputStatusKepBag, JadwalBaruActivity::class.java)
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

    private fun deleteJadwal(id: String) {
        val url = "https://sinarbaruna.zegion.site/api/jadwal/$id"

        val token = getToken()
        Log.d(ContentValues.TAG, "Token: $token")

        AndroidNetworking.delete(url)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Bearer $token")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        Log.d(ContentValues.TAG, response.toString())
                        if (response.getString("success") == "true") {
                            Toast.makeText(this@InputStatusKepBag, "Delete successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@InputStatusKepBag, JadwalBaruActivity::class.java)
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
    private fun getToken(): String {
        val sharedPreference = this.getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        return sharedPreference?.getString("token", "") ?: ""
    }
}
