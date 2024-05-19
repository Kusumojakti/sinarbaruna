package com.example.sinarbaruna

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sinarbaruna.databinding.ActivityDashboardsBinding

class DashboardsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardsBinding
    private var userole: String? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        userole = intent.getStringExtra("role") ?: sharedPreferences.getString("role", null)

        if (userole != null) {
            with(sharedPreferences.edit()) {
                putString("role", userole)
                apply()
            }
        }

        configureButtons(userole)

        binding.btnMasterdata.setOnClickListener {
            val intent = Intent(this, MasterDataActivity::class.java)
            startActivity(intent)
        }

        binding.btnDatajadwal.setOnClickListener {
            val intent = Intent(this, DataJadwal::class.java)
            startActivity(intent)
        }

        binding.btnRegist.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnBacktologin.setOnClickListener {
            clearPreferencesAndLogout()
        }
    }

    private fun configureButtons(role: String?) {
        when (role) {
            "manajer" -> {
                binding.btnRegist.visibility = View.GONE
                binding.btnMasterdata.visibility = View.GONE
            }
            "admin" -> {
                binding.btnRegist.visibility = View.VISIBLE
                binding.btnMasterdata.visibility = View.VISIBLE
            }
            else -> {
                binding.btnRegist.visibility = View.GONE
                binding.btnMasterdata.visibility = View.GONE
            }
        }
    }

    private fun clearPreferencesAndLogout() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
