package com.example.sinarbaruna

import android.R
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
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
import com.example.sinarbaruna.databinding.ActivityDataUsersBinding
import com.example.sinarbaruna.model.dataUsers
import org.json.JSONException
import org.json.JSONObject

class DataUsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataUsersBinding
    private lateinit var datausers: ArrayList<dataUsers>
    private var selectedRowIndex: Int? = null
    private val selectedRowIds = mutableSetOf<Int>()
    private val spinnerSelectedIndices = mutableMapOf<Int, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        datausers = ArrayList()

        binding.btnTambahusers.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnKembali.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        val sharedPreference = this.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token", "")
        Log.d(ContentValues.TAG, "Token: $token")

        AndroidNetworking.get("https://sinarbaruna.zegion.site/api/user")
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Bearer $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d(ContentValues.TAG, "Raw Data received: $response")
                    try {
                        if (response.getString("success").equals("true")) {

                            val jadwalArray = response.getJSONArray("data")
                            val jadwalList = mutableListOf<dataUsers>()
                            jadwalList.addAll(jadwalList)
                            for (i in 0 until jadwalArray.length()) {
                                val jadwalObject = jadwalArray.getJSONObject(i)
                                val jadwal = dataUsers(
                                    id = jadwalObject.getInt("id"),
                                    name = jadwalObject.getString("name"),
                                    username = jadwalObject.getString("username"),
                                    email = jadwalObject.getString("email"),
                                    bagian = jadwalObject.getString("bagian"),
                                    role = jadwalObject.getString("role")
                                )
                                jadwalList.add(jadwal)
                                datausers.add(jadwal)
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


    private fun populateTable(userlist: List<dataUsers>) {
        val role = resources.getStringArray(com.example.sinarbaruna.R.array.actions_array)



        for (users in userlist) {
            val row = TableRow(this).apply {
                setBackgroundColor(Color.WHITE)
            }

            row.setOnClickListener {
                selectedRowIndex = binding.tablelayout.indexOfChild(row)
                row.setBackgroundColor(Color.LTGRAY)
            }

            val idtableTextView = TextView(this).apply {
                text = users.id.toString()
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(idtableTextView)

            val nametextview = TextView(this).apply {
                text = users.name
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1

            }
            row.addView(nametextview)

            val usernametextview = TextView(this).apply {
                text = users.username
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(usernametextview)

            val emailtextview = TextView(this).apply {
                text = users.email
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(emailtextview)

            val bagiantextview = TextView(this).apply {
                text = users.role
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(bagiantextview)

            val roletextview = TextView(this).apply {
                text = users.bagian
                setPadding(5, 5, 5, 5)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ) // Lebar kolom 1
            }
            row.addView(roletextview)

            val keteranganSpinner = Spinner(this).apply {
                val adapter = ArrayAdapter<String>(
                    this@DataUsersActivity,
                    android.R.layout.simple_spinner_item,
                    role
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                setAdapter(adapter)


                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        val selectedAction = parent.getItemAtPosition(position).toString()
                        spinnerSelectedIndices[users.id] = position
                        setSelection(0)
                        if (selectedAction == "Edit") {
                            // Pindah ke activity Edit dengan data user yang dipilih
                            val intent = Intent(this@DataUsersActivity, EditDataUserActivity::class.java)
                            intent.putExtra("id", users.id)
                            intent.putExtra("name", users.name)
                            intent.putExtra("username", users.username)
                            intent.putExtra("email", users.email)
                            intent.putExtra("bagian", users.bagian)
                            intent.putExtra("role", users.role)

                            startActivity(intent)
                        }
                        else if (selectedAction == "Delete") {
                            deleteJadwal(users.id.toString())
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            }
            row.addView(keteranganSpinner)

            binding.tablelayout.removeView(row)
            binding.tablelayout.addView(row)
            selectedRowIds.add(users.id)

        }
    }

    private fun deleteJadwal(id: String) {
        val url = "https://sinarbaruna.zegion.site/api/user/$id"

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
                            fetchDataFromApi()
                            Toast.makeText(this@DataUsersActivity, "Delete successful", Toast.LENGTH_SHORT).show()
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
    private fun getToken(): String {
        val sharedPreference = this.getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        return sharedPreference?.getString("token", "") ?: ""
    }
}