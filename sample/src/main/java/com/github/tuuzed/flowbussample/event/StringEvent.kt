package com.github.tuuzed.flowbussample.event

import com.github.tuuzed.flowbus.IFlowEvent

private typealias KString = String

sealed interface FlowEvent : IFlowEvent {
    class String(val value: KString) : FlowEvent
}

fun KString.asFlowEvent(): FlowEvent = FlowEvent.String(this)