package com.example.basiccalculator

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MediaHandler {
    companion object{

        private fun createVaultDirectory(context: Context, mediaType: String): File {
            val vaultDirectory = File(context.filesDir, "vault_gallery/${mediaType}")

            if (!vaultDirectory.exists()) {
                vaultDirectory.mkdirs()
            }
            return vaultDirectory
        }

        fun moveMediaToVault(context: Context, uris: List<Uri>) {
            uris.forEach { uri ->
                println(uri.toString())

                // Get the vault directories
                val vaultDirectoryImages = createVaultDirectory(context, "images")
                val vaultDirectoryVideos = createVaultDirectory(context, "videos")

                // Extract the file name from the URI (fallback to timestamp if null)
                val fileName = filenameFromUri(uri, context)

                // Create a file in the vault directory



                if(fileName.substringAfterLast(".") in listOf("png", "jpg", "jpeg")){
                    val destinationFile = File(vaultDirectoryImages, fileName)

                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

                    // Write the file to the vault directory
                    val outputStream = FileOutputStream(destinationFile)
                    inputStream?.copyTo(outputStream)


                    // Close the streams
                    inputStream?.close()
                    outputStream.close()


                    try {
                        val fileId = ContentUris.parseId(uri)
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
                    println("Image moved to vault: ${destinationFile.absolutePath}")
                }
                else if(fileName.substringAfterLast(".") in listOf("mp4", "mkv", "avi")){
                    val destinationFile = File(vaultDirectoryVideos, fileName)

                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

                    // Write the file to the vault directory
                    val outputStream = FileOutputStream(destinationFile)
                    inputStream?.copyTo(outputStream)


                    // Close the streams
                    inputStream?.close()
                    outputStream.close()


                    try {
                        val fileId = ContentUris.parseId(uri)
                        val selection = "${MediaStore.Images.Media._ID} = ?"
                        val selectionArgs = arrayOf(fileId.toString())

                        context.contentResolver.delete(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
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
                    println("Image moved to vault: ${destinationFile.absolutePath}")
                    println("File is a video")
                }


                // Open the input stream from the URI

            }
        }
        fun moveMediaToExtStorage(imageUri: Uri?, context: Context){
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

        fun deleteMedia(mediaUri: Uri, ){
            mediaUri.path?.let { File(it) }?.delete()

        }
    }
}