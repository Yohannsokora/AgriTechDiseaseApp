package com.example.agritechdisease

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.agritechdisease.ui.theme.AgriTechDiseaseTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgriTechDiseaseTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> navController.navigate("main") { popUpTo(0) }
            is AuthState.Idle -> navController.navigate("login") { popUpTo(0) }
            else -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.isUserAuthenticated()) "main" else "login"
    ) {
        composable("login") {
            LoginScreen(navController, authViewModel)
        }
        composable("register") {
            RegisterScreen(navController, authViewModel)
        }
        composable("main") {
            MainScreen(
                onScanClick = { navController.navigate("camera") },
                onCommonDiseasesClick = { navController.navigate("diseases") },
                userEmail = userEmail,
                onSignOut = {
                    authViewModel.signOut()
                }
            )
        }
        composable("camera") {
            CameraScreenWrapper(navController)
        }
        composable("diseases") {
            DiseaseListScreen(onBackClick = { navController.popBackStack() })
        }
    }
}


@Composable
fun CameraScreenWrapper(navController: NavHostController) {
    val context = LocalContext.current
    val imageUris = remember { mutableStateListOf<Uri>() }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let { imageUris.add(it) }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.size <= 5) {
            imageUris.clear()
            imageUris.addAll(uris)
        }
    }

    CameraScreen(
        imageUris = imageUris,
        onTakePicture = {
            val newUri = createImageFileUri(context)
            photoUri = newUri
            takePictureLauncher.launch(newUri)
        },
        onPickImages = {
            pickImageLauncher.launch("image/*")
        },
        onBackClick = {
            navController.popBackStack()
        }
    )
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
fun MainScreen(
    onScanClick: () -> Unit,
    onCommonDiseasesClick: () -> Unit,
    userEmail: String?,
    onSignOut: () -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background
            Image(
                painter = painterResource(id = R.drawable.image4),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Surface(
                color = Color.Black.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxSize()
            ) {}

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                userEmail?.let {
                    Text(
                        text = "👋 Welcome, $it",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onSignOut,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Red
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Sign Out")
                }

                Spacer(modifier = Modifier.height(40.dp))

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
                    Text("Scan Crop", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                    Text("Common diseases", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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


fun uriToBase64(context: Context, uri: Uri): String? {
    return context.contentResolver.openInputStream(uri)?.use { input ->
        val bytes = input.readBytes()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}



suspend fun identifyWithKindwise(base64Image: String): String? = withContext(Dispatchers.IO) {
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

    try {
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) response.body?.string()
            else null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}




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
    var result by remember { mutableStateOf<DiagnosisResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var shouldStartAnalysis by remember { mutableStateOf(false) }

    if (imageUri != null) {
        Button(
            onClick = { shouldStartAnalysis = true },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text("🧠 Analyze Disease")
        }

        if (shouldStartAnalysis) {
            LaunchedEffect(imageUri) {
                isLoading = true
                val base64 = uriToBase64(context, imageUri)
                val json = base64?.let { identifyWithKindwise(it) }
                result = json?.let { parseDiagnosisResult(it) }
                isLoading = false
                shouldStartAnalysis = false
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        result?.let { ResultCard(it) }
    }
}






sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun signUp(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _authState.value = if (task.isSuccessful) AuthState.Success
                else AuthState.Error(task.exception?.message ?: "Signup failed")
            }
    }

    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _authState.value = if (task.isSuccessful) AuthState.Success
                else AuthState.Error(task.exception?.message ?: "Login failed")
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun isUserAuthenticated(): Boolean = auth.currentUser != null
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.signIn(email, password) }) {
            Text("Login")
        }

        TextButton(onClick = { navController.navigate("register") }) {
            Text("Don't have an account? Register")
        }

        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Success -> navController.navigate("main") { popUpTo("login") { inclusive = true } }
            is AuthState.Error -> Text(
                text = (authState as AuthState.Error).message,
                color = Color.Red
            )
            else -> {}
        }
    }
}

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.signUp(email, password) }) {
            Text("Register")
        }

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login")
        }

        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Success -> navController.navigate("main") { popUpTo("register") { inclusive = true } }
            is AuthState.Error -> Text(
                text = (authState as AuthState.Error).message,
                color = Color.Red
            )
            else -> {}
        }
    }
}

@Composable
fun AuthNavHost(startDestination: String = "login") {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("main") {
            MainScreen(
                onScanClick = {},
                onCommonDiseasesClick = {},
                userEmail = userEmail,
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AgriTechDiseaseTheme {
        MainScreen(
            onScanClick = {},
            onCommonDiseasesClick = {},
            userEmail = "demo@example.com",
            onSignOut = {}
        )
    }
}

