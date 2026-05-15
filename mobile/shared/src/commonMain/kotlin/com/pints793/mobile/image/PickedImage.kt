package com.pints793.mobile.image

/** Cross-platform representation of an image chosen from the device gallery. */
data class PickedImage(
    val bytes: ByteArray,
    val mimeType: String,
    val filename: String,
)

