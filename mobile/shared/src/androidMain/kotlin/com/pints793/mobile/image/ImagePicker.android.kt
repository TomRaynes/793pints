package com.pints793.mobile.image

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberImagePicker(onResult: (PickedImage?) -> Unit): () -> Unit {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) {
            onResult(null)
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val picked = withContext(Dispatchers.IO) {
                runCatching {
                    val resolver = ctx.contentResolver
                    val mime = resolver.getType(uri) ?: "image/*"
                    val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                        ?: return@runCatching null
                    val name = uri.lastPathSegment?.substringAfterLast('/') ?: "avatar"
                    PickedImage(bytes = bytes, mimeType = mime, filename = name)
                }.getOrNull()
            }
            onResult(picked)
        }
    }
    return remember(launcher) {
        {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

