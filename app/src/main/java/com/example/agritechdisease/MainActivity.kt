package com.example.agritechdisease

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.agritechdisease.ui.theme.AgriTechDiseaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgriTechDiseaseTheme {
                MainScreen{ navigateToCameraScreen()}
            }
        }
    }
    private fun navigateToCameraScreen(){
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun MainScreen(onUploadClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        // Lets a Insert Background Image
        Image(
            painter = painterResource(id = R.drawable.image4),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            // Lets Ensure the image fills the screen
            contentScale = ContentScale.Crop
        )
        // Lets ensure that the content Overlays
        Column(
            modifier = Modifier.fillMaxSize().padding(30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 30.sp,
                // Ensure readability
                color = androidx.compose.ui.graphics.Color.White
            )

            Spacer(modifier = Modifier.height(550.dp))

            Button(
                onClick = onUploadClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Upload or Take a Picture")
            }
        }
    }

}




@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AgriTechDiseaseTheme {
        MainScreen { }
    }
}