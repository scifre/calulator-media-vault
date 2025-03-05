package com.example.basiccalculator.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.basiccalculator.sha256
import com.example.basiccalculator.ui.theme.darkOrange
import com.example.basiccalculator.ui.theme.poppinsFontFamily

@Composable
fun SetPasswordScreen(navController: NavController?=null) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmPasswordFocused by remember { mutableStateOf(false) }
    var isSetPasswordButtonClickable by remember { mutableStateOf(false) }


    fun onClickSetPassword(password: String, confirmPassword: String) {
        if (password == confirmPassword) {
            val passwordHash = sha256(password)
            navController?.navigate("security_question/$passwordHash")
        }

    }

    val focusManager = LocalFocusManager.current
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = 16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                },
        ) {
            Spacer(modifier = Modifier.size(50.dp))
            Text(
                text = "Setup a Vault PIN",
                fontSize = 40.sp,
                color = Color(0xFFE0801F),
                fontWeight = FontWeight.Bold,
                fontFamily = poppinsFontFamily
            )
            Spacer(modifier = Modifier.size(30.dp))
            Text(
                text = "Hello! Set up your vault PIN, that will be used to access it.",
                fontSize = 18.sp,
                modifier = Modifier,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                fontFamily = poppinsFontFamily

            )
            Spacer(modifier = Modifier.size(50.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { newText: String ->
                    if (newText.length <= 6 && newText.all { c: Char -> c.isDigit() }) {
                        password = newText
                    }
                },
                label = {
                    Text(
                        text = "Enter PIN",
                        color = if (isPasswordFocused) {
                            Color(0xFFE0801F)
                        } else Color.DarkGray,
                        fontSize = if (password.isNotEmpty() || isPasswordFocused) 15.sp else 20.sp,
                        modifier = Modifier


                    )
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),

                textStyle = TextStyle(
                    fontSize = 20.sp
                ),
                singleLine = true,
                placeholder = { Text("Enter 6-digit PIN") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isPasswordFocused = focusState.isFocused
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFFBE8CD),
                    focusedBorderColor = Color(0xFFE0801F)
                )

            )
            Text(
                text = if (password.isNotEmpty() && password.length < 6) {
                    "PIN should be 6 digits"
                } else {
                    ""
                },
                modifier = Modifier.padding(top = 5.dp),
                color = Color.Red,
                fontSize = 15.sp
            )
            //Spacer(modifier = Modifier.size(30.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { newText: String ->
                    if (newText.length <= 6 && newText.all { c: Char -> c.isDigit() }) {
                        confirmPassword = newText
                    }
                },
                label = {
                    Text(
                        text = "Confirm PIN",
                        color = Color.DarkGray,
                        fontSize = if (confirmPassword.isNotEmpty() || isConfirmPasswordFocused) 15.sp else 20.sp,
                        modifier = Modifier
                    )
                },
                textStyle = TextStyle(
                    fontSize = 20.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                singleLine = true,
                placeholder = { Text("Enter 6-digit PIN") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isConfirmPasswordFocused = focusState.isFocused
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFFBE8CD),
                    focusedBorderColor = Color(0xFFE0801F),
                )
            )

            Text(
                text = if (confirmPassword.isNotEmpty() && (password != confirmPassword)) {
                    "PINs should match"
                } else {
                    ""
                },
                modifier = Modifier.padding(top = 5.dp),
                color = Color.Red,
                fontSize = 15.sp
            )
            if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword) {
                isSetPasswordButtonClickable = true
            } else {
                isSetPasswordButtonClickable = false
            }

            //Spacer(modifier = Modifier.size(50.dp))
            Button(
                onClick = { onClickSetPassword(password, confirmPassword) },
                enabled = isSetPasswordButtonClickable,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),

                colors = ButtonDefaults.buttonColors(
                    containerColor = darkOrange
                ),
                shape = RoundedCornerShape(5.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp
                )
            ) {
                Text(
                    text = "Set Password",
                    fontSize = 20.sp
                )
            }
        }
    }
}