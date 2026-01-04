package com.scrap2025.scrap2025.di

import com.scrap2025.scrap2025.model.SnsType
import dagger.MapKey

@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class SnsTypeKey(val value: SnsType)
