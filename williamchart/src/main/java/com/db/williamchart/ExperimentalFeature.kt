package com.db.williamchart

@RequiresOptIn(level = RequiresOptIn.Level.WARNING, message = "This feature is experimental and it can change in future.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.PROPERTY)
annotation class ExperimentalFeature
