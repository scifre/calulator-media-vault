package com.example.basiccalculator

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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

    suspend fun isPasswordSet(context: Context): Boolean {
        return readPassword(context).first() != null
    }
}

