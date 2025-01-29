package com.example.basiccalculator

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.io.File

@Preview(device = "spec:width=411dp,height=891dp", showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//remove null
fun ImageViewer(navController: NavController? = null, imageUri: Uri? = null) {
    println(imageUri.toString())
    val context = LocalContext.current
    fun moveImageToExtStorage(imageUri: Uri?){
        val fileName = imageUri?.lastPathSegment?.substringAfterLast("/")
            ?: "image_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/camera")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }


        val newImageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        newImageUri?.let{outputUri ->
            try {
                context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                    if (imageUri != null) {
                        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(outputUri, contentValues, null, null)

                imageUri?.path?.let { File(it) }?.delete()

                Log.d("Camera Roll", "Images moved to camera roll DCIM")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Image Viewer",
                        fontSize = 40.sp,
                        color = Color.Green
                    )
                },
                colors =    TopAppBarDefaults.topAppBarColors(containerColor = Color.Magenta),
                //elevation = 5.dp
                actions = {
                    IconButton(
                        onClick = {
                            moveImageToExtStorage(imageUri)
                            navController?.popBackStack()

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Unhided the image",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            imageUri?.path?.let { File(it) }?.delete()
                            navController?.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "delete image",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()

        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Image Viewer",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

