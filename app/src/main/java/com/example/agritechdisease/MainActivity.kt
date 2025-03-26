package com.example.agritechdisease

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.io.File
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.getValue

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.util.Base64
import org.json.JSONObject
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgriTechDiseaseTheme {
                MainScreen(
                    onScanClick = { navigateToCameraScreen() },
                    onCommonDiseasesClick = { navigateToDiseaseListScreen() }
                )
            }
        }
    }

    private fun navigateToCameraScreen() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDiseaseListScreen() {
        val intent = Intent(this, DiseaseListActivity::class.java)
        startActivity(intent)
    }
}




// Data class for diseases
data class RiceDisease(val name: String, val description: String)
@Composable
fun DiseaseItem(disease: RiceDisease) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = disease.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = disease.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}




class DiseaseListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgriTechDiseaseTheme {
                DiseaseListScreen(onBackClick = { finish() })
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseListScreen(onBackClick: () -> Unit) {
    val diseaseList = listOf(
        RiceDisease("Rice Blast", "Fungal disease causing leaf spots."),
        RiceDisease("Bacterial Leaf Blight", "Bacterial infection causing leaf wilting."),
        RiceDisease("Sheath Blight", "Fungal disease affecting rice stems."),
        RiceDisease("Rice Tungro Disease", "Viral disease spread by leafhoppers."),
        RiceDisease("Brown Spot", "Fungal disease leading to grain discoloration."),
        RiceDisease("Leaf Scald", "Fungal disease causing elongated lesions on leaves."),
        RiceDisease("Stem Rot", "Fungal infection leading to blackened rice stems."),
        RiceDisease("False Smut", "Fungal disease causing greenish spore balls on grains."),
        RiceDisease("Grain Discoloration", "Multiple pathogens causing discolored rice grains."),
        RiceDisease("Bakanae Disease", "Fungal disease leading to elongated and weak plants."),
        RiceDisease("Ufra Disease", "Nematode infection causing twisted leaves and unfilled grains."),
        RiceDisease("Bacterial Sheath Rot", "Bacterial infection leading to necrotic lesions on leaves."),
        RiceDisease("Sheath Brown Rot", "Bacterial disease causing sheath browning and rotting."),
        RiceDisease("Rice Yellow Mottle Virus", "Viral disease causing yellowing and stunted growth."),
        RiceDisease("Tungro Virus", "Viral disease leading to yellow-orange leaves and plant stunting."),
        RiceDisease("Rice Grassy Stunt", "Viral disease spread by brown planthopper."),
        RiceDisease("White Tip Nematode", "Nematode infestation leading to white tips on leaves."),
        RiceDisease("Bacterial Panicle Blight", "Bacterial infection causing grain sterility."),
        RiceDisease("Rice Hoja Blanca", "Viral disease spread by planthoppers."),
        RiceDisease("Rice Stripe Virus", "Viral disease causing yellow streaks on leaves."),
        RiceDisease("Rice Ragged Stunt", "Viral disease leading to deformed leaves and stunting."),
        RiceDisease("Black Streaked Dwarf Virus", "Viral disease causing dwarfing and black streaks."),
        RiceDisease("Rice Orange Leaf Disease", "Viral infection leading to orange-yellow leaves."),
        RiceDisease("Rice Tungro Bacilliform Virus", "Viral co-infection causing severe stunting."),
        RiceDisease("Rice Root Knot Nematode", "Nematode infestation causing root galls."),
        RiceDisease("Rice Wilted Stunt", "Bacterial infection causing leaf wilting and plant death."),
        RiceDisease("Grassy Stunt", "Viral disease causing excessive tillering."),
        RiceDisease("White Leaf Streak", "Fungal disease causing white streaks on leaves."),
        RiceDisease("Bacterial Brown Stripe", "Bacterial infection leading to brown streaks on leaves."),
        RiceDisease("Rice Dwarf Virus", "Viral disease leading to shortened plants and reduced yield.")
    )


    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val filteredList = diseaseList.filter {
        it.name.contains(searchQuery.text, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Common Rice Diseases", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32), titleContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search disease...") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(filteredList) { disease ->
                    DiseaseItem(disease)
                }
            }
        }
    }
}



