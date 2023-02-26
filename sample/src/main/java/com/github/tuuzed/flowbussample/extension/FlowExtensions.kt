package com.github.tuuzed.flowbussample.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> Flow<T>.observe(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>
) {
    require(owner !is Fragment) { "Use Fragment.getViewLifecycleOwner() Replace to owner" }
    owner.lifecycleScope.launch {
        flowWithLifecycle(owner.lifecycle, minActiveState)
            .collect(collector)
    }
}