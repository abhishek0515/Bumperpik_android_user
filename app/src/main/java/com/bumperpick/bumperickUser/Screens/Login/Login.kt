package com.bumperpick.bumperickUser.Screens.Login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.ButtonView
import com.bumperpick.bumperickUser.Screens.Component.Google_SigInButton
import com.bumperpick.bumperickUser.Screens.Component.TextFieldView
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.satoshi
import com.bumperpick.bumperickUser.ui.theme.satoshi_bold
import com.bumperpick.bumperickUser.ui.theme.satoshi_medium
import com.bumperpick.bumperickUser.ui.theme.satoshi_regular
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun Login(
    viewModel: LoginViewmodel = koinViewModel(),
    googleSignInViewModel: GoogleSignInViewModel = koinViewModel(),
    onLoginSuccess: (mobile:String,isMobile:Boolean) -> Unit
) {
    // Collect UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()
    // Material 3 Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val google_uiState by googleSignInViewModel.uiState.collectAsState()

    // Launcher for Google Sign-In intent
    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        googleSignInViewModel.processSignInResult(result.data)
    }

    // Monitor OTP sent state for navigation
    LaunchedEffect(uiState.isOtpSent) {
        if (uiState.isOtpSent) {
            onLoginSuccess(viewModel.uiState.value.phoneNumber,true)
            viewModel.updateState()
        }

    }

    // Show error Snackbar when needed
    LaunchedEffect(uiState.error) {
        if (uiState.error.isNotEmpty()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = uiState.error,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
                // Clear error after showing snackbar
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect (google_uiState.userData){
        if (google_uiState.userData != null) {




            onLoginSuccess(google_uiState.userData!!.email.toString(),false)

        }
    }
    // Show error Snackbar for Google Sign-In
    LaunchedEffect(google_uiState.error) {
        if (google_uiState.error.isNotEmpty()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = google_uiState.error,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
                googleSignInViewModel.clearError()
            }
        }
    }

    Scaffold( snackbarHost = {
        SnackbarHost(hostState = snackbarHostState,  modifier = Modifier.padding( 16.dp),  snackbar = { data ->
            Snackbar(
                action = {
                    TextButton(onClick = { data.performAction() }) {
                        Text(
                            text = "OK",
                            color = Color.White
                        )
                    }
                },
                containerColor = Color.Red.copy(alpha = 0.8f),
                contentColor = Color.White
            ) {
                Text(text = data.visuals.message)
            }
        })
    },) {padding->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(150.dp))

            // BumperPick Logo - Using your provided image
            Image(
                painter = painterResource(id = R.drawable.image_1),
                contentDescription = "BumperPick Logo",
                modifier = Modifier
                    .height(72.dp)
                    .width(200.dp)
                    .padding(start = 20.dp, end = 20.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Login text
            Text(
                text = "Login with your mobile number",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 24.sp // ← Increased line spacing
                ),
                fontFamily = satoshi_regular,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 20.dp, end = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Field - connected to state
            PhoneNumberField(
                value = uiState.phoneNumber,
                onValueChange = {
                    viewModel.updatePhoneNumber(it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Terms and Conditions Checkbox - connected to state
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { viewModel.toggleTermsAccepted() }
                    .padding(horizontal = 20.dp)
            ) {
                Checkbox(
                    checked = uiState.termsAccepted,
                    onCheckedChange = { viewModel.toggleTermsAccepted() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = BtnColor,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White
                    )
                )

                Text(
                    text = "I accept the Terms and conditions & Privacy policy",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Spacer(modifier=Modifier.height(10.dp))
            // Loading indicator or Button - connected to state
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = BtnColor,
                    strokeWidth = 2.dp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                ButtonView(
                    "Get OTP",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontal_padding = 20.dp
                ) {
                    viewModel.sendOtp()
                }
            }



            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign-In button
            Google_SigInButton(modifier = Modifier.padding(horizontal = 20.dp)) {
                val signInIntent = googleSignInViewModel.getSignInIntent()
                signInLauncher.launch(signInIntent)
            }

            Spacer(modifier = Modifier.weight(1f))


        }
    }


}





@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = Color(0xFFE5E5E5),
                    shape = RoundedCornerShape(12.dp)
                )

        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flag and country code
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "+91",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212427)
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Divider line
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(Color(0xFFE5E5E5))
                    )
                }

                // Phone number input field
                BasicTextField(
                    value = value.removePrefix("+91 "),
                    onValueChange = { onValueChange("+91 $it") },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFF212427)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)
                )
            }
        }
    }
}