class CameraActivity : ComponentActivity() {
    private var photoUri: Uri? = null
    private val imageUris = mutableStateListOf<Uri>()

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let {
                    imageUris.add(it) // Add the captured image to the list
                }
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.size <= 5) {
                imageUris.clear()
                imageUris.addAll(uris)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AgriTechDiseaseTheme {
                CameraScreen(
                    imageUris = imageUris,
                    onTakePicture = {
                        val newPhotoUri = createImageFileUri(this) // Create URI before the launching
                        photoUri = newPhotoUri // Assign to photoUri
                        takePictureLauncher.launch(newPhotoUri) // Launch with a valid URI
                    },
                    onPickImages = { pickImageLauncher.launch("image/*") },
                    onBackClick = {finish()}
                )
            }
        }
    }
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(imageUris: List<Uri>, onTakePicture: () -> Unit, onPickImages: () -> Unit, onBackClick: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crop Scanner", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Capture or Upload an Image",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (imageUris.isEmpty()) {
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Images Selected", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(imageUris) { uri ->
                        Card(
                            modifier = Modifier
                                .size(150.dp)
                                .padding(4.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                        ) {
                            Box {
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = "Selected Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onTakePicture, shape = RoundedCornerShape(50.dp)) {
                    Text("📷 Take Picture")

                }

                Button(onClick = onPickImages, shape = RoundedCornerShape(50.dp)) {
                    Text("📤 Upload")

                }
            }
            DiagnosisSection(imageUris.firstOrNull())
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

// Helper function to create a file URI for the image
private fun createImageFileUri(context: Context): Uri {
    val file = File(context.filesDir, "captured_image_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}


const val API_KEY = "A4cAivvfg7Ub8HwJ6eJnyODaPfKfLWaCUgcfEahfIIVvoDBdM0"


// Data class to hold parsed diagnosis result
data class DiagnosisResult(
    val name: String,
    val scientificName: String,
    val probability: Double,
    val wikiUrl: String,
    val treatmentBiological: String?,
    val treatmentChemical: String?,
    val treatmentPrevention: String?
)


fun parseDiagnosisResult(json: String): DiagnosisResult? {
    return try {
        val root = JSONObject(json)
        val suggestion = root
            .getJSONObject("result")
            .getJSONObject("disease")
            .getJSONArray("suggestions")
            .getJSONObject(0)

        val name = suggestion.getString("name")
        val probability = suggestion.getDouble("probability")
        val scientificName = suggestion.getString("scientific_name")
        val wikiUrl = suggestion.optString("wiki_url", "")

        val treatment = suggestion.optJSONObject("details")?.optJSONObject("treatment")

        DiagnosisResult(
            name = name,
            scientificName = scientificName,
            probability = probability,
            wikiUrl = wikiUrl,
            treatmentBiological = treatment?.optString("biological"),
            treatmentChemical = treatment?.optString("chemical"),
            treatmentPrevention = treatment?.optString("prevention")
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun uriToBase64(context: Context, uri: Uri): String? {
    return context.contentResolver.openInputStream(uri)?.use { input ->
        val bytes = input.readBytes()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}


suspend fun identifyWithKindwise(base64Image: String): String? {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val json = """
        {
            "images": ["data:image/jpeg;base64,$base64Image"],
            "latitude": 0.0,
            "longitude": 0.0,
            "similar_images": true
        }
    """.trimIndent()

    val request = Request.Builder()
        .url("https://crop.kindwise.com/api/v1/identification")
        .addHeader("Content-Type", "application/json")
        .addHeader("Api-Key", API_KEY)
        .post(json.toRequestBody("application/json".toMediaType()))
        .build()

    val response = client.newCall(request).execute()
    return response.body?.string()
}


@Composable
fun ResultCard(result: DiagnosisResult) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🦠 Disease: ${result.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("🔬 Scientific Name: ${result.scientificName}", fontSize = 16.sp)
            Text("📈 Confidence: ${(result.probability * 100).toInt()}%", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            result.treatmentBiological?.let {
                Text("🌱 Biological Treatment: ${'$'}it")
            }
            result.treatmentChemical?.let {
                Text("🧪 Chemical Treatment: ${'$'}it")
            }
            result.treatmentPrevention?.let {
                Text("🛡 Prevention: ${'$'}it")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (result.wikiUrl.isNotBlank()) {
                val context = LocalContext.current
                TextButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, result.wikiUrl.toUri())
                    context.startActivity(intent)
                }) {
                    Text("🔗 Learn More", color = Color(0xFF2E7D32))
                }
            }
        }
    }
}




@Composable
fun DiagnosisSection(imageUri: Uri?) {
    val context = LocalContext.current
    val resultState = remember { mutableStateOf<DiagnosisResult?>(null) }
    val isLoading = remember { mutableStateOf(false) }

    if (imageUri != null) {
        Button(
            onClick = {
                val base64 = uriToBase64(context, imageUri)
                if (base64 != null) {
                    isLoading.value = true
                    CoroutineScope(Dispatchers.IO).launch {
                        val json = identifyWithKindwise(base64)
                        val result = json?.let { parseDiagnosisResult(it) }

                        resultState.value = result
                        isLoading.value = false
                    }
                }
            },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text("🧠 Analyze Disease")
        }

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        resultState.value?.let { result ->
            ResultCard(result)
        }
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
