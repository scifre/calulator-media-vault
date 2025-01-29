package com.example.basiccalculator


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:width=411dp,height=891dp", showBackground = true)
@Composable
fun SettingsView(){
    val scope = rememberCoroutineScope()
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                        Text(
                            text = "Settings",
                            fontSize = 40.sp,
                            color = Color.Green
                        )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(40.dp)
                    )
                }

            )
        }
    )
    {paddingValues->
            Column(modifier = Modifier.padding(paddingValues).padding(start=10.dp, end = 10.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Setting 1",
                    fontSize = 30.sp,
                    color = Color.Black,
                    //modifier = Modifier.padding(all = 10.dp)
                )
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = true,
                    onCheckedChange = { /*TODO*/ }
                )
                }
            }
        }
}