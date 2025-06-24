package com.bumperpick.bumperickUser.Screens.EditProfile

import android.content.Context
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.bumperpick.bumperickUser.API.New_model.profile_model
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.ButtonView
import com.bumperpick.bumperickUser.Screens.Component.TextFieldView
import com.bumperpick.bumperickUser.Screens.Home.AccountViewmodel
import com.bumperpick.bumperickUser.Screens.Home.UiState
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.grey
import com.bumperpick.bumperickUser.ui.theme.satoshi_regular
import org.koin.androidx.compose.koinViewModel
import java.io.File

fun getFileFromUri(context: Context, uri: android.net.Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "picked_image_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        file
    } catch (e: Exception) {
        null
    }
}

@Composable
fun EditProfile(
    onBackClick: () -> Unit,
    AccountViewmodel: AccountViewmodel = koinViewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var imageurl by remember { mutableStateOf("") }

    // Validation states
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var showImagePickerError by remember { mutableStateOf(false) }

    val profileState by AccountViewmodel.profileState.collectAsState()
    val updateProfileState by AccountViewmodel.updateProfileState.collectAsState()

    // Handle update profile result
    LaunchedEffect(updateProfileState) {
        when (updateProfileState) {
            is UiState.Success -> {
                Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                onBackClick()
            }
            is UiState.Error -> {
                Toast.makeText(context, (updateProfileState as UiState.Error).message ?: "Update failed", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = getFileFromUri(context, it)
            if (file != null) {
                imageFile = file
                showImagePickerError = false
            } else {
                showImagePickerError = true
                Toast.makeText(context, "Failed to process selected image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        AccountViewmodel.getProfile()
    }

    // Handle profile data when successfully loaded
    LaunchedEffect(profileState) {
        when (profileState) {
            is UiState.Success -> {
                val data = (profileState as UiState.Success<profile_model>).data.data
                name = data.name ?: ""
                email = data.email ?: ""
                mobile = data.phone_number ?: ""
                imageurl = data.image_url ?: ""
            }
            is UiState.Error -> {
                Toast.makeText(context, "Failed to load profile", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    // Validation functions
    fun validateName(value: String): String? {
        return when {
            value.isBlank() -> "Full name is required"
            value.length < 2 -> "Name must be at least 2 characters"
           // !value.matches(Regex("^[a-zA-Z\\s]+$")) -> "Name can only contain letters and spaces"
            else -> null
        }
    }

    fun validateEmail(value: String): String? {
        return when {
            value.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Please enter a valid email address"
            else -> null
        }
    }

    fun validateForm(): Boolean {
        nameError = validateName(name)
        emailError = validateEmail(email)
        return nameError == null && emailError == null
    }

    Scaffold(
        containerColor = (grey)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Enhanced Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(
                                ) { onBackClick() }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Edit Profile",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = satoshi_regular,
                            color = Color.Black
                        )
                    }
                }

                // Loading indicator for profile fetch
                if (profileState is UiState.Loading && name.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = BtnColor,
                            strokeWidth = 3.dp
                        )
                    }
                } else {
                    // Scrollable Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .background(grey)
                            .padding(24.dp)
                    ) {
                        // Enhanced Profile Image Section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(
                                        width = 3.dp,
                                        color = if (showImagePickerError) Color.Red.copy(alpha = 0.5f) else Color.Transparent,
                                        shape = CircleShape
                                    )
                            ) {
                                AsyncImage(
                                    model = imageFile?.toUri() ?: imageurl.ifEmpty { R.drawable.image_1 },
                                    contentDescription = "Profile picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                    error = painterResource(R.drawable.image_1),
                                    placeholder = painterResource(R.drawable.image_1)
                                )

                                // Camera overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Color.Black.copy(alpha = 0.3f),
                                            CircleShape
                                        )
                                        .clickable {
                                            launcher.launch("image/*")
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.pencil_svgrepo_com),
                                        contentDescription = "Change photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Tap to change photo",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = satoshi_regular
                            )

                            if (showImagePickerError) {
                                Text(
                                    text = "Failed to load image",
                                    fontSize = 12.sp,
                                    color = Color.Red,
                                    fontFamily = satoshi_regular,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Enhanced Form Fields

                            Column(
                                modifier = Modifier.padding(horizontal = 0.dp)
                            ) {
                                // Name Field
                                Text(
                                    text = buildAnnotatedString {
                                        append("Full Name")
                                        withStyle(style = SpanStyle(color = Color.Red)) {
                                            append(" *")
                                        }
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = satoshi_regular,
                                    color =Color.Black
                                )
                                Spacer(Modifier.height(8.dp))

                                EnhancedTextField(
                                    value = name,
                                    onValueChange = {
                                        name = it
                                        nameError = null
                                    },
                                    placeholder = "Enter your full name",
                                    isError = nameError != null,
                                    leadingIcon = Icons.Outlined.Person
                                )

                                nameError?.let { error ->
                                    Text(
                                        text = error,
                                        fontSize = 12.sp,
                                        color = Color.Red,
                                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                                    )
                                }

                                Spacer(Modifier.height(20.dp))

                                // Mobile Field (Read-only)
                                Text(
                                    text = "Mobile Number",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = satoshi_regular,
                                    color =Color.Black
                                )
                                Spacer(Modifier.height(8.dp))

                                EnhancedTextField(
                                    value = mobile,
                                    onValueChange = {},
                                    placeholder = "Mobile number not provided",
                                    isEnabled = false,
                                    leadingIcon = Icons.Outlined.Phone
                                )

                                Spacer(Modifier.height(20.dp))

                                // Email Field
                                Text(
                                    text = buildAnnotatedString {
                                        append("Email Address")
                                        withStyle(style = SpanStyle(color = Color.Red)) {
                                            append(" *")
                                        }
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = satoshi_regular,
                                    color =Color.Black
                                )
                                Spacer(Modifier.height(8.dp))

                                EnhancedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        emailError = null
                                    },
                                    placeholder = "Enter your email address",
                                    isError = emailError != null,
                                    leadingIcon = Icons.Outlined.Email,
                                    keyboardType = KeyboardType.Email
                                )

                                emailError?.let { error ->
                                    Text(
                                        text = error,
                                        fontSize = 12.sp,
                                        color = Color.Red,
                                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                                    )
                                }
                            }


                        Spacer(modifier = Modifier.height(20.dp))

                        // Required fields note
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "*",
                                color = Color.Red,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Required fields",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontFamily = satoshi_regular
                            )
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            // Enhanced Update Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column {
                    // Show error if update failed
                    if (updateProfileState is UiState.Error) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            colors = CardDefaults.cardColors(Color.Red.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = (updateProfileState as UiState.Error).message ?: "Update failed. Please try again.",
                                    fontSize = 14.sp,
                                    color = Color.Red,
                                    fontFamily = satoshi_regular
                                )
                            }
                        }
                    }

                    EnhancedButton(
                        text = when {
                            updateProfileState is UiState.Loading -> "Updating..."
                            else -> "Update Profile"
                        },
                        isLoading = updateProfileState is UiState.Loading,
                        enabled = updateProfileState !is UiState.Loading && name.isNotEmpty() && email.isNotEmpty(),
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                            if (validateForm()) {
                                if (imageFile != null) {
                                    AccountViewmodel.updateProfile(imageFile!!, name, email, mobile)
                                } else {
                                    // Handle case where no new image is selected
                                    AccountViewmodel.updateProfile(null, name, email, mobile)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = 16.sp
            )
        },
        modifier = modifier.fillMaxWidth(),
        enabled = isEnabled,
        isError = isError,
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isEnabled) {
                        if (isError) Color.Red else Color.Gray
                    } else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) Color.Red else BtnColor,
            unfocusedBorderColor = if (isError) Color.Red else Color.Gray.copy(alpha = 0.3f),
            disabledBorderColor = Color.Gray.copy(alpha = 0.2f),
            disabledTextColor = Color.Gray,
            focusedTextColor = Color.Black,
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            disabledContainerColor = Color(0xFFF5F5F5),
            cursorColor =  BtnColor,
            unfocusedTextColor = Color.Black


        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontFamily = satoshi_regular
        )
    )
}

@Composable
fun EnhancedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = BtnColor,
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontFamily = satoshi_regular
        )
    }
}
