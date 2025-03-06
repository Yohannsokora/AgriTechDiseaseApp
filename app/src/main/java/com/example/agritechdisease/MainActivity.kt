package com.example.agritechdisease

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.agritechdisease.ui.theme.AgriTechDiseaseTheme
import kotlinx.coroutines.delay
import java.io.File
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgriTechDiseaseTheme {
                MainScreen(
                    onScanClick = { navigateToCameraScreen() },
                    onCommonDiseasesClick = { /* TODO: Will Implement it later */ }
                )
            }
        }
    }

    private fun navigateToCameraScreen(){
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
}

class CameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUris = mutableStateOf<List<Uri>>(emptyList())
        val photoUri = mutableStateOf<Uri?>(null)

        val takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    photoUri.value?.let {
                        imageUris.value = listOf(it) // Save the captured image
                    }
                }
            }

        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                if (uris.size <= 5) {
                    imageUris.value = uris
                }
            }

        setContent {
            AgriTechDiseaseTheme {
                CameraScreen(
                    imageUris.value,
                    onTakePicture = { uri -> takePictureLauncher.launch(uri) }, // Correctly pass launcher
                    onPickImages = { pickImageLauncher.launch("image/*") }
                )
            }
        }
    }

    // Helper function to create a file URI for the image
    private fun createImageFileUri(context: Context): Uri {
        val file = File(context.filesDir, "captured_image_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }
}


    // Helper function to create a file URI for the image
    private fun createImageFileUri(context: Context): Uri {
        val file = File(context.filesDir, "captured_image_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }




@Composable
fun MainScreen(onScanClick: () -> Unit, onCommonDiseasesClick: () -> Unit) {
    Scaffold(
        bottomBar = { BottomNavigationBar() } // Ensures the Bottom Navigation Bar stays at the bottom
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Prevents content from overlapping the bottom bar
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.image4),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Semi-transparent overlay for readability
            Surface(
                color = Color.Black.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxSize()
            ) {}

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Crops Disease Identifier",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 30.sp,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(50.dp))

                Button(
                    onClick = onScanClick,
                    shape = RoundedCornerShape(50.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF2E7D32)
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text(
                        text = "Scan Crop",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = onCommonDiseasesClick,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF2E7D32)
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text(
                        text = "Common diseases",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}






@Composable
fun CameraScreen(imageUris: List<Uri>, onTakePicture: (Uri) -> Unit, onPickImages: () -> Unit) {
    val context = LocalContext.current // Get the context

    val photoUri = remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Capture or Upload 5 Images",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(250.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageUris.isEmpty()) {
                Text("No Images Selected")
            } else {
                Image(
                    painter = rememberAsyncImagePainter(imageUris.first()),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                val uri = createImageFileUri(context) // Create a file for the image
                photoUri.value = uri
                onTakePicture(uri) // Call takePictureLauncher with the correct URI
            }) {
                Text("Take Picture")
            }

            Button(onClick = onPickImages) {
                Text("Upload")
            }
        }
    }
}


@Composable
fun BottomNavigationBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,

        ) {
            BottomNavItem(icon = R.drawable.plant, label = "Find Crop")
            BottomNavItem(icon = R.drawable.pot, label = "My Crops")
            BottomNavItem(icon = R.drawable.settings, label = "Settings")
        }
    }
}


@Composable
fun BottomNavItem(icon: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AgriTechDiseaseTheme {
        MainScreen(
            onScanClick = {},
            onCommonDiseasesClick = {}
        )
    }
}
