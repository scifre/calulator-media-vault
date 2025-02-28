package com.example.basiccalculator


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basiccalculator.auth.ResetPassword
import com.example.basiccalculator.auth.SetSecurityQuestion
import com.example.basiccalculator.mediaViewers.ImageViewer
import com.example.basiccalculator.mediaViewers.VideoViewer
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setting up permissionLauncher which is needs to be setup in OnCreate

        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            //BasicCalculator(navController)
            var isPasswordSet: Boolean? by remember { mutableStateOf(null) }
            LaunchedEffect(Unit) {
                isPasswordSet = Preferences.isPasswordSet(context = this@MainActivity)
            }
            fun AnimatedContentTransitionScope<*>.splitExitTransition(): ExitTransition {
                return slideOutVertically(
                    targetOffsetY = { fullHeight -> if (fullHeight > 0) -fullHeight / 2 else fullHeight / 2  },
                    animationSpec = tween(700)
                ) + fadeOut(animationSpec = tween(700))
            }

            NavHost(
                navController = navController,
                startDestination = when(isPasswordSet){
                    true -> "main"
                    false -> "set_password"
                    else -> "app_opener"
                }
            ) {
                composable(
                    route ="main"
                ) { BasicCalculator(navController) }
                composable(
                    route = "hidden"
                ) {HiddenPageGalleryView(navController = navController)}
                composable(
                    route = "image_viewer/{encodedUri}",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = {it},
                            animationSpec = tween(400)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = {it},
                            animationSpec = tween(400)
                        )
                    }
                ) { backStackEntry ->
                        val encodedUri = backStackEntry.arguments?.getString("encodedUri")
                        ImageViewer(navController, encodedUri)
                }
                composable(
                    route = "set_password",
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = {-it},
                            animationSpec = tween(durationMillis = 500)
                        )
                    }
                ) { SetPasswordScreen(navController = navController) }

                composable(
                    route = "security_question/{passwordHash}",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = {it}
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = {-it}
                        )
                    }
                ) {backStackEntry ->
                    val passwordHash = backStackEntry.arguments?.getString("passwordHash")
                    if (passwordHash != null) {
                        SetSecurityQuestion(navController = navController, passwordHash = passwordHash)
                    }
                }
                composable(
                    route = "video_player/{encodedUri}",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = {it},
                            animationSpec = tween(500)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = {it},
                            animationSpec = tween(500)
                        )
                    }
                ){backstackEntry ->
                    val encodedUri = backstackEntry.arguments?.getString("encodedUri")
                    VideoViewer(navController = navController, encodedUri = encodedUri)
                }
                composable("app_opener") { Box(modifier = Modifier.fillMaxSize())}
                composable(
                    route = "reset_password",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = {it},
                            animationSpec = tween(500)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = {it},
                            animationSpec = tween(500)
                        )
                    }
                ) { ResetPassword(navController) }

                composable(
                    route = "settings",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = {it},
                            animationSpec = tween(500)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = {it},
                            animationSpec = tween(500)
                        )
                    }
                ) { SettingsActivity(navController)  }


            }
        }
    }


    @Composable
    fun CalcScreen(num: String, modifier: Modifier = Modifier) {

        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(color = Color.White),

            ) {
            Text(
                text = num,
                fontFamily = poppinsFontFamily,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomEnd),
                color = Color.DarkGray,
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
        TextButton(
            onClick = { onClick() },
            modifier = modifier
                //.padding(2.dp)
                .size(100.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = lightOrange),
            //border = BorderStroke(width = 0.5.dp, color = Color.White),

        ) {
            Text(
                text = sym,
                fontSize = 55.sp,
                fontFamily = poppinsFontFamily,
                color = darkOrange,
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


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Preview(showBackground = true)
    @Composable
    fun BasicCalculator(navController: NavController? = null) {
        val context = LocalContext.current
        var displayNum: String by remember { mutableStateOf("0") }

        val scope = rememberCoroutineScope()



        Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Calculator",
                                fontSize = 40.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = poppinsFontFamily
                            )

                        },
                        colors = TopAppBarColors(
                            containerColor = darkOrange,
                            scrolledContainerColor = Color.White,
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                },

                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {

                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.wrapContentSize()) {
                                CalcScreen(num = displayNum, modifier = Modifier.weight(1f))
                                //First Row --> C % <-
                                Row {
                                    CalcButton(
                                        sym = "C",
                                        modifier = Modifier
                                            .weight(1f),
                                        onClick = {
                                            displayNum = updateScreen(displayNum, "", true)
                                        }
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
                                        colors = ButtonDefaults
                                            .buttonColors(
                                                containerColor = lightOrange,
                                            )
                                    ) {
                                        Box{
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = R.drawable.backspace_vec),
                                                contentDescription = "Backspace_icon",
                                                tint = Color.Black,
                                                modifier = Modifier
                                                    .size(65.dp)
                                                    .offset(x = 1.dp, y = 1.dp)

                                            )
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = R.drawable.backspace_vec),
                                                contentDescription = "Backspace_icon",
                                                tint = darkOrange,
                                                modifier = Modifier.size(65.dp)

                                            )
                                        }
                                    }

                                }

                                //Second Row --> 1 2 3 +
                                Row {
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
                                Row {
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
                                Row {
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
                                    Box(modifier = Modifier
                                        .size(100.dp)
                                        .weight(1f)
                                        .background(color = lightOrange)
                                        .combinedClickable(
                                            onClick = {
                                                displayNum = updateScreen(displayNum, ".")
                                            },
                                            onLongClick = {
                                                scope.launch {
                                                    if (navController != null) {
                                                        navigateToVault(
                                                            password = displayNum,
                                                            navController = navController,
                                                            context = context
                                                        )
                                                    }
                                                }
                                            }
                                        ),
                                        contentAlignment = Alignment.Center


                                    ){
                                            Text(
                                                text = ".",
                                                fontSize = 55.sp,
                                                color = darkOrange,
                                                style = TextStyle(
                                                    shadow = Shadow(
                                                        color = Color.Black,
                                                        blurRadius = 6f,
                                                        offset = Offset(3f, 3f)
                                                    )
                                                )
                                            )

                                    }

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
                                                expression = displayNum
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



    @Preview(showBackground = true)
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






