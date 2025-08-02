package com.bumperpick.bumperickUser.Screens.Campaign

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.grey
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun EventForm(
    eventName: String,
    eventId: String,
    initialMobile: String? = null,
    onBackClick: () -> Unit,
    onRegistrationSuccess: () -> Unit = {},
    viewmodel: EventScreenViewmodel = koinViewModel()
) {
    // State variables
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf(initialMobile ?: "") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var mobileError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    val statusBarColor = Color(0xFF5A0E26) // Your desired color
    val systemUiController = rememberSystemUiController()    // Change status bar color
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false // true for dark icons on light background
        )
    }
    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val registerevent by viewmodel.user_reg_eventstate.collectAsState()

    LaunchedEffect(registerevent) {
        when (registerevent) {
            is UiState.Success -> {
                isSubmitting = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Registration successful!")
                    onRegistrationSuccess()
                }
                onRegistrationSuccess()
            }
            is UiState.Error -> {
                isSubmitting = false

                coroutineScope.launch {
                    snackbarHostState.showSnackbar((registerevent as UiState.Error).message)
                }
            }
            is UiState.Loading -> {
                isSubmitting = true
            }
            else -> {
                isSubmitting = false
            }
        }
    }

    // Clear errors when user starts typing
    LaunchedEffect(name) {
        if (nameError != null) nameError = null
    }

    LaunchedEffect(email) {
        if (emailError != null) emailError = null
    }

    LaunchedEffect(mobile) {
        if (mobileError != null) mobileError = null
    }

    // Form validation
    fun validateForm(): Boolean {
        var isValid = true

        // Clear previous errors
        nameError = null
        emailError = null
        mobileError = null

        // Name validation
        when {
            name.isBlank() -> {
                nameError = "Name is required"
                isValid = false
            }
            name.length < 2 -> {
                nameError = "Name must be at least 2 characters"
                isValid = false
            }
            name.length > 50 -> {
                nameError = "Name must be less than 50 characters"
                isValid = false
            }
            !name.matches(Regex("^[a-zA-Z\\s]+$")) -> {
                nameError = "Name should only contain letters and spaces"
                isValid = false
            }
        }

        // Email validation
        when {
            email.isBlank() -> {
                emailError = "Email is required"
                isValid = false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailError = "Please enter a valid email address"
                isValid = false
            }
            email.length > 100 -> {
                emailError = "Email must be less than 100 characters"
                isValid = false
            }
        }

        // Mobile validation (optional but if provided must be valid)
        if (mobile.isNotBlank()) {
            when {
                mobile.length < 10 -> {
                    mobileError = "Mobile number must be at least 10 digits"
                    isValid = false
                }
                mobile.length > 15 -> {
                    mobileError = "Mobile number must be less than 15 digits"
                    isValid = false
                }
                !mobile.matches(Regex("^[+]?[0-9\\s-()]+$")) -> {
                    mobileError = "Please enter a valid mobile number"
                    isValid = false
                }
            }
        }

        return isValid
    }

    // Handle form submission
    fun handleSubmit() {
        if (validateForm() && !isSubmitting) {
            viewmodel.registerEvent(eventId, name, mobile, email)
        }
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()
            .padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                // Enhanced Header with better styling
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 20.dp,
                                bottom = 18.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onBackClick() }
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = eventName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Campaign Registration",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .background(grey)
                        .padding(24.dp)
                ) {
                    // Form Instructions
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Please fill in all required fields to complete your registration",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Form Fields
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Name Field
                        FormField(
                            label = "Full Name",
                            isRequired = true,
                            value = name,
                            onValueChange = { name = it.take(50) }, // Limit input length
                            placeholder = "Enter your full name",
                            error = nameError,
                            leadingIcon = Icons.Outlined.Person,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )

                        // Email Field
                        FormField(
                            label = "Email Address",
                            isRequired = true,
                            value = email,
                            onValueChange = { email = it.take(100) }, // Limit input length
                            placeholder = "Enter your email address",
                            error = emailError,
                            leadingIcon = Icons.Outlined.Email,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )

                        // Mobile Field
                        FormField(
                            label = "Mobile Number",
                            isRequired = false,
                            value = mobile,
                            onValueChange = { mobile = it.take(10) }, // Limit input length
                            placeholder = "Enter your mobile number (optional)",
                            error = mobileError,
                            leadingIcon = Icons.Outlined.Phone,
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done,
                            isEnabled = initialMobile == null // Only allow editing if not pre-filled
                        )
                    }

                    // Additional spacing before submit button area
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // Enhanced Submit Button with loading state
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Button(
                        onClick = { handleSubmit() },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BtnColor,
                            disabledContainerColor = BtnColor.copy(alpha = 0.6f)
                        )
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submitting...")
                        } else {
                            Text(
                                text = "Register for Campaign",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    isRequired: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isEnabled: Boolean = true
) {
    Column {
        Text(
            text = buildAnnotatedString {
                append(label)
                if (isRequired) {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                        append(" *")
                    }
                }
            },
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (error != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            },
            isError = error != null,
            enabled = isEnabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BtnColor,
                cursorColor = BtnColor,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = grey.copy(alpha = 0.1f),
                errorContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        error?.let { errorText ->
            Row(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}