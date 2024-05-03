package com.example.sinarbaruna

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sinarbaruna.databinding.ActivityDashboardsBinding

class DashboardsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMasterdata.setOnClickListener {
            val intent = Intent(this, MasterDataActivity::class.java)
            startActivity(intent)
        }

        binding.btnDatajadwal.setOnClickListener {
            val intent = Intent(this, DataJadwal::class.java)
            startActivity(intent)
        }

        binding.btnBacktologin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}