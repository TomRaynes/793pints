package com.pints793.mobile.image

import androidx.compose.runtime.Composable

/**
 * `expect` factory for an image picker bound to the host platform.
 * Returns a launcher closure; invoke it to open the system image picker.
 * The [onResult] callback fires with the picked image, or `null` if the user cancelled.
 */
@Composable
expect fun rememberImagePicker(onResult: (PickedImage?) -> Unit): () -> Unit

