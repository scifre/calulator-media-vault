package com.example.basiccalculator
import android.annotation.SuppressLint
import android.content.Context
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
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
fun calculateExpression(expression: String, navController: NavController?): String {
    if (expression == "9026") {
        navController?.navigate("hidden")
    }
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


suspend fun navigateToVault(password: String, navController: NavController, scope: CoroutineScope, context: Context){
    val enteredPasswordHash = sha256(password)
    val savedPasswordHash = Preferences.readPassword(context).first()

    if(enteredPasswordHash==savedPasswordHash){
        navController.navigate("hidden")
    }
}
