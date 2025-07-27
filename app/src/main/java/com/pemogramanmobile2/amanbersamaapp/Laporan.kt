package com.pemogramanmobile2.amanbersamaapp

import java.io.Serializable

data class Laporan(
    var id: String? = null,
    val pelaporId: String? = null,
    val deskripsi: String? = null,
    val status: String? = null,
    val timestamp: Long? = 0L
) : Serializable