package com.example.sinarbaruna

import android.R
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.sinarbaruna.databinding.ActivityKeteranganJadwalBaruBinding
import com.example.sinarbaruna.databinding.ActivityReviewDataJadwalBinding
import com.example.sinarbaruna.model.dataJadwal
import org.json.JSONObject

class ReviewDataJadwalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewDataJadwalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewDataJadwalBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                        if (response.getString("success").equals("true")){

                            val jadwalArray = response.getJSONArray("data")
                            val jadwalList = mutableListOf<dataJadwal>()
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
            }

            val idtableTextView = TextView(this).apply {
                text = jadwal.id.toString()
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f) // Lebar kolom 1
            }
            row.addView(idtableTextView)

            val idMouldingTextView = TextView(this).apply {
                text = jadwal.id_moulding
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f) // Lebar kolom 1

            }
            row.addView(idMouldingTextView)

            val tanggalTextView = TextView(this).apply {
                text = jadwal.tanggal
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f) // Lebar kolom 1
            }
            row.addView(tanggalTextView)

            val typeMouldingTextView = TextView(this).apply {
                text = jadwal.type_moulding
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f) // Lebar kolom 1
            }
            row.addView(typeMouldingTextView)

            val durasiTextView = TextView(this).apply {
                text = jadwal.durasi
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f) // Lebar kolom 1
            }
            row.addView(durasiTextView)

            val mulaiTanggalTextView = TextView(this).apply {
                text = jadwal.mulai_tanggal
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f) // Lebar kolom 1
            }
            row.addView(mulaiTanggalTextView)

            val userNameTextView = TextView(this).apply {
                text = jadwal.user_id
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f) // Lebar kolom 1
            }
            row.addView(userNameTextView)

            val keteranganSpinner = Spinner(this).apply {
                val adapter = ArrayAdapter<String>(this@ReviewDataJadwalActivity, R.layout.simple_spinner_item, role)
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                setAdapter(adapter)
                setSelection(adapter.getPosition(jadwal.keterangan))
            }
            row.addView(keteranganSpinner)


            binding.tablelayout.removeView(row)
            binding.tablelayout.addView(row)
        }
    }
}