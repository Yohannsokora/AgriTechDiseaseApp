package com.example.agritechdisease

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.example.agritechdisease.ui.theme.AgriTechDiseaseTheme
import kotlinx.coroutines.delay

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
        // Lets add a semi-transparent Overlay
        Surface(
            color = Color.Black.copy(alpha = 0.4f), //Gives an opacity of 40%
            modifier = Modifier.fillMaxSize()
        ){}

        // Lets ensure that the content Overlays
        Column(
            modifier = Modifier.fillMaxSize().padding(30.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 30.sp,
                // Ensure readability
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 350.dp)

            )


            Spacer(modifier = Modifier.height(300.dp))

            Button(
                onClick = onUploadClick,
                shape = RoundedCornerShape(15.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),


                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(text = "Upload or Take a Picture",
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif)
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