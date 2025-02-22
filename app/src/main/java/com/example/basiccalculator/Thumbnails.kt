package com.example.basiccalculator

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import java.util.Locale

class Thumbnails {
    companion object {
        @Composable
        fun ImageThumbnail(image: Uri, navController: NavController) {
            AsyncImage(
                model = image,
                contentDescription = "Image Thumbnail",
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(0.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Black
                    )
                    .shadow(
                        elevation = 8.dp,
                        spotColor = Color.Black
                    )
                    .clickable {
                        navController.navigate("image_viewer/${Uri.encode(image.toString())}")
                    },
                contentScale = ContentScale.Crop
            )
        }

        @Composable
        fun VideoThumbnail(video: Uri, context: Context, navController: NavController) {

            val time = videoLength(context, video)

            Box(contentAlignment = Alignment.BottomEnd) {

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(video)
                        .videoFrameMillis(10000)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .decoderFactory() { result, options, _ ->
                            VideoFrameDecoder(
                                result.source,
                                options
                            )
                        }
                        .crossfade(true)
                        .build(),
                    contentDescription = "Video Thumbnail",
                    modifier = Modifier
                        .aspectRatio(1f)
                        .border(
                            width = 1.dp,
                            color = Color.Black
                        )
                        .shadow(
                            elevation = 8.dp,
                            spotColor = Color.Black
                        )
                        .clickable { navController.navigate("video_player/${Uri.encode(video.toString())}") },
                    contentScale = ContentScale.Crop
                )

                Box {
                    Text(
                        text = time,
                        color = Color.White,
                        fontFamily = poppinsFontFamily

                    )
                }

            }
        }


        private fun videoLength(context: Context, video: Uri): String{
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, video)

            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()

            val durationLong = duration?.toLongOrNull() ?: 0L

            val minutes = (durationLong / 1000) / 60
            val seconds = (durationLong / 1000) % 60

            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}