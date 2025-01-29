package com.example.basiccalculator
import android.annotation.SuppressLint
import androidx.navigation.NavController
import net.objecthunter.exp4j.ExpressionBuilder

fun updateScreen(curInt: String, newInt: String, clear: Boolean = false): String {
    return if(clear) ""
    else {
        if(curInt=="0") newInt
        else curInt+newInt
    }
}


@SuppressLint("DefaultLocale")
fun calculateExpression(expression: String, navController: NavController): String {
    if (expression == "9026") {
        navController.navigate("hidden")
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
