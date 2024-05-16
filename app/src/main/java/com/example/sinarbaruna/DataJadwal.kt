package com.example.sinarbaruna

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sinarbaruna.databinding.ActivityDataJadwalBinding

class DataJadwal : AppCompatActivity() {
    private lateinit var binding : ActivityDataJadwalBinding
    private var userole: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataJadwalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userole = intent.getStringExtra("role")
        if (userole == "kepala bagian") {
            binding.btnJadwalbaru.isEnabled = false
            binding.btnJadwalbaru.alpha = 0.5f // Mengubah tampilan tombol agar terlihat dinonaktifkan
        }

        binding.btnJadwalbaru.setOnClickListener {
            val intent = Intent(this, JadwalBaruActivity::class.java)
            startActivity(intent)
        }

        binding.btnReviewjadwal.setOnClickListener {
            val intent = Intent(this, ReviewDataJadwalActivity::class.java)
            startActivity(intent)
        }

        binding.btnStatusjadwal.setOnClickListener {
            val intent = Intent(this, InputStatusJadwalActivity::class.java)
            startActivity(intent)
        }

        binding.backbtn.setOnClickListener {
            val intent = Intent(this, DashboardsActivity::class.java)
            startActivity(intent)
        }
    }
}