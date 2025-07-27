package com.pemogramanmobile2.amanbersamaapp

import java.io.Serializable

data class PanicAlert(
    val pelaporId: String? = null,
    val lokasi: HashMap<String, Double>? = null,
    val timestamp: Long? = 0L
) : Serializable