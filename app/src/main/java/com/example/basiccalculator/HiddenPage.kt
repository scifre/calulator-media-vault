package com.example.basiccalculator


import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


@Composable
fun AlertDialogBox(
    alertText: String,
    alertTitle: String,
    onDismissRequest: () -> Unit,
    confirmButtonAction: () -> Unit,
    alertIcon: ImageVector? = null
){
    AlertDialog(
        onDismissRequest = {onDismissRequest()},
        confirmButton = {
            TextButton(
                onClick = {confirmButtonAction()}
            ) {
                Text(
                    text = "OK",
                    fontSize = 18.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {onDismissRequest()}
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 18.sp
                )
            }
        },
        title = {
            Text(
                text = alertTitle,
                fontSize = 25.sp,
            )
        },
        text = {
            Text(
                text = alertText,
                fontSize = 18.sp
            )
        },
        icon = {
            if (alertIcon != null) {
                Icon(
                    imageVector = alertIcon,
                    contentDescription = "Alert Icon"
                )
            }
        }
    )

}




@Composable
@OptIn(ExperimentalMaterial3Api::class)

fun HiddenPageGalleryView(navController: NavController?){
    val context = LocalContext.current
    val activity = context as? Activity
    var launchPhotoPicker by remember { mutableStateOf(false) }


    fun createVaultDirectory(context: Context): File {
        val vaultDirectory = File(context.filesDir, "vault_gallery")

        if (!vaultDirectory.exists()) {
            vaultDirectory.mkdirs()
        }
        return vaultDirectory
    }


    fun isValidUri(uri: Uri): Boolean {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val isValid = cursor != null && cursor.moveToFirst()
            cursor?.close()
            isValid
        } catch (e: Exception) {
            false
        }
    }
    val images = remember{ mutableStateListOf<Uri>() }
    @Composable
    fun ImageThumbnail(image: Uri){
        Image(
            painter = rememberAsyncImagePainter(image),
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
                    navController?.navigate("image_viewer/${Uri.encode(image.toString())}")
                },
            contentScale = ContentScale.Crop
        )
    }


    fun loadImagesDynamically() {
        val imagesFiles = File(context.filesDir, "vault_gallery").listFiles { file -> file.isFile }
            ?.map(Uri::fromFile) ?: emptyList<Uri>()
        images.clear()
        images.addAll(imagesFiles)
    }

    fun moveImageToVault(context: Context, uris: List<Uri>) {
        uris.forEach { uri ->
            println(uri.toString())
            if (!isValidUri(uri)) {
                Log.e("Error", "Invalid URI")
                return
            }
            try {
                // Get the vault directory
                val vaultDirectory = createVaultDirectory(context)
                // Extract the file name from the URI (fallback to timestamp if null)
                val fileName = uri.lastPathSegment?.substringAfterLast("/")
                    ?: "image_${System.currentTimeMillis()}.jpg"

                // Create a file in the vault directory
                val destinationFile = File(vaultDirectory, fileName)

                // Open the input stream from the URI
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

                // Write the file to the vault directory
                val outputStream = FileOutputStream(destinationFile)
                inputStream?.copyTo(outputStream)


                // Close the streams
                inputStream?.close()
                outputStream.close()


                try {
                    val     fileId = ContentUris.parseId(uri)
                    val selection = "${MediaStore.Images.Media._ID} = ?"
                    val selectionArgs = arrayOf(fileId.toString())

                    context.contentResolver.delete(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection,
                        selectionArgs
                    )
                } catch (e: IllegalArgumentException) {
                    // Handle the error (e.g., invalid Uri)
                    e.printStackTrace()
                } catch (e: SecurityException) {
                    // Handle permission issues
                    e.printStackTrace()
                }
                loadImagesDynamically()

                println("Image moved to vault: ${destinationFile.absolutePath}")
            } catch (e: Exception) {
                println("Error11")
                e.printStackTrace()
            }
        }
    }

        LaunchedEffect(Unit) {
        loadImagesDynamically()
    }


    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()){
            Toast.makeText(context, "Selected ${uris.size} items", Toast.LENGTH_SHORT).show()
            moveImageToVault(context, uris)
        }
        else{
            Toast.makeText(context, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }


    @Composable
    fun LaunchPhotoPicker(){
        println("LPP Launched")
        if(!Environment.isExternalStorageManager()){
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${activity?.packageName}")
            }

            AlertDialogBox(
                alertText = "This App needs permission to access your files, this will be used to move your files in and out of the vault. Grant it in the next step",
                alertTitle = "Storage Permission required",
                onDismissRequest = {
                    launchPhotoPicker = false
                },
                confirmButtonAction = {
                    launchPhotoPicker = false
                    activity?.startActivity(intent)}
            )

        } else{
            launchPhotoPicker = false
            pickMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gallery",
                        fontSize = 40.sp,
                        color = Color.Green
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Magenta),
                actions = {
                    IconButton(
                        onClick ={
                            println("Add button pressed")
                            launchPhotoPicker = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Image",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            )
        }
    ) {paddingValues->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()


        ) {
            items(images.size){index->

                    ImageThumbnail(images[index])
            }

        }

    }
    if(launchPhotoPicker){
        println("LaunchPhotoPicker set to true")
        LaunchPhotoPicker()
        //launchPhotoPicker = false
    }

}

@Preview(device = "spec:width=411dp,height=891dp", showBackground = true)
@Composable
fun HiddenPageGalleryViewPreview(){
    HiddenPageGalleryView(null)
}
