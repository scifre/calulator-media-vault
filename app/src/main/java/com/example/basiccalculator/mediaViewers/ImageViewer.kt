package com.example.basiccalculator.mediaViewers

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.basiccalculator.AlertDialogBox
import com.example.basiccalculator.MediaHandler.Companion.deleteMedia
import com.example.basiccalculator.MediaHandler.Companion.moveMediaToExtStorage
import com.example.basiccalculator.darkOrange
import com.example.basiccalculator.poppinsFontFamily

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//remove null
fun ImageViewer(navController: NavController? = null, encodedUri: String? = "Image") {
    //parsing encoded URI
    val imageUri = encodedUri.let { Uri.parse(it) }

    //state var
    var deleteAlertDialogState by remember { mutableStateOf(false) }
    var unhideAlertDialogState by remember { mutableStateOf(false) }
    println(imageUri.toString())

    val context = LocalContext.current

    if(unhideAlertDialogState){
        AlertDialogBox(
            alertText = "This operation will move the image back to the gallery.",
            alertTitle = "Unhide this Image?",
            onDismissRequest = {unhideAlertDialogState = false},
            confirmButtonAction = {
                unhideAlertDialogState = false
                moveMediaToExtStorage(imageUri,context)
                navController?.popBackStack()
            },
            alertIcon = Icons.Filled.Lock

        )
    }
    if(deleteAlertDialogState){
        AlertDialogBox(
            alertText = "Are you sure you want to delete this image? This operation cannot be undone.",
            alertTitle = "Delete this Image?",
            onDismissRequest = {deleteAlertDialogState = false},
            confirmButtonAction = {
                deleteAlertDialogState = false
                deleteMedia(imageUri)
                navController?.popBackStack()
            },
            alertIcon = Icons.Filled.Delete
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    imageUri.lastPathSegment?.substringAfterLast("/")?.let {
                        Text(
                            text = it,
                            fontSize = 40.sp,
                            color = Color.White,
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController?.popBackStack()
                        },
                        colors = IconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            disabledContainerColor = Color.White,
                            disabledContentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkOrange),
                //elevation = 5.dp
                actions = {
                    IconButton(
                        onClick = {
                            unhideAlertDialogState = true
                        },
                        colors = IconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            disabledContainerColor = Color.White,
                            disabledContentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Un-hide the image",
                            modifier = Modifier.size(40.dp),

                        )
                    }

                    IconButton(
                        onClick = {
                            deleteAlertDialogState = true
                        },
                        colors = IconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            disabledContainerColor = Color.White,
                            disabledContentColor = Color.White
                        )
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp)
            )
        }
    }
}

