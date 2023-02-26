package com.github.tuuzed.flowbussample

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.github.tuuzed.flowbus.FlowBus
import com.github.tuuzed.flowbussample.databinding.ActivityMainBinding
import com.github.tuuzed.flowbussample.event.FlowEvent
import com.github.tuuzed.flowbussample.event.asFlowEvent
import com.github.tuuzed.flowbussample.extension.observe

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.fab.setOnClickListener {
            FlowBus.trySend("${SystemClock.elapsedRealtime()}".asFlowEvent())
            startActivity(Intent(this, SecondActivity::class.java))
        }
        FlowBus.select<FlowEvent.String>().observe(this) {
            binding.tvText.text = it.value
        }
        FlowBus.trySend("Sticky: ${SystemClock.elapsedRealtime()}".asFlowEvent(), true)
    }


}