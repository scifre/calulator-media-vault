package com.example.basiccalculator

//import androidx.compose.ui.tooling.data.EmptyGroup.data
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


//class HiddenPage: ComponentActivity() {

    private fun checkWriteExternalStoragePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkReadExternalStoragePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkReadMediaImagesPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkReadMediaVideoPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_MEDIA_VIDEO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(context: Context, permissionsList: List<String>, permissionLauncher: ActivityResultLauncher<Array<String>>?) {

        permissionLauncher?.launch(permissionsList.toTypedArray())
    }

     @OptIn(ExperimentalMaterial3Api::class)
     fun openBottomDrawer(context: Context, permissionLauncher: ActivityResultLauncher<Array<String>>?): Boolean {
        val permissionsList = mutableListOf<String>()
        Log.d("My Tag", "Debug message 1")
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("My Tag", "Android 13 and above")
            if (!checkReadMediaImagesPermission(context)) {
                permissionsList.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (!checkReadMediaVideoPermission(context)) {
                permissionsList.add(android.Manifest.permission.READ_MEDIA_VIDEO)
            }

        } else {
            Log.d("My Tag", "Android 12 and below")
            if (!checkReadExternalStoragePermission(context)) {
                Log.d("My tag", "read not given")
                permissionsList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (!checkWriteExternalStoragePermission(context)) {
                Log.d("My Tag", "write not given")
                permissionsList.add(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            }

        }
        if (permissionsList.isNotEmpty()) {
            Log.d("My Tag", "Permission list not empty")
            requestPermission(context, permissionsList, permissionLauncher)
            return false
        }
        else{
            Log.d("My Tag", "Permission list empty")
            Toast.makeText(context, "Permission Granted11", Toast.LENGTH_SHORT).show()
            return true
        }


    }






@Composable
@OptIn(ExperimentalMaterial3Api::class)

fun HiddenPageGalleryView(navController: NavController?, permissionLauncher: ActivityResultLauncher<Array<String>>?){
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
                .clickable {
                    navController?.navigate("image_viewer/${Uri.encode(image.toString())}")
                },
            contentScale = ContentScale.Crop
        )
    }


    fun loadImagesDynamically() {
        val imagesFiles = File(context.filesDir, "vault_gallery").listFiles() { file -> file.isFile }
            ?.map { file ->
                Uri.fromFile(file)
            } ?: emptyList<Uri>()
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
    fun AlertDialogBox(alertText: String, alertTitle: String, alertState: Boolean, onDismissRequest: () -> Unit, confirmButtonAction: ()-> Unit){
        if(alertState){
            AlertDialog(
                onDismissRequest = {onDismissRequest()},
                confirmButton = {
                    TextButton(
                        onClick = {confirmButtonAction()}
                    ) {
                        Text(text = "OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {onDismissRequest()}
                    ) {
                        Text(text = "Cancel")
                    }
                },
                title = { Text(alertTitle) },
                text = { Text(alertText) },
            )
        }
    }

    @Composable
    fun LaunchPhotoPicker(){
        println("LPP Launched")
        val storagePermissionAlertDialogState = remember { mutableStateOf(true) }
        if(!Environment.isExternalStorageManager()){
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data  = Uri.parse("package:${activity?.packageName}")
            }

            AlertDialogBox(
                alertText = "This App needs permission to access your files, this will be used to move your files in and out of the vault. Grant it in the next step",
                alertTitle = "Storage Permission required",
                alertState = storagePermissionAlertDialogState.value,
                onDismissRequest = {
                    storagePermissionAlertDialogState.value = false
                    launchPhotoPicker = false
                },
                confirmButtonAction = {
                    storagePermissionAlertDialogState.value = false
                    launchPhotoPicker = false
                    activity?.startActivity(intent)}
            )
            /*AlertDialog.Builder(context)
                .setTitle("Storage Permission required")
                .setMessage("This App needs permission to access your files, this will be used to move your files in and out of the vault. Grant it in the next step")
                .setPositiveButton("OK"){ dialog, _ ->
                    dialog.dismiss()
                    activity?.startActivity(intent)
                }
                .setNeutralButton("Cancel"){dialog, _ ->
                    dialog.dismiss()
                }
                .show()*/
            //activity?.startActivity(intent)

        } else{
            storagePermissionAlertDialogState.value = false
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
                            println("Add buttoin pressed")
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
            columns = GridCells.Fixed(3),
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
    HiddenPageGalleryView(null, null)
}
