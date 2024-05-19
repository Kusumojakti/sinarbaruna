package com.example.sinarbaruna

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sinarbaruna.databinding.ActivityDataJadwalBinding
import com.example.sinarbaruna.databinding.ActivityKepBagDataJadwalBinding

class KepBagDataJadwal : AppCompatActivity() {
    private lateinit var binding : ActivityKepBagDataJadwalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKepBagDataJadwalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReviewjadwal.setOnClickListener {
            val intent = Intent(this, ReviewJadwalKaryawan::class.java)
            startActivity(intent)
        }

        binding.btnStatusjadwal.setOnClickListener {
            val intent = Intent(this, InputStatusKepBag::class.java)
            startActivity(intent)
        }

        binding.backbtn.setOnClickListener {
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