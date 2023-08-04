package com.example.finalprojectgigih

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.finalprojectgigih.databinding.ActivitySettingBinding

class SettingActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.swDarkMode.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.ivBack.setImageResource(R.drawable.ic_baseline_arrow_back_24)
                saveData(true)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.ivBack.setImageResource(R.drawable.ic_baseline_arrow_back_24_black)
                saveData(false)
            }
        }
        binding.ivBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                finish()
            }
        })

    }

    fun saveData(state:Boolean){
        val sharedPreferences = getSharedPreferences("nightMode", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply{
            putBoolean("nightModeState",state)
        }.apply()
    }
    fun loadData(){
        val sharedPreferences = getSharedPreferences("nightMode", Context.MODE_PRIVATE)
        val savedNightMode = sharedPreferences.getBoolean("nightModeState",false)
    }
}