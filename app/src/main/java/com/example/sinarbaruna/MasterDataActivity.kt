package com.example.sinarbaruna

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sinarbaruna.databinding.ActivityMasterDataBinding

class MasterDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMasterDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnKembali.setOnClickListener {
            val intent = Intent(this, DashboardsActivity::class.java)
            startActivity(intent)
        }

    }
}