package com.example.basiccalculator



import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.basiccalculator.MediaHandler.Companion.moveMediaToVault
import com.example.basiccalculator.Thumbnails.Companion.ImageThumbnail
import com.example.basiccalculator.Thumbnails.Companion.VideoThumbnail
import kotlinx.coroutines.launch
import java.io.File


@androidx.annotation.OptIn(UnstableApi::class)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HiddenPageGalleryView(navController: NavController){
    val context = LocalContext.current
    val activity = context as? Activity
    var launchPhotoPicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val images = remember{ mutableStateListOf<Uri>() }
    val videos = remember{ mutableStateListOf<Uri>() }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                navController.navigate("main") // Navigates back to calculator when app goes to background
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    fun loadMediaDynamically() {
        val imagesFiles = File(context.filesDir, "vault_gallery/images")
            .listFiles { file -> file.isFile}
            ?.map(Uri::fromFile) ?: emptyList<Uri>()
        val videoFiles = File(context.filesDir, "vault_gallery/videos")
            .listFiles { file -> file.isFile}
            ?.map(Uri::fromFile)?: emptyList<Uri>()

        images.clear()
        images.addAll(imagesFiles)

        videos.clear()
        videos.addAll(videoFiles)
    }

    LaunchedEffect(Unit) {
        loadMediaDynamically()
    }


    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()){
            Toast.makeText(context, "Selected ${uris.size} items", Toast.LENGTH_SHORT).show()
            moveMediaToVault(context, uris)
            loadMediaDynamically()
        }
        else{
            Toast.makeText(context, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }


    @Composable
    fun LaunchPhotoPicker(){
        if(!Environment.isExternalStorageManager()){
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${activity?.packageName}")
            }

            AlertDialogBox(
                alertText = "Calculator needs permission to access your files, this will be used to move your files in and out of the vault. Grant it in the next step",
                alertTitle = "Storage Permission required",
                onDismissRequest = {
                    launchPhotoPicker = false
                },
                confirmButtonAction = {
                    launchPhotoPicker = false
                    activity?.startActivity(intent)},
                alertIcon = Icons.Filled.Info
            )

        } else{
            launchPhotoPicker = false

            pickMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        }
    }

    //UI Screen
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    println("Add button pressed")
                    launchPhotoPicker = true
                },
                containerColor = lightOrange,
                modifier = Modifier
                    .size(60.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.add_image),
                    contentDescription = "Add Image",
                    tint = darkOrange,
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gallery",
                        fontSize = 40.sp,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("settings")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkOrange)
            )
        }
        
    ) {paddingValues->
        val listState = rememberLazyGridState()
        val pagerState = rememberPagerState(
            pageCount = { 2 },
            initialPage = 0
        )

        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        ){
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth(),
                indicator = {tabPositions->
                    SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(
                                tabPositions[pagerState.currentPage]
                            ),
                        color = darkOrange
                    )
                }

            ){
                Tab(
                    text = {
                        Text(
                            text = "Images",
                            fontFamily = poppinsFontFamily,
                            fontSize = 20.sp
                        )
                    },
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    unselectedContentColor = Color.Gray,
                    selectedContentColor = darkOrange


                )
                Tab(
                    text = {
                        Text(
                            text = "Videos",
                            fontSize = 20.sp,
                            fontFamily = poppinsFontFamily
                        )
                    },
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    unselectedContentColor = Color.Gray,
                    selectedContentColor = darkOrange

                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) {page->
                when(page){
                    0 -> {

                        if(images.isNotEmpty()){
                            //val imageThumbnails = remember { mutableMapOf<Uri, Bitmap>() }
                            LazyVerticalGrid(
                                state = listState,
                                columns = GridCells.Fixed(4),
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .fillMaxSize()
                            ) {
                                items(images.size){index->
                                    ImageThumbnail(images[index], navController)
                                }
                            }
                        }
                        else{
                            Box(modifier = Modifier
                                .fillMaxWidth()
                            ){
                                Text(
                                    text = "No images found",
                                    fontSize = 20.sp,
                                    color = Color.LightGray,
                                    fontFamily = poppinsFontFamily,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                    )
                            }
                        }
                    }
                    1 -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .fillMaxSize()
                            ) {
                                items(videos.size){index->

                                    VideoThumbnail(videos[index],
                                        navController = navController,
                                        context = context
                                    )

                                }
                            }

                    }
                }
            }
        }

    }

    if(launchPhotoPicker){
        LaunchPhotoPicker()
        //launchPhotoPicker = false
    }
}