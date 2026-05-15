@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

package com.pints793.mobile.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import platform.Foundation.NSData
import platform.Foundation.NSItemProvider
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@Composable
actual fun rememberImagePicker(onResult: (PickedImage?) -> Unit): () -> Unit {
    val callback = remember(onResult) { onResult }
    return remember {
        {
            presentPicker(callback)
        }
    }
}

private fun presentPicker(onResult: (PickedImage?) -> Unit) {
    val configuration = PHPickerConfiguration().apply {
        setSelectionLimit(1)
        setFilter(PHPickerFilter.imagesFilter())
    }
    val controller = PHPickerViewController(configuration = configuration)
    val delegate = PickerDelegate(onResult, onFinish = { controller.dismissViewControllerAnimated(true, null) })
    controller.delegate = delegate
    // Hold a reference so the delegate isn't GC'd while the picker is open.
    DelegateRetainer.current = delegate

    val root = UIApplication.sharedApplication.keyWindow?.rootViewController
    var topMost: UIViewController? = root
    while (topMost?.presentedViewController != null) topMost = topMost.presentedViewController
    topMost?.presentViewController(controller, animated = true, completion = null)
}

private object DelegateRetainer { var current: Any? = null }

private class PickerDelegate(
    private val onResult: (PickedImage?) -> Unit,
    private val onFinish: () -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        @Suppress("UNCHECKED_CAST")
        val results = didFinishPicking as List<PHPickerResult>
        val item: NSItemProvider? = results.firstOrNull()?.itemProvider
        if (item == null) {
            onFinish()
            DelegateRetainer.current = null
            onResult(null)
            return
        }
        // Load raw image data via UTI — works for HEIC, JPEG, PNG transparently.
        item.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
            val picked = data?.toByteArray()?.let {
                PickedImage(bytes = it, mimeType = "image/jpeg", filename = "avatar.jpg")
            }
            onFinish()
            DelegateRetainer.current = null
            onResult(picked)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val len = this.length.toInt()
    if (len == 0) return ByteArray(0)
    val src = this.bytes!!.reinterpret<ByteVar>()
    return ByteArray(len) { i -> src[i] }
}



