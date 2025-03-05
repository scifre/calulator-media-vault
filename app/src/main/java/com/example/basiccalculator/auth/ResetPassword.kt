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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.basiccalculator.Preferences
import com.example.basiccalculator.ui.theme.darkOrange
import com.example.basiccalculator.ui.theme.lightOrange
import com.example.basiccalculator.ui.theme.poppinsFontFamily
import com.example.basiccalculator.sha256
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ResetPassword(navController: NavController? = null){
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var securityQuestion by remember { mutableStateOf("") }
    var securityAnswer by remember { mutableStateOf("") }

    var isSecurityQuestionFocused by remember { mutableStateOf(false) }
    var isSecurityQuestionDropdownExpanded by remember { mutableStateOf(false) }
    var isSecurityAnswerFocused by remember { mutableStateOf(false) }
    var isOldPasswordFocused by remember { mutableStateOf(false) }
    var isNewPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmNewPasswordFocused by remember { mutableStateOf(false) }
    var isNextEnabled by remember { mutableStateOf(false) }
    var isResetPinEnabled by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val pagerState = rememberPagerState (
        pageCount = { 2 },
        initialPage = 0
    )

    val securityQuestionsList = listOf(
        "What is your favorite color?",
        "What is your favorite animal?",
        "What was the first movie you saw in a theater?",
        "What wad the name of your primary school teacher?",
        "What is the name of your favorite pet?",
        "What is your favorite food?"
    )

    suspend fun onClickNext(){
        val savedSecurityQuestion = Preferences.readSecurityQuestion(context = context).first()
        val savedSecurityAnswer = Preferences.readSecurityAnswer(context = context).first()

        if(securityQuestion == savedSecurityQuestion && securityAnswer == savedSecurityAnswer) {
            scope.launch {
                pagerState.animateScrollToPage(1)
            }
        }
    }
    fun onClickResetPin(newPassword: String){
        val newPasswordHash = sha256(newPassword)
        scope.launch{
            Preferences.savePassword(context = context, password = newPasswordHash)
            navController?.navigate("main")
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController?.navigate("main"){
                                popUpTo("main") {
                                    inclusive = true
                                }
                            }


                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "back icon",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                title = { Text("") }

            )
        }
    ) {paddingValues ->
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
                }
        ) {
            Spacer(modifier = Modifier.size(50.dp))

            Text(
                text = "Reset your vault PIN",
                fontSize = 40.sp,
                color = Color(0xFFE0801F),
                fontWeight = FontWeight.Bold,
                fontFamily = poppinsFontFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(30.dp))
            HorizontalPager(
                state = pagerState
            ) { page ->
                when (page) {
                    0 -> {
                        Column {
                            Text(
                                text = "Answer your security question to reset your vault PIN",
                                fontSize = 18.sp,
                                modifier = Modifier,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                fontFamily = poppinsFontFamily

                            )


                            Spacer(modifier = Modifier.size(30.dp))

                            ExposedDropdownMenuBox(
                                expanded = isSecurityQuestionDropdownExpanded,
                                onExpandedChange = {
                                    isSecurityQuestionDropdownExpanded =
                                        !isSecurityQuestionDropdownExpanded
                                }
                            ) {
                                OutlinedTextField(
                                    value = securityQuestion,
                                    label = {
                                        Text(
                                            text = "Security Question",
                                            fontSize = if (securityQuestion.isNotEmpty() || isSecurityQuestionFocused) 15.sp else 20.sp,
                                            fontFamily = poppinsFontFamily,
                                            color = if (securityQuestion.isNotEmpty() || isSecurityQuestionFocused) darkOrange else Color.DarkGray

                                        )
                                    },
                                    placeholder = {
                                        Text(
                                            text = "Select a question",
                                            fontSize = 20.sp
                                        )
                                    },
                                    textStyle = TextStyle(
                                        fontSize = 20.sp
                                    ),
                                    onValueChange = { newText: String ->
                                        securityQuestion = newText
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = isSecurityQuestionDropdownExpanded,
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                        .onFocusChanged { focusState ->
                                            isSecurityQuestionFocused = focusState.isFocused
                                        },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = lightOrange,
                                        focusedBorderColor = darkOrange
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = isSecurityQuestionDropdownExpanded,
                                    onDismissRequest = {
                                        isSecurityQuestionDropdownExpanded = false
                                    },
                                    modifier = Modifier.fillMaxWidth()

                                ) {
                                    securityQuestionsList.forEach { selectedOption ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = selectedOption,
                                                    fontSize = 20.sp,
                                                    modifier = Modifier.fillMaxWidth(),
                                                )
                                            },
                                            onClick = {
                                                securityQuestion = selectedOption
                                                isSecurityQuestionDropdownExpanded = false
                                            }
                                        )
                                    }
                                }

                            }
                            Spacer(modifier = Modifier.size(20.dp))
                            OutlinedTextField(
                                value = securityAnswer,
                                onValueChange = { newText ->
                                    securityAnswer = newText.lowercase()
                                },
                                label = {
                                    Text(
                                        text = "Answer",
                                        fontSize = if (securityAnswer.isNotEmpty() || isSecurityAnswerFocused) 15.sp else 20.sp,
                                        color = if (isSecurityAnswerFocused) darkOrange else Color.DarkGray,
                                        fontFamily = poppinsFontFamily
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        isSecurityAnswerFocused = focusState.isFocused
                                    },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = lightOrange,
                                    focusedBorderColor = darkOrange
                                ),
                                textStyle = TextStyle(
                                    fontSize = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.size(20.dp))

                            if(securityQuestion.isNotEmpty() && securityAnswer.isNotEmpty()){
                                isNextEnabled = true
                            }
                            else{
                                isNextEnabled = false
                            }

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(65.dp),
                                enabled = isNextEnabled,
                                shape = RoundedCornerShape(6.dp),
                                onClick = {
                                    scope.launch{
                                        onClickNext()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = darkOrange,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "Next",
                                    fontSize = 20.sp,
                                    fontFamily = poppinsFontFamily
                                )
                                Spacer(Modifier.size(5.dp))
                                Icon(
                                    contentDescription = "next icon",
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                )
                            }
                        }
                    }
                    1-> {
                        Column {
                            Text(
                                text = "Enter your old and new PINs",
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center

                            )
                            Spacer(modifier = Modifier.size(30.dp))

                            OutlinedTextField(
                                value = oldPassword,
                                label = {
                                    Text(
                                        text = "Old PIN",
                                        fontSize = if (oldPassword.isNotEmpty() || isOldPasswordFocused) 15.sp else 20.sp,
                                        color = if (oldPassword.isNotEmpty() || isOldPasswordFocused) darkOrange else Color.DarkGray,
                                        fontFamily = poppinsFontFamily
                                    )
                                },
                                onValueChange = {newText->
                                    if(newText.length <= 6 && newText.all{c: Char -> c.isDigit()}){
                                        oldPassword = newText
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        isOldPasswordFocused = focusState.isFocused
                                    },
                                textStyle = TextStyle(
                                    fontFamily = poppinsFontFamily,
                                    fontSize = 20.sp
                                ),
                                placeholder = {
                                    Text(
                                        text = "Enter your old PIN",
                                        fontFamily = poppinsFontFamily,
                                        fontSize = 20.sp,
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = lightOrange,
                                    focusedBorderColor = darkOrange
                                )
                            )

                            Spacer(Modifier.size(20.dp))
                            OutlinedTextField(
                                value = newPassword,
                                label = {
                                    Text(
                                        text = "New PIN",
                                        fontSize = if (newPassword.isNotEmpty() || isNewPasswordFocused) 15.sp else 20.sp,
                                        color = if (newPassword.isNotEmpty() || isNewPasswordFocused) darkOrange else Color.DarkGray,
                                        fontFamily = poppinsFontFamily
                                    )
                                },
                                onValueChange = {newText->
                                    if(newText.length <= 6 && newText.all{c: Char -> c.isDigit()}){
                                        newPassword = newText
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        isNewPasswordFocused = focusState.isFocused
                                    },
                                textStyle = TextStyle(
                                    fontFamily = poppinsFontFamily,
                                    fontSize = 20.sp
                                ),
                                placeholder = {
                                    Text(
                                        text = "Enter your new PIN",
                                        fontFamily = poppinsFontFamily,
                                        fontSize = 20.sp,
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = lightOrange,
                                    focusedBorderColor = darkOrange
                                )
                            )
                            if(newPassword.isNotEmpty() && newPassword.length<6){
                                Text(
                                    text =  "The new PIN should be 6 digits long",
                                    fontFamily = poppinsFontFamily,
                                    color = Color.Red
                                )
                            }
                            Spacer(Modifier.size(12.dp))
                            OutlinedTextField(
                                value = confirmNewPassword,
                                label = {
                                    Text(
                                        text = "Confirm new PIN",
                                        fontSize = if (confirmNewPassword.isNotEmpty() || isConfirmNewPasswordFocused) 15.sp else 20.sp,
                                        color = if (confirmNewPassword.isNotEmpty() || isConfirmNewPasswordFocused) darkOrange else Color.DarkGray,
                                        fontFamily = poppinsFontFamily
                                    )
                                },
                                onValueChange = {newText->
                                    if(newText.length <= 6 && newText.all{c: Char -> c.isDigit()}){
                                        confirmNewPassword = newText
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        isConfirmNewPasswordFocused = focusState.isFocused
                                    },
                                textStyle = TextStyle(
                                    fontFamily = poppinsFontFamily,
                                    fontSize = 20.sp
                                ),
                                placeholder = {
                                    Text(
                                        text = "Confirm your new PIN",
                                        fontFamily = poppinsFontFamily,
                                        fontSize = 20.sp,
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = lightOrange,
                                    focusedBorderColor = darkOrange
                                )
                            )
                            if(newPassword != confirmNewPassword){
                                Text(
                                    text = "The PINs should match",
                                    color = Color.Red,
                                    fontSize = 15.sp
                                )

                            }
                            if(newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty() && newPassword == confirmNewPassword){
                                isResetPinEnabled = true
                            }
                            else{
                                isResetPinEnabled = false
                            }
                            Spacer(Modifier.size(22.dp))
                            Button(
                                onClick = {
                                    if(newPassword == confirmNewPassword){
                                        onClickResetPin(newPassword = newPassword)
                                    }
                                },
                                enabled = isResetPinEnabled,
                                shape = RoundedCornerShape(corner = CornerSize(6.dp)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = darkOrange,
                                    contentColor = Color.White
                                )
                            ){
                                Text(
                                    text = "Reset PIN",
                                    fontSize = 20.sp,
                                    fontFamily = poppinsFontFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}