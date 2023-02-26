package com.github.tuuzed.flowbussample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.tuuzed.flowbus.FlowBus
import com.github.tuuzed.flowbussample.databinding.ActivitySecondBinding
import com.github.tuuzed.flowbussample.event.FlowEvent
import com.github.tuuzed.flowbussample.extension.observe

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.fab.setOnClickListener {
            finish()
        }
        FlowBus.select<FlowEvent.String>(true).observe(this) {
            FlowBus.removeSticky<FlowEvent.String>()
            binding.tvText.text = it.value
        }
    }

}