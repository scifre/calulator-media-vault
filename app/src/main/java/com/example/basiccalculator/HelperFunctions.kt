package com.example.basiccalculator

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

fun filenameFromUri(uri: Uri, context: Context): String {
    if(uri.scheme.equals("content")){
        print("scheme is content")
        var filename = ""
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        try {
            if(cursor != null && cursor.moveToFirst()){
                filename = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        } catch (e: IllegalArgumentException){
            println("File picker" + e.message.toString())
        }
        finally {
            cursor?.close()
        }

        return filename
    }
    return "media_${System.currentTimeMillis()}"
}


