package com.example.basiccalculator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:width=411dp,height=891dp", showBackground = true)
@Composable
fun SetSecurityQuestion(navController: NavController? = null, passwordHash: String = "acs"){
    val context = LocalContext.current
    var isSecurityQuestionDropdownExpanded by remember { mutableStateOf(false) }
    var selectedSecurityQuestion by remember { mutableStateOf("") }
    var isSecurityQuestionFocused by remember { mutableStateOf(false) }
    var securityAnswer by remember { mutableStateOf("") }
    var isSecurityAnswerFocused by remember { mutableStateOf(false) }
    var isSetPINClickable by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val securityQuestionsList = listOf(
        "What is your favorite color?",
        "What is your favorite animal?",
        "What was the first movie you saw in a theater?",
        "What wad the name of your primary school teacher?",
        "What is the name of your favorite pet?",
        "What is your favorite food?"
    )
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 10.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ){
                focusManager.clearFocus()
            }
    ) {
        Spacer(modifier = Modifier.size((50.dp)))
        Text(
            text = "Vault Setup",
            fontSize = 60.sp
        )
        Spacer(
            modifier = Modifier
                .size(50.dp)
        )
        Text(
            text = "Security Question",
            fontSize = 40.sp
        )
        Spacer(
            modifier = Modifier
                .size(30.dp)
        )
        Text(
            text = "Setup a security question so that if you forget your PIN you can reset it.",
            fontSize = 30.sp,

        )
        Spacer(
            modifier = Modifier
                .size(50.dp)
        )
        ExposedDropdownMenuBox(
            expanded = isSecurityQuestionDropdownExpanded,
            onExpandedChange = {
                isSecurityQuestionDropdownExpanded = !isSecurityQuestionDropdownExpanded
            }
        ) {
            OutlinedTextField(
                value = selectedSecurityQuestion,
                readOnly = true,
                label = {
                    Text(
                        text = "Security Question",
                        fontSize = if(selectedSecurityQuestion.isNotEmpty()||isSecurityQuestionFocused)15.sp else 20.sp
                    )
                },
                placeholder = {
                    Text(
                        text = "Select a question",
                        fontSize = 20.sp
                    )
                },
                textStyle = TextStyle(
                        fontSize = 20.sp,
                ),
                singleLine = true,
                onValueChange = {newText:String ->
                    selectedSecurityQuestion = newText
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
                    }

            )
            ExposedDropdownMenu(
                expanded = isSecurityQuestionDropdownExpanded,
                onDismissRequest = {
                    isSecurityQuestionDropdownExpanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                securityQuestionsList.forEach(){selectedOption ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = selectedOption,
                                fontSize = 20.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            selectedSecurityQuestion = selectedOption
                            isSecurityQuestionDropdownExpanded = false
                        }
                    )
                }
            }

        }
        Spacer(
            modifier = Modifier
                .size(30.dp)
        )
        OutlinedTextField(
            value = securityAnswer,
            label = {
                Text(
                    text = "Answer",
                    fontSize = if(securityAnswer.isNotEmpty()||isSecurityAnswerFocused) 15.sp else 20.sp
                )
            },
            onValueChange = {newText:String ->
                if(newText.length <=10) securityAnswer = newText.lowercase()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isSecurityAnswerFocused = focusState.isFocused
                },
            placeholder = {
                Text(
                    text = "Enter Answer",
                    fontSize = 20.sp,
                    color = Color.LightGray
                )
            },
            textStyle = TextStyle(
                fontSize = 20.sp
            )



        )
        Spacer(modifier = Modifier.size(50.dp))

        if(securityAnswer.isNotEmpty()){
            isSetPINClickable = true
        }
        else{
            isSetPINClickable = false
        }


        fun onSetPINClicked(
            securityAnswer: String,
            selectedSecurityQuestion: String,
            passwordHash: String,

        ){
            scope.launch() {
                Preferences.saveSecurityQuestionAndAnswer(context = context, securityQuestion = selectedSecurityQuestion, securityAnswer = securityAnswer)
                Preferences.savePassword(context = context, password = passwordHash)
                navController?.navigate("main")
            }

        }

        Button(
            onClick = {onSetPINClicked(securityAnswer, selectedSecurityQuestion, passwordHash)},
            enabled = (isSetPINClickable)

        ) { Text(
            text = "Set PIN",
            fontSize = 20.sp
        )}



    }
}