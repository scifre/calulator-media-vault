package com.example.basiccalculator

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.first
import net.objecthunter.exp4j.ExpressionBuilder
import java.security.MessageDigest

fun updateScreen(curInt: String, newInt: String, clear: Boolean = false): String {
    return if(clear) ""
    else {
        if(curInt=="0") newInt
        else curInt+newInt
    }
}


@SuppressLint("DefaultLocale")
fun calculateExpression(expression: String): String {
    val exp = ExpressionBuilder(expression).build().evaluate()

    return if (exp % 1 == 0.0) String.format("%.0f", exp)
    else return String.format("%.2f", exp)


}

fun backspace(expression: String): String {

    return if (expression.isEmpty())
    {
        ""
    }
    else
    {
        expression.dropLast(1)
    }

}
fun sha256(input:String):String{
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
    val hexString = StringBuilder()

    for(bytes in hashBytes){
        val hex = Integer.toHexString(0xff and bytes.toInt())
        if(hex.length == 1){
            hexString.append('0')
        }
        hexString.append(hex)
    }

    return hexString.toString()
}


suspend fun navigateToVault(password: String, navController: NavController, context: Context){
    val enteredPasswordHash = sha256(password)
    val savedPasswordHash = Preferences.readPassword(context).first()

    if(enteredPasswordHash==savedPasswordHash){
        navController.navigate("hidden")
        Toast.makeText(context, "Vault Unlocked", Toast.LENGTH_SHORT).show()
    }
}



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
                    fontSize = 18.sp,
                    color = darkOrange
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {onDismissRequest()}
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 18.sp,
                    color = darkOrange
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
                    contentDescription = "Alert Icon",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    )

}