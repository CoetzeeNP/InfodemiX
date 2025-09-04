package za.ac.ufs.infodemix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import za.ac.ufs.infodemix.ui.CustomTopAppBar
import za.ac.ufs.infodemix.ui.theme.InfodemiXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InfodemiXTheme {
                RumourApp()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RumourApp() {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val emailFocusRequester = remember { FocusRequester() }
        var email by remember { mutableStateOf(TextFieldValue("")) }
        var location by remember { mutableStateOf(TextFieldValue("")) }
        var rumour by remember { mutableStateOf(TextFieldValue("")) }
        var showDialog by remember { mutableStateOf(false) }
        var capturedLocation by remember { mutableStateOf("") }
        var capturedRumour by remember { mutableStateOf("") }
        var requestFocus by remember { mutableStateOf(false) } // ✅ New state

        //val db = FirebaseFirestore.getInstance()

        // For auto-focus
        //val locationFocusRequester = remember { FocusRequester() }
        //val focusManager = LocalFocusManager.current

        // ✅ This runs in Composable scope
        LaunchedEffect(requestFocus) {
            if (requestFocus) {
                //locationFocusRequester.requestFocus()
                requestFocus = false
            }
        }

        LaunchedEffect(Unit) {
            emailFocusRequester.requestFocus()
        }

        Scaffold(
            topBar = { CustomTopAppBar("Infodemix") }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // ✅ Background image
                Image(
                    painter = painterResource(id = R.drawable.bg), // replace with your drawable
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(
                        Color(0x95000000),
                        blendMode = BlendMode.Darken
                    )
                )
            }

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start

            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Image(
                    painter = painterResource(id = R.drawable.ufs_icdf), // replace with your drawable
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

//                Card {
//                    Text(
//                        "Please supply your recent encountered rumour below. Provide a location or source for the rumour and what was the misinfomration in question.",
//                        Modifier.padding(15.dp), fontSize = 18.sp
//                    )
//                }

                Spacer(modifier = Modifier.height(10.dp))

                Card {
                    Text(
                        "By entering your email, you consent to anonymously contribute it for research purposes only; it will not be used to contact you or shared.",
                        Modifier.padding(15.dp), fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Please enter you e-mail address here.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .heightIn(min = 100.dp)
                            .focusRequester(emailFocusRequester),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Where you witnessed the rumour.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .heightIn(min = 100.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rumour,
                        onValueChange = { rumour = it },
                        label = { Text("What was the rumour in question?") },
                        modifier = Modifier.fillMaxWidth().padding(10.dp)
                            .heightIn(min = 100.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(

                    onClick = {
                        emailFocusRequester.requestFocus()
                        val rumourData = hashMapOf(
                            "location" to location.text,
                            "rumour" to rumour.text,
                            "timestamp" to System.currentTimeMillis(),
                        )


//                        db.collection("rumours")
//                            .add(rumourData)
//                            .addOnSuccessListener {
//                                capturedLocation = location.text
//                                capturedRumour = rumour.text
//                                showDialog = true
//
//                                // ✅ Clear fields
//                                location = TextFieldValue("")
//                                rumour = TextFieldValue("")
//
//                                // ✅ Trigger focus request via state
//                                //focusManager.clearFocus()
//                                requestFocus = true
//                            }
//                            .addOnFailureListener { e ->
//                                capturedLocation = ""
//                                capturedRumour = "Error: ${e.message}"
//                                showDialog = true
//                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        "Submit",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Confirmation Popup
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Submission Successful") },
                    text = {
                        if (capturedLocation.isNotEmpty()) {
                            Text("Your rumour was captured!\n\n📍 Location: $capturedLocation\n💬 Rumour: $capturedRumour")
                        } else {
                            Text(capturedRumour) // Shows error message
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }


        }
    }
}
