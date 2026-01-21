package com.prayerreminder.app.ui.home.model

data class Prayer(
    val name: String,
    val time: String,
    val description: String,
    val reminderEnabled: Boolean
)
