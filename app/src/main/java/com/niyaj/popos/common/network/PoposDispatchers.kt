package com.niyaj.popos.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val dispatcher: PoposDispatchers)

enum class PoposDispatchers {
    Default,
    IO,
}
