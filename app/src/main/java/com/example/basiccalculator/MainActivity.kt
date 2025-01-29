package com.example.basiccalculator


import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setting up permissionLauncher which is needs to be setup in OnCreate
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allPermissionsGranted = permissions.values.all { it }
            if (allPermissionsGranted) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            //BasicCalculator(navController)

            NavHost(navController = navController, startDestination = "main") {
                composable("main") { BasicCalculator(navController) }
                composable("hidden") {HiddenPageGalleryView(navController = navController, permissionLauncher = permissionLauncher)}
                composable("image_viewer/{imageUri}") { backStackEntry ->
                        val imageUri = backStackEntry.arguments?.getString("imageUri").let { Uri.parse(it) }
                        ImageViewer(navController, imageUri)
                }


            }
        }
    }


    @Composable
    fun CalcScreen(num: String, modifier: Modifier = Modifier) {

        Box(
            modifier = modifier.fillMaxWidth().background(color = Color(0xFFECCBF0)),

            ) {
            Text(
                text = num,
                modifier = Modifier.wrapContentSize().align(Alignment.BottomEnd),
                color = Color.Gray,
                fontSize = 100.sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        blurRadius = 2f,
                        offset = Offset(3f, 3f),
                    )
                ),

                textAlign = TextAlign.End,
                softWrap = true,
                maxLines = 6,
                overflow = TextOverflow.Clip

            )

        }
    }

    @Composable
    fun CalcButton(sym: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
        Button(
            onClick = { onClick() },
            modifier = modifier
                //.padding(2.dp)
                .size(100.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            border = BorderStroke(width = 0.5.dp, color = Color.White)
        ) {
            Text(
                text = sym,
                fontSize = 55.sp,
                color = Color.Gray,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        blurRadius = 6f,
                        offset = Offset(3f, 3f)
                    )
                )
            )
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BasicCalculator(navController: NavController) {


        var displayNum: String by remember { mutableStateOf("0") }
        val drawerState =
            rememberDrawerState(initialValue = DrawerValue.Closed)//change initial state of drawer here
        val scope = rememberCoroutineScope()
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        text = "Menu",
                        modifier = Modifier.padding(16.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text(text = "Calc 1") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Calc 2") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                }
            }

        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Basic Calculator",
                                fontSize = 40.sp,
                                color = Color.Green
                            )

                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Menu",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    )
                },


                )
            { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.wrapContentSize()) {
                            CalcScreen(num = displayNum, modifier = Modifier.weight(1f))
                            //First Row --> C % <-
                            Row() {
                                CalcButton(
                                    sym = "C",
                                    modifier = Modifier.weight(1f),
                                    onClick = { displayNum = updateScreen(displayNum, "", true) }
                                )
                                CalcButton(
                                    sym = "%",
                                    modifier = Modifier.weight(1f),
                                    onClick = { displayNum = updateScreen(displayNum, "%") }
                                )
                                Button(
                                    onClick = { displayNum = backspace(displayNum) },
                                    modifier = Modifier
                                        //.padding(2.dp)
                                        .size(100.dp)
                                        .weight(1f),
                                    shape = RectangleShape,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color.Black
                                    ),
                                    border = BorderStroke(width = 0.5.dp, color = Color.White)
                                ) {
                                    Box() {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = R.drawable.backspace_vec),
                                            contentDescription = "Backspace_icon",
                                            tint = Color.Black,
                                            modifier = Modifier.size(65.dp)
                                                .offset(x = 1.dp, y = 1.dp)

                                        )
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = R.drawable.backspace_vec),
                                            contentDescription = "Backspace_icon",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(65.dp)

                                        )
                                    }
                                }

                            }
                            //Second Row --> 1 2 3 +
                            Row() {
                                for (i in 1..3) {
                                    CalcButton(
                                        sym = i.toString(),
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            displayNum = updateScreen(displayNum, i.toString())
                                        }
                                    )
                                }
                                CalcButton(
                                    sym = "+",
                                    modifier = Modifier.weight(1f),
                                    onClick = { displayNum = updateScreen(displayNum, "+") }
                                )
                            }
                            //Third Row --> 4 5 6 -
                            Row() {
                                for (i in 4..6) {
                                    CalcButton(
                                        sym = i.toString(),
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            displayNum = updateScreen(displayNum, i.toString())
                                        }
                                    )
                                }
                                CalcButton(
                                    sym = "-",
                                    modifier = Modifier.weight(1f),
                                    onClick = { displayNum = updateScreen(displayNum, "-") }
                                )
                            }
                            //Fourth Row --> 7 8 9 *
                            Row() {
                                for (i in 7..9) {
                                    CalcButton(
                                        sym = i.toString(),
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            displayNum = updateScreen(displayNum, i.toString())
                                        }
                                    )
                                }
                                CalcButton(
                                    sym = "*",
                                    modifier = Modifier.weight(1f),
                                    onClick = { displayNum = updateScreen(displayNum, "*") }
                                )
                            }
                            //Fifth Row --> 0 . = /
                            Row()
                            {
                                CalcButton(
                                    sym = ".",
                                    modifier = Modifier.weight(1f),
                                    onClick = { displayNum = updateScreen(displayNum, ".") }
                                )
                                CalcButton(
                                    sym = "0",
                                    modifier = Modifier.weight(1f),
                                    onClick = { displayNum = updateScreen(displayNum, "0") }
                                )
                                CalcButton(
                                    sym = "=",
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        displayNum = calculateExpression(
                                            expression = displayNum,
                                            navController = navController
                                        )
                                    }
                                )
                                CalcButton(
                                    sym = "/",
                                    modifier = Modifier.weight(1f),
                                    onClick = { displayNum = updateScreen(displayNum, "/") }
                                )

                            }


                        }
                    }
                }
            }
        }

    }

    @Preview(device = "spec:width=411dp,height=891dp", showBackground = true)
    @Composable
    fun BasicCalculatorPreview() {
        //BasicCalculator()
    }
}


