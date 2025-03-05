package com.example.basiccalculator

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import com.example.basiccalculator.ui.theme.darkOrange
import com.example.basiccalculator.ui.theme.poppinsFontFamily
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


val Context.preferencesDataStore by preferencesDataStore(name = "Preferences")

object Preferences{
    private val PASSWORD_KEY = stringPreferencesKey("vault_password")
    private val SECURITY_QUESTION_KEY = stringPreferencesKey("security_question")
    private val SECURITY_ANSWER_KEY = stringPreferencesKey("security_answer")

    suspend fun savePassword(context: Context, password: String){
        context.preferencesDataStore.edit { preferences ->
            preferences[PASSWORD_KEY] = password
        }
    }

    suspend fun saveSecurityQuestionAndAnswer(context: Context, securityQuestion: String, securityAnswer: String){
        context.preferencesDataStore.edit { preferences ->
            preferences[SECURITY_QUESTION_KEY] = securityQuestion
            preferences[SECURITY_ANSWER_KEY] = securityAnswer
        }
    }
    fun readPassword(context: Context): Flow<String?> {
        return context.preferencesDataStore.data.map{preferences ->
            preferences[PASSWORD_KEY]
       }
    }

    fun readSecurityQuestion(context: Context): Flow<String?> {
        return context.preferencesDataStore.data.map{ preferences ->
            preferences[SECURITY_QUESTION_KEY]
        }
    }

    fun readSecurityAnswer(context: Context): Flow<String?> {
        return context.preferencesDataStore.data.map{ preferences ->
            preferences[SECURITY_ANSWER_KEY]
        }
    }

    suspend fun isPasswordSet(context: Context): Boolean {
        return readPassword(context).first() != null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SettingsActivity(navController: NavController? = null){

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 40.sp,
                        fontFamily = poppinsFontFamily,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController?.popBackStack()
                        }
                    ) {
                        Icon (
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                            )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkOrange
                )
            )
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            //Spacer(modifier = Modifier.size(30.dp))
            Row(modifier = Modifier.fillMaxWidth()){

                TextButton(
                    onClick = {
                        navController?.navigate("reset_password")
                    }
                ){
                    Text(
                        text = "Reset Password",
                        fontSize = 25.sp,
                        fontFamily = poppinsFontFamily,
                        color = Color.Black

                    )
                }
            }
        }
    }

}
