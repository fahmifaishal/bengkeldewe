package com.negarfahmifaishal.bengkeldewe.ui.screens.login

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.negarfahmifaishal.bengkeldewe.BuildConfig
import com.negarfahmifaishal.bengkeldewe.R
import com.negarfahmifaishal.bengkeldewe.utils.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    var googleErrorMsg by remember { mutableStateOf<String?>(null) }

    val loginState by viewModel.loginState.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Logo / Header
            Text(
                text = stringResource(id = R.string.app_name_banner),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFF9800)
            )

            Text(
                text = stringResource(id = R.string.app_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Google Sign-In Button
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        googleErrorMsg = null
                        try {
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val activityContext = context as? Activity
                            if (activityContext != null) {
                                val result = credentialManager.getCredential(activityContext, request)
                                val credential = result.credential

                                if (credential is CustomCredential &&
                                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                    val gEmail = googleIdTokenCredential.id
                                    val gName = googleIdTokenCredential.displayName ?: "User Google"
                                    val gPhotoUrl = googleIdTokenCredential.profilePictureUri?.toString() ?: ""
                                    
                                    viewModel.loginWithGoogle(gEmail, gName, gPhotoUrl)
                                } else {
                                    googleErrorMsg = "Format kredensial tidak sesuai"
                                }
                            } else {
                                googleErrorMsg = "Gagal memproses konteks sistem"
                            }
                        } catch (e: GetCredentialException) {
                            googleErrorMsg = e.message
                        } catch (e: Exception) {
                            googleErrorMsg = e.message
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = loginState !is UiState.Loading),
                enabled = loginState !is UiState.Loading
            ) {
                if (loginState is UiState.Loading) {
                    CircularProgressIndicator(
                        color = Color(0xFFFF9800),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(id = R.string.btn_google_sign_in),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // Google Error Display
            if (googleErrorMsg != null) {
                Text(
                    text = stringResource(id = R.string.msg_google_sign_in_error, googleErrorMsg!!),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
