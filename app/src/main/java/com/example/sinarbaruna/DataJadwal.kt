package com.example.sinarbaruna

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sinarbaruna.databinding.ActivityDataJadwalBinding

class DataJadwal : AppCompatActivity() {
    private lateinit var binding : ActivityDataJadwalBinding
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataJadwalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRole = intent.getStringExtra("role")

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
