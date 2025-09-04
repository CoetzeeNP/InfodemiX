package za.ac.ufs.infodemix

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

import za.ac.ufs.infodemix.ui.CustomTopAppBar
import za.ac.ufs.infodemix.ui.theme.InfodemiXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase in the default process
        FirebaseApp.initializeApp(this)
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
        var emailError by remember { mutableStateOf(false) }
        var email by remember { mutableStateOf(TextFieldValue("")) }
        var location by remember { mutableStateOf(TextFieldValue("")) }
        var rumour by remember { mutableStateOf(TextFieldValue("")) }

        var showDialog by remember { mutableStateOf(false) }
        var isErrorDialog by remember { mutableStateOf(false) }
        var capturedEmail by remember { mutableStateOf("") }
        var capturedLocation by remember { mutableStateOf("") }
        var capturedRumour by remember { mutableStateOf("") }

        val context = LocalContext.current

        val scope = rememberCoroutineScope()

// Load saved email
        val savedEmail by UserPreferences.getEmail(context).collectAsState(initial = null)

// Pre-fill the email once loaded
        LaunchedEffect(savedEmail) {
            savedEmail?.let {
                email = TextFieldValue(it)
            }
        }

        // Reference to Firebase Realtime Database
        val database = FirebaseDatabase.getInstance().reference.child("rumours")

        Scaffold(
            topBar = { CustomTopAppBar("InfodemiX") }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )

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
                    painter = painterResource(id = R.drawable.ufs_icdf),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        "By entering your email, you consent to anonymously contribute it for research purposes only; it will not be used to contact you or shared.",
                        Modifier.padding(15.dp),
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            // Validate email live as user types
                            emailError = it.text.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(it.text).matches()
                        },
                        label = { Text("Please enter your email address here.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .focusRequester(emailFocusRequester),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        isError = emailError
                    )

// Show inline error message if invalid
                    if (emailError) {
                        Text(
                            text = "Please enter a valid email address",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Where you witnessed the rumour.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .heightIn(min = 175.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        if (email.text.isBlank() || location.text.isBlank() || rumour.text.isBlank()) {
                            // Show error dialog
                            isErrorDialog = true
                            showDialog = true
                        } else {
                            val rumourData = hashMapOf(
                                "email" to email.text,
                                "location" to location.text,
                                "rumour" to rumour.text,
                                "timestamp" to System.currentTimeMillis()
                            )

                            val newEntry = database.push()
                            newEntry.setValue(rumourData)
                                .addOnSuccessListener {
                                    capturedEmail = email.text
                                    capturedLocation = location.text
                                    capturedRumour = rumour.text

                                    // ✅ Save email so it persists
                                    scope.launch {
                                        UserPreferences.saveEmail(context, capturedEmail)
                                    }

                                    // Clear only location and rumour (not email)
                                    location = TextFieldValue("")
                                    rumour = TextFieldValue("")

                                    isErrorDialog = false
                                    showDialog = true
                                }

                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5472D3),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Submit",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }

            // Confirmation Dialog
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        if (isErrorDialog) {
                            Text("Missing Information")
                        } else {
                            Text("Submission Successful")
                        }
                    },
                    text = {
                        if (isErrorDialog) {
                            Text("Please fill in all fields before submitting.")
                        } else {
                            Text("Your rumour was captured!\n\n Email: $capturedEmail\n\n Location: $capturedLocation\n\n Rumour: $capturedRumour")
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