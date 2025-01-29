package com.example.basiccalculator

import android.net.Uri
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Preview(device = "spec:width=411dp,height=891dp", showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//remove null
fun ImageViewer(navController: NavController? = null, imageUri: Uri? = null) {
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
                            //unhidden the image

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
                            //delete te image
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

