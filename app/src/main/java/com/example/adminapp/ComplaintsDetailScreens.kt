package com.example.adminapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    id :String,
    viewModel: ComplaintDetailViewModel,
    navHostController: NavHostController
) {

    val complaint by viewModel.complaint.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val imageUri by viewModel.resolvedImageUri.collectAsState()

    val snackBarHost = remember {
        SnackbarHostState()
    }


    LaunchedEffect(id) {
        viewModel.getComplaintById(id)
    }


    LaunchedEffect(Unit) {

        viewModel.snackBarEvent.collect {

            snackBarHost.showSnackbar(it)

        }
    }

    Scaffold(

        snackbarHost = {
            SnackbarHost(snackBarHost)
        },

        topBar = {

            TopAppBar(

                title = {
                    Text("Complaint Details")
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navHostController.popBackStack()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(

                        onClick = {
                          viewModel.getComplaintById(id)
                        }

                    ) {

                        Icon(

                            imageVector = Icons.Default.Refresh,

                            contentDescription = "Refresh"

                        )

                    }
                }
            )
        }

    ) { padding ->

        when (loading) {

            is States.Error -> {
                ErrorSection(message = (loading as States.Error).message){
                     viewModel.getComplaintById(id)
                }
            }

            is States.Loading -> {
                CustomLoadingScreen(padding = padding)
            }

            is States.Success -> {

                if (complaint == null){
                   ErrorSection(message = "not found try again", onRetry = {
                       viewModel.getComplaintById(id)
                   })
                }
                else{
                    ComplaintDetailContent(item = complaint!!,  padding = padding,imageUri, onImageUri = {
                        viewModel.updateImageUri(it)
                    }, onUpdate = { viewModel.updateData()})
                }
            }


        }



    }
}












@Composable
fun CustomLoadingScreen(message: String = "Please wait…",padding: PaddingValues) {

    Box(
        modifier = Modifier
            .fillMaxSize().padding(padding)
            .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator()

            Spacer(Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}






@Composable
fun ComplaintDetailContent(

    item: Complaint,
    padding: PaddingValues,
    imageUri: Uri?,
    onImageUri: (Uri) -> Unit,
    onUpdate :() -> Unit
) {
    var selectedImage by remember {
        mutableStateOf<String?>(null)
    }

    var tempImageUri by remember {

        mutableStateOf<Uri?>(null)

    }


    val context = LocalContext.current

    val isResolved = item.status == "RESOLVED"

    //  Camera launcher

    val cameraLauncher = rememberLauncherForActivityResult(

        contract = ActivityResultContracts.TakePicture()

    ) { success ->

        if (success && tempImageUri != null) {

            onImageUri(tempImageUri!!)

        }
    }

    Column(

        modifier = Modifier
            .fillMaxSize()


            .verticalScroll(

                rememberScrollState()

            )
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
            .padding(vertical = 10.dp, horizontal = 3.dp)

    ) {

        //  Complaint Image Card



        Spacer(modifier = Modifier.height(20.dp))

        //  Resolved Image Section

        Card(

            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),

            shape = RoundedCornerShape(20.dp)

        ) {

            Log.e("resolved", "ComplaintDetailContent: ${item.resolvedImageUrl}")
            when {
                //  Backend resolved image

                !item.resolvedImageUrl.isNullOrBlank() -> {

                    Log.e("resolved", "ComplaintDetailContent    6767: ${item.resolvedImageUrl}")
                    AsyncImage(
                        model = item.resolvedImageUrl,
                        contentDescription = null,
                        placeholder = painterResource(R.drawable.ic_launcher_foreground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedImage = item.resolvedImageUrl
                            },
                        contentScale = ContentScale.Crop
                    )
                }

                imageUri != null -> {

                    Log.e("resolved", "ComplaintDetailContent    6768: ${item.resolvedImageUrl}")

                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        placeholder = painterResource(R.drawable.ic_launcher_foreground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedImage = imageUri.toString()
                            },
                        contentScale = ContentScale.Crop
                    )
                }
                //  Add Image UI

                else -> {
                    Log.e("resolved", "ComplaintDetailContent    6770: ${item.resolvedImageUrl}")

                    Column(

                        modifier = Modifier
                            .fillMaxSize()

                            .clickable {

                                val uri =
                                    createImageUri(context)
                                tempImageUri = uri

                                if (uri != null) {
                                    cameraLauncher.launch(uri)
                                }

                            },

                        horizontalAlignment =
                        Alignment.CenterHorizontally,

                        verticalArrangement =
                        Arrangement.Center

                    ) {

                        Icon(

                            imageVector = Icons.Default.AddAPhoto,

                            contentDescription = null,

                            modifier = Modifier.size(60.dp),

                            tint = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(

                            text = "Add Resolved Image",

                            color = Color(0xFF2962FF),

                            fontWeight = FontWeight.SemiBold,

                            fontSize = 18.sp
                        )
                    }
                }
            }

        }



        ///




        Card(

            shape = RoundedCornerShape(20.dp),

            elevation = CardDefaults.cardElevation(8.dp)

        ) {

            Box {

                AsyncImage(
                    model = item.imageUrlApp,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            selectedImage = item.imageUrlApp
                        },
                    contentScale = ContentScale.Crop
                )

                //  Status Badge

                Box(

                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(

                            if (isResolved)
                                Color(0xFF4CAF50)

                            else
                                Color(0xFFE53935),

                            RoundedCornerShape(20.dp)
                        )
                ) {

                    Text(

                        text =
                        if (isResolved)
                            "Resolved"

                        else
                            "Pending",

                        color = Color.White,

                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),

                        fontSize = 12.sp
                    )
                }
            }
        }






        if (selectedImage != null) {

            Dialog(
                onDismissRequest = {
                    selectedImage = null
                }
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {

                    AsyncImage(
                        model = selectedImage,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    IconButton(
                        onClick = {
                            selectedImage = null
                        },
                        modifier = Modifier.align(
                            Alignment.TopEnd
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }


if(item.mode == Mode.OUTDOOR){
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = {

            val uri = Uri.parse(
                "https://www.google.com/maps/search/?api=1&query=${item.latitude},${item.longitude}"
            )

            val intent = Intent(
                Intent.ACTION_VIEW,
                uri
            )

            intent.setPackage(
                "com.google.android.apps.maps"
            )

            context.startActivity(intent)
        }
    ){
        Text("check on map")
    }
}

        Spacer(Modifier.height(16.dp))

            //  Complaint Info
            Card(
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Complaint", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(item.complain, fontSize = 15.sp)

                    Spacer(Modifier.height(12.dp))

                    if (item.description.isNotBlank()) {
                        Text("Description", fontWeight = FontWeight.Bold)
                        Text(item.description)
                        Spacer(Modifier.height(12.dp))
                    }

                    Text("Address", fontWeight = FontWeight.Bold)
                    Text(item.address)

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "⏱ ${formatTimestamp(item.timestamp.toLong())}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(

                modifier = Modifier.height(10.dp)

            )



        if(item.resolvedImageUrl.isNullOrBlank()){
            Button(
                onClick = {
                    onUpdate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("update")
            }
        }

        }




        }



fun formatTimestamp(ts: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(ts))
}

fun createImageUri(context: Context): Uri? {
    val contentValues = ContentValues().apply {
        // put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis()) without display name  its show unsupported image error
        put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
    }

    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,// its a address of image table where all meta data store
        contentValues  )
}

