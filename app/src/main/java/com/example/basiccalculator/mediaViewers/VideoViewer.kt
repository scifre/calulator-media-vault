package com.example.basiccalculator.mediaViewers

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.basiccalculator.AlertDialogBox
import com.example.basiccalculator.MediaHandler
import com.example.basiccalculator.darkOrange
import com.example.basiccalculator.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun VideoViewer(navController: NavController? = null, encodedUri: String? = "ed") {
    val context = LocalContext.current
    val videoUri = Uri.parse(encodedUri)

    var deleteAlertDialogState by remember { mutableStateOf(false) }
    var unhideAlertDialogState by remember { mutableStateOf(false) }

    if(deleteAlertDialogState){
        AlertDialogBox(
            alertTitle = "Delete Video?",
            alertText = "Are you sure you want to delete this video? This operation cannot be undone.",
            onDismissRequest = {
                deleteAlertDialogState = false
            },
            confirmButtonAction = {
                MediaHandler.deleteMedia(videoUri)
                deleteAlertDialogState = false
                navController?.popBackStack()
            },
            alertIcon = Icons.Filled.Delete
        )
    }

    if(unhideAlertDialogState){
        AlertDialogBox(
            alertTitle = "Unhide Video?",
            alertText = "Are you sure you want to unhide this video? This operation will restore the video to the gallery.",
            onDismissRequest = {
                unhideAlertDialogState = false
            },
            confirmButtonAction = {
                MediaHandler.moveMediaToExtStorage(videoUri, context)
                unhideAlertDialogState = false
                navController?.popBackStack()
            },
            alertIcon = Icons.Filled.Refresh
        )
    }



    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Video",
                        fontSize = 40.sp,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController?.popBackStack()
                        },

                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkOrange),
                actions = {
                    IconButton(
                        onClick = {
                            MediaHandler.moveMediaToExtStorage(
                                videoUri,
                                context
                            )
                            navController?.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Restore",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            MediaHandler.deleteMedia(videoUri)
                            navController?.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

            )
        }
    ){paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ){
            val exoPlayer = remember {
                ExoPlayer
                    .Builder(context)
                    .build()
                    .apply {
                        setMediaItem(
                            MediaItem.fromUri(videoUri)
                        )
                        prepare()
                        playWhenReady = true
                    }
            }

            DisposableEffect(Unit){
                onDispose {
                    exoPlayer.release()
                }
            }

            AndroidView(
                factory = { context->
                    PlayerView(context).apply {
                        player = exoPlayer
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }

                },
                modifier = Modifier
                    .fillMaxSize()
            )


        }
    }
}