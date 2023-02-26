@file:OptIn(FlowBus.OnlyInternal::class)

package com.github.tuuzed.flowbus

import kotlinx.coroutines.flow.*

class FlowBus {

    companion object {

        @JvmStatic
        fun get(): FlowBus = DefaultHolder.INSTANCE

        fun <E : IFlowEvent> trySend(event: E, sticky: Boolean = false): Boolean = get().trySend(event, sticky)

        suspend fun <E : IFlowEvent> send(event: E, sticky: Boolean = false) = get().send(event, sticky)

        inline fun <reified E : IFlowEvent> tryRemoveSticky(): Boolean = get().tryRemoveSticky<E>()

        suspend inline fun <reified E : IFlowEvent> removeSticky() = get().removeSticky<E>()

        inline fun <reified E : IFlowEvent> select(sticky: Boolean = false): Flow<E> = get().select(sticky)

    }

    private object DefaultHolder {
        val INSTANCE = FlowBus()
    }

    private val flow = MutableSharedFlow<IFlowEvent?>(0, Int.MAX_VALUE)
    private val _cachedStickyFlow = HashMap<Class<*>, MutableSharedFlow<IFlowEvent?>>()

    fun <E : IFlowEvent> trySend(
        event: E, sticky: Boolean = false
    ): Boolean = flow(event::class.java, sticky).tryEmit(event)

    suspend fun <E : IFlowEvent> send(
        event: E, sticky: Boolean = false
    ) = flow(event::class.java, sticky).emit(event)

    inline fun <reified E : IFlowEvent> tryRemoveSticky(): Boolean = flow(E::class.java, true).tryEmit(null)

    suspend inline fun <reified E : IFlowEvent> removeSticky() = flow(E::class.java, true).emit(null)

    inline fun <reified E : IFlowEvent> select(
        sticky: Boolean = false
    ): Flow<E> = selectInternal(E::class.java, sticky)

    @OnlyInternal
    fun <E : IFlowEvent> selectInternal(clazz: Class<E>, sticky: Boolean): Flow<E> {
        return flow {
            flow(clazz, sticky)
                .filterNotNull()
                .filter { clazz.isAssignableFrom(it::class.java) }
                .collect {
                    @Suppress("UNCHECKED_CAST")
                    emit(it as E)
                }
        }
    }

    @OnlyInternal
    fun flow(type: Class<*>, sticky: Boolean): MutableSharedFlow<IFlowEvent?> {
        return if (sticky) {
            synchronized(_cachedStickyFlow) {
                var ret = _cachedStickyFlow[type]
                if (ret == null) {
                    ret = MutableSharedFlow(1, Int.MAX_VALUE)
                    _cachedStickyFlow[type] = ret
                }
                ret
            }
        } else {
            flow
        }
    }

    @RequiresOptIn(
        level = RequiresOptIn.Level.ERROR,
        message = "This is an internal FlowBus API. It is not intended for external use."
    )
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    internal annotation class OnlyInternal
}
