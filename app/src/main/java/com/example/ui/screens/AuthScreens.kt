package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SaathAvatar
import com.example.ui.components.SaathPrimaryButton
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("aditya.mishra@example.com") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Skip for now link on top right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Skip for now ›",
                color = SecondaryText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { onLoginSuccess() }
                    .testTag("skip_login_button")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Brand Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "SAATH",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ForestGreen,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "Always with you",
            fontSize = 13.sp,
            color = SecondaryText,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "A healthier mind. A happier you.",
            fontSize = 12.sp,
            color = ForestGreen,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Companion Greeting Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SaathiAvatar(avatarId = "aarav", size = 88.dp)
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(PaleGreen)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Hey!\nI'm Saathi. Let's build a brighter you together 💚",
                    fontSize = 13.sp,
                    color = DeepGreen,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // White Form Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "Welcome Back 👋",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                    Text(
                        text = "Glad to see you again! Let's continue your journey with SAATH.",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = SecondaryText) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("login_email_input")
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = SecondaryText) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = SecondaryText
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                )

                // Remember me & Forgot Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = ForestGreen)
                        )
                        Text(text = "Remember me", fontSize = 12.sp, color = DarkNavy)
                    }
                    Text(
                        text = "Forgot Password?",
                        fontSize = 12.sp,
                        color = ForestGreen,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { }
                    )
                }

                // Login Button
                SaathPrimaryButton(
                    text = "Login",
                    onClick = onLoginSuccess,
                    testTag = "login_submit_button"
                )

                // Divider or continue with
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                    Text(
                        text = "  or continue with  ",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                }

                // Social Logins: Google & Apple
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onLoginSuccess,
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                        modifier = Modifier.weight(1f).height(46.dp).testTag("google_login_button")
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Google", tint = ForestGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Google", fontSize = 13.sp, color = DarkNavy, fontWeight = FontWeight.Medium)
                    }

                    OutlinedButton(
                        onClick = onLoginSuccess,
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                        modifier = Modifier.weight(1f).height(46.dp).testTag("apple_login_button")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Apple", tint = DarkNavy, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Apple", fontSize = 13.sp, color = DarkNavy, fontWeight = FontWeight.Medium)
                    }
                }

                // Create Account Prompt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "New here? ", fontSize = 13.sp, color = SecondaryText)
                    Text(
                        text = "Create an account ›",
                        fontSize = 13.sp,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToSignUp() }.testTag("navigate_signup_button")
                    )
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("Aditya Mishra") }
    var email by remember { mutableStateOf("aditya.mishra@example.com") }
    var password by remember { mutableStateOf("password123") }
    var confirmPassword by remember { mutableStateOf("password123") }
    var agreeTerms by remember { mutableStateOf(true) }
    var receiveTips by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Step progress header: Step 1 of 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateToLogin) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Step 1 of 2", fontSize = 11.sp, color = SecondaryText)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { 0.5f },
                    modifier = Modifier.width(70.dp).height(4.dp).clip(CircleShape),
                    color = ForestGreen,
                    trackColor = BorderColor
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Brand Logo & Companion
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "SAATH", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = ForestGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Spa, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                }
                Text(text = "Always with you", fontSize = 11.sp, color = SecondaryText)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Create Your\nAccount", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                Text(text = "One step closer to a healthier, happier you.", fontSize = 12.sp, color = SecondaryText)

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Spa, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Track your wellbeing", fontSize = 11.sp, color = DarkNavy)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Get AI support", fontSize = 11.sp, color = DarkNavy)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stay connected", fontSize = 11.sp, color = DarkNavy)
                }
            }

            SaathiAvatar(avatarId = "aarav", size = 88.dp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // White Sign Up Form
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = SecondaryText) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("signup_name_input")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = SecondaryText) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("signup_email_input")
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password (min 8 chars)") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = SecondaryText) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("signup_password_input")
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = SecondaryText) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("signup_confirm_password_input")
                )

                // Consent checkboxes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = agreeTerms,
                        onCheckedChange = { agreeTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = ForestGreen)
                    )
                    Text(
                        text = "I agree to Terms of Service and Privacy Policy",
                        fontSize = 11.sp,
                        color = DarkNavy
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = receiveTips,
                        onCheckedChange = { receiveTips = it },
                        colors = CheckboxDefaults.colors(checkedColor = ForestGreen)
                    )
                    Text(
                        text = "I'd like to receive helpful tips and updates from SAATH",
                        fontSize = 11.sp,
                        color = DarkNavy
                    )
                }

                // Submit Button
                SaathPrimaryButton(
                    text = "Create Account",
                    onClick = onSignUpSuccess,
                    enabled = agreeTerms && fullName.isNotBlank() && email.isNotBlank(),
                    testTag = "signup_submit_button"
                )

                // Already have account
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Already have an account? ", fontSize = 12.sp, color = SecondaryText)
                    Text(
                        text = "Login ›",
                        fontSize = 12.sp,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }
        }
    }
}
