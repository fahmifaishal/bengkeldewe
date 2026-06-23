package com.negarfahmifaishal.bengkeldewe.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.negarfahmifaishal.bengkeldewe.R
import com.negarfahmifaishal.bengkeldewe.ui.components.BookingItem
import com.negarfahmifaishal.bengkeldewe.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onAddClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var bookingIdToDelete by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.title_home)) },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(id = R.string.desc_profile)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text(stringResource(id = R.string.fab_add_booking))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.msg_no_bookings),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp,
                                bottom = 80.dp
                            ),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.data) { booking ->
                                BookingItem(
                                    booking = booking,
                                    onEditClick = onEditClick,
                                    onDeleteClick = { id ->
                                        bookingIdToDelete = id
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.msg_error_prefix) + state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.getBookings() }) {
                            Text(stringResource(id = R.string.btn_retry))
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && bookingIdToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                bookingIdToDelete = null
            },
            title = { Text(stringResource(id = R.string.dialog_delete_title)) },
            text = { Text(stringResource(id = R.string.dialog_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        bookingIdToDelete?.let { id ->
                            viewModel.deleteBooking(id)
                        }
                        showDeleteDialog = false
                        bookingIdToDelete = null
                    }
                ) {
                    Text(stringResource(id = R.string.dialog_yes), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        bookingIdToDelete = null
                    }
                ) {
                    Text(stringResource(id = R.string.dialog_cancel))
                }
            }
        )
    }
}
