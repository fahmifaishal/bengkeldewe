package com.negarfahmifaishal.bengkeldewe.ui.screens.edit_booking

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.negarfahmifaishal.bengkeldewe.R
import com.negarfahmifaishal.bengkeldewe.data.model.Booking
import com.negarfahmifaishal.bengkeldewe.utils.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookingScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditBookingViewModel = viewModel()
) {
    val loadState by viewModel.loadState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    var namaMotor by remember { mutableStateOf("") }
    var keluhan by remember { mutableStateOf("") }
    var tanggalBooking by remember { mutableStateOf("") }
    var currentImageUrl by remember { mutableStateOf("") }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }
    var userId by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        newImageUri = uri
    }

    // Load initial data
    LaunchedEffect(bookingId) {
        viewModel.getBookingById(bookingId)
    }

    // Populate fields when successfully loaded
    LaunchedEffect(loadState) {
        if (loadState is UiState.Success) {
            val booking = (loadState as UiState.Success<Booking>).data
            namaMotor = booking.namaMotor
            keluhan = booking.keluhan
            tanggalBooking = booking.tanggalBooking
            currentImageUrl = booking.imageUrl
            userId = booking.userId
        }
    }

    // Navigate back on successful update
    LaunchedEffect(updateState) {
        if (updateState is UiState.Success) {
            viewModel.resetUpdateState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.title_edit_booking), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.desc_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = loadState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.msg_error_prefix) + state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.getBookingById(bookingId) }) {
                            Text(stringResource(id = R.string.btn_retry))
                        }
                    }
                }
                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Image Display & Picker Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (newImageUri != null) {
                                    AsyncImage(
                                        model = newImageUri,
                                        contentDescription = stringResource(id = R.string.desc_new_motor_photo),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (currentImageUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = currentImageUrl,
                                        contentDescription = stringResource(id = R.string.desc_motor_photo),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = stringResource(id = R.string.desc_select_photo),
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = stringResource(id = R.string.placeholder_select_photo_edit),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Nama Motor
                        OutlinedTextField(
                            value = namaMotor,
                            onValueChange = { namaMotor = it },
                            label = { Text(stringResource(id = R.string.label_motor_name)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Keluhan
                        OutlinedTextField(
                            value = keluhan,
                            onValueChange = { keluhan = it },
                            label = { Text(stringResource(id = R.string.label_complaint)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3,
                            maxLines = 5
                        )

                        // Tanggal Booking
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = tanggalBooking,
                                onValueChange = {},
                                label = { Text(stringResource(id = R.string.label_booking_date)) },
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = stringResource(id = R.string.desc_select_date)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showDatePicker = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                viewModel.updateBooking(
                                    id = bookingId,
                                    userId = userId,
                                    namaMotor = namaMotor,
                                    keluhan = keluhan,
                                    tanggalBooking = tanggalBooking,
                                    currentImageUrl = currentImageUrl,
                                    newImageUri = newImageUri
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800), // Tema Orange Bengkel
                                contentColor = Color.White
                            ),
                            enabled = updateState !is UiState.Loading
                        ) {
                            if (updateState is UiState.Loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.btn_save_changes),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        // Update Error Display
                        if (updateState is UiState.Error) {
                            val errorMessage = (updateState as UiState.Error).message
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Material 3 DatePickerDialog
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedDateMillis = datePickerState.selectedDateMillis
                                if (selectedDateMillis != null) {
                                    tanggalBooking = formatter.format(Date(selectedDateMillis))
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text(stringResource(id = R.string.btn_choose))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(stringResource(id = R.string.btn_cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}
