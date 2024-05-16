package com.example.sinarbaruna

import android.R
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.sinarbaruna.databinding.ActivityJadwalBaruBinding
import com.example.sinarbaruna.model.dataJadwal
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import android.graphics.Color
import android.view.View
import androidx.compose.material3.Button

class JadwalBaruActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJadwalBaruBinding
    private lateinit var datajadwal: ArrayList<dataJadwal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadwalBaruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val statusket = resources.getStringArray(com.example.sinarbaruna.R.array.status)
        val dropdown = binding.keterangantable // Use view binding to access the spinner

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, InputStatusJadwalActivity::class.java)
            startActivity(intent)
        }

        binding.btnTambah.setOnClickListener {
            val intent = Intent(this, InputDataJadwalBaruActivity::class.java)
            startActivity(intent)
        }

        val adapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item, statusket
        )
//        dropdown.adapter = adapter
//
//        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                selectedProcess = statusket[position] // Save selected value
//                Toast.makeText(
//                    this@JadwalBaruActivity,
//                    "Pilih Keterangan $selectedProcess",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // No action needed
//            }
//        }
        binding.btnHapus.setOnClickListener {view ->
            onHapusClicked(view)
        }

        fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        val sharedPreference = this.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token", "")
        Log.d(ContentValues.TAG, "Token: $token")

        AndroidNetworking.get("http://sinarbaruna.d2l.my.id/api/jadwal")
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Bearer $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d(ContentValues.TAG, "Raw Data received: $response")
                    try {
                        if (response.getString("success").equals("true")) {

                            val jadwalArray = response.getJSONArray("data")
                            val jadwalList = mutableListOf<dataJadwal>()
                            jadwalList.addAll(jadwalList)
                            for (i in 0 until jadwalArray.length()) {
                                val jadwalObject = jadwalArray.getJSONObject(i)
                                val jadwal = dataJadwal(
                                    id = jadwalObject.getInt("id"),
                                    id_moulding = jadwalObject.getString("id_moulding"),
                                    user_id = jadwalObject.getString("username"),
                                    tanggal = jadwalObject.getString("tanggal"),
                                    type_moulding = jadwalObject.getString("type_moulding"),
                                    durasi = jadwalObject.getString("durasi"),
                                    mulai_tanggal = jadwalObject.getString("mulai_tanggal"),
                                    keterangan = jadwalObject.getString("keterangan")
                                )
                                jadwalList.add(jadwal)
                            }
                            Log.d(ContentValues.TAG, "Parsed Data: $jadwalList")
                            runOnUiThread {
                                populateTable(jadwalList)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(ContentValues.TAG, "Error parsing data: ${e.message}")
                    }
                }

                override fun onError(anError: ANError) {
                    Log.e(ContentValues.TAG, "Error: ${anError.errorDetail}")
                    Log.e(ContentValues.TAG, "Response: ${anError.response}")
                }
            })
    }


    private fun populateTable(jadwalList: List<dataJadwal>) {
//        val idtable = binding.idtable
//        val mouldingtable = binding.mouldingtable
//        val tanggal_table = binding.tanggalTable
//        val jenismould_table = binding.jenismouldTable
//        val durasi_table = binding.durasiTable
//        val pic_table = binding.picTable
//        val keterangan_table = binding.keteranganTable

        val role = resources.getStringArray(com.example.sinarbaruna.R.array.keterangan)

        for (jadwal in jadwalList) {
            val row = TableRow(this).apply {
                setBackgroundColor(Color.WHITE)
                setOnClickListener { view ->
                    onHapusClicked(this)
                }
            }

            val idtableTextView = TextView(this).apply {
                text = jadwal.id.toString()
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(idtableTextView)

            val idMouldingTextView = TextView(this).apply {
                text = jadwal.id_moulding
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1

            }
            row.addView(idMouldingTextView)

            val tanggalTextView = TextView(this).apply {
                text = jadwal.tanggal
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(tanggalTextView)

            val typeMouldingTextView = TextView(this).apply {
                text = jadwal.type_moulding
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(typeMouldingTextView)

            val durasiTextView = TextView(this).apply {
                text = jadwal.durasi
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(durasiTextView)

            val mulaiTanggalTextView = TextView(this).apply {
                text = jadwal.mulai_tanggal
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(mulaiTanggalTextView)

            val userNameTextView = TextView(this).apply {
                text = jadwal.user_id
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(userNameTextView)

            val keteranganSpinner = Spinner(this).apply {
                val adapter = ArrayAdapter<String>(
                    this@JadwalBaruActivity,
                    R.layout.simple_spinner_item,
                    role
                )
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                setAdapter(adapter)
                setSelection(adapter.getPosition(jadwal.keterangan))
            }
            row.addView(keteranganSpinner)

            binding.tablelayout.removeView(row)
            binding.tablelayout.addView(row)
        }
    }
    fun onHapusClicked(view: View) {
        if (::datajadwal.isInitialized) {
            val position = view.getTag() as Int
            val idToDelete = datajadwal[position].id // Ambil ID dari data pada posisi ini di tabel

            // Panggil metode untuk menghapus data berdasarkan ID
            deleteData(idToDelete)
        } else {
            Log.e(ContentValues.TAG, "datajadwal is not initialized")
            // Handle the case when datajadwal is not initialized
        }
    }
    private fun deleteData(id: Int) {
        val url = "http://sinarbaruna.d2l.my.id/api/jadwal/$id"

        val sharedPreference = this.getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        val token = sharedPreference?.getString("token", "")
        Log.d(ContentValues.TAG, "Token: $token") // Log token

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
                            Toast.makeText(this@JadwalBaruActivity, "Delete successful", Toast.LENGTH_SHORT).show()
                            // Refresh the activity or update the UI accordingly
                            val intent = Intent(this@JadwalBaruActivity, JadwalBaruActivity::class.java)
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
