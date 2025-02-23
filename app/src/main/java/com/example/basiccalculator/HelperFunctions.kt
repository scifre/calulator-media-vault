package com.example.basiccalculator

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp

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