package com.example.sinarbaruna

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sinarbaruna.databinding.ActivityDashboardsBinding

class DashboardsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardsBinding
    private var userole: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userole = intent.getStringExtra("role")
        if (userole != "admin") {
            binding.btnMasterdata.isEnabled = false
            binding.btnMasterdata.alpha = 0.5f // Mengubah tampilan tombol agar terlihat dinonaktifkan
        }
        binding.btnMasterdata.setOnClickListener {
            val intent = Intent(this, MasterDataActivity::class.java)
            startActivity(intent)
        }

        binding.btnDatajadwal.setOnClickListener {
            val intent = Intent(this, DataJadwal::class.java)
            startActivity(intent)
        }

        binding.btnBacktologin.setOnClickListener {
            val preferences: SharedPreferences = this.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        }
    }
}