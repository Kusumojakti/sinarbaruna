package com.example.sinarbaruna

import android.Manifest
import android.R
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.AdapterView
import androidx.compose.material3.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class JadwalBaruActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJadwalBaruBinding
    private lateinit var datajadwal: ArrayList<dataJadwal>
    private var selectedProcess: String? = null
    private var selectedRowIndex: Int? = null
    private val selectedRowIds = mutableSetOf<Int>()
    private val PERMISSIONS_REQUEST_LOCATION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadwalBaruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        datajadwal = ArrayList()

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, InputStatusJadwalActivity::class.java)
            startActivity(intent)
        }

        binding.btnTambah.setOnClickListener {
            val intent = Intent(this@JadwalBaruActivity, InputDataJadwalBaruActivity::class.java)
            startActivity(intent)
        }

        binding.btnHapus.setOnClickListener {
            val intent = Intent(this, InputStatusJadwalActivity::class.java)
            startActivity(intent)
        }

//        download excel
        binding.downloadexcel.setOnClickListener {
            if (datajadwal.isNotEmpty()) {
                exportToExcel(datajadwal)
            } else {
                Toast.makeText(this, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnKembali.setOnClickListener {
            val intent = Intent(this, DataJadwal::class.java)
            startActivity(intent)
        }


        val statusket = resources.getStringArray(com.example.sinarbaruna.R.array.keterangan)
        val dropdown = binding.keterangantable // Use view binding to access the spinner

        val adapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item, statusket
        )
        dropdown.adapter = adapter

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedProcess = statusket[position] // Save selected value
                Toast.makeText(
                    this@JadwalBaruActivity,
                    "Pilih Keterangan $selectedProcess",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }

        fetchDataFromApi()
    }

//    penanganan proses write data
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check and request WRITE_EXTERNAL_STORAGE permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )) {

                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_LOCATION)
                }
            }
        }
    }

    private fun fetchDataFromApi() {
        val sharedPreference = this.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token", "")
        Log.d(ContentValues.TAG, "Token: $token")

        AndroidNetworking.get("https://sinarbaruna.zegion.cloud/public/api/jadwal")
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
                                datajadwal.add(jadwal)
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
        val role = resources.getStringArray(com.example.sinarbaruna.R.array.keterangan)


        for (jadwal in jadwalList) {
            val row = TableRow(this).apply {
                setBackgroundColor(Color.WHITE)
            }

            row.setOnClickListener {
                selectedRowIndex = binding.tablelayout.indexOfChild(row)
                row.setBackgroundColor(Color.LTGRAY)
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
            selectedRowIds.add(jadwal.id)

        }
    }

    private fun exportToExcel(jadwalList: List<dataJadwal>) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Jadwal")
        val header = sheet.createRow(0)

        val headerCellStyle = workbook.createCellStyle()
        headerCellStyle.fillForegroundColor = IndexedColors.YELLOW.index
            headerCellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

        // Create header cells
        val headers = arrayOf("ID", "ID Moulding", "Tanggal", "Type Moulding", "Durasi", "Mulai Tanggal", "User ID", "Keterangan")
        for ((index, headerName) in headers.withIndex()) {
            val cell = header.createCell(index)
            cell.setCellValue(headerName)
            cell.cellStyle = headerCellStyle
        }

        // Populate data rows
        for ((rowIndex, jadwal) in jadwalList.withIndex()) {
            val row = sheet.createRow(rowIndex + 1)
            row.createCell(0).setCellValue(jadwal.id.toString())
            row.createCell(1).setCellValue(jadwal.id_moulding)
            row.createCell(2).setCellValue(jadwal.tanggal)
            row.createCell(3).setCellValue(jadwal.type_moulding)
            row.createCell(4).setCellValue(jadwal.durasi)
            row.createCell(5).setCellValue(jadwal.mulai_tanggal)
            row.createCell(6).setCellValue(jadwal.user_id)
            row.createCell(7).setCellValue(jadwal.keterangan)
        }

        // Save to file
        try {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Jadwal.xlsx")
            val fileOut = FileOutputStream(file)
            workbook.write(fileOut)
            fileOut.close()
            workbook.close()

            Toast.makeText(this, "Data berhasil diekspor ", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal mengekspor data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}

