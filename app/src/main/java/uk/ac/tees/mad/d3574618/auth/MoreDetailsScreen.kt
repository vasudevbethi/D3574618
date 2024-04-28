package uk.ac.tees.mad.d3574618.auth

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.data.repository.LocationManager
import uk.ac.tees.mad.d3574618.handleImageCapture
import uk.ac.tees.mad.d3574618.handleImageSelection
import uk.ac.tees.mad.d3574618.showToast
import uk.ac.tees.mad.d3574618.ui.components.PhotoPickerOptionBottomSheet
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination
import uk.ac.tees.mad.d3574618.ui.viewmodels.ApplicationViewModel
import kotlin.random.Random

object MoreDetailDestination : NavigationDestination {
    override val route: String
        get() = "more_details"
    override val titleRes: Int
        get() = R.string.more_details

}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MoreDetailsScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    applicationViewModel: ApplicationViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    val uiState = viewModel.moreDetailsUiState.collectAsState().value
    val focusManager = LocalFocusManager.current
    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationState =
        applicationViewModel.locationFlow.collectAsState(initial = newLocation())

    val locationPermissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    val locationPermissionsState = rememberMultiplePermissionsState(
        locationPermissions
    )
    val activity = (context as ComponentActivity)
    val locationManager = LocationManager(context, activity)
    val isGpsEnabled = locationManager.gpsStatus.collectAsState(initial = false)

    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            selectedImage = uri
            if (uri != null) {
                val result = handleImageSelection(uri, context)
                viewModel.updateMoreDetailState(uiState.copy(images = result))
            }
        }

    val requestCameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                val result = handleImageCapture(it)
                viewModel.updateMoreDetailState(uiState.copy(images = result))
            }
        }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val updateDetailsStatus = viewModel.updateDetailsStatus.collectAsState(initial = null)



    LaunchedEffect(key1 = updateDetailsStatus.value?.isSuccess) {
        scope.launch {
            if (updateDetailsStatus.value?.isSuccess?.isNotEmpty() == true) {
                focusManager.clearFocus()
                val success = updateDetailsStatus.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                onSuccess()
            }
        }
    }

    LaunchedEffect(key1 = updateDetailsStatus.value?.isError) {
        scope.launch {
            if (updateDetailsStatus.value?.isError?.isNotEmpty() == true) {
                val error = updateDetailsStatus.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState,
                windowInsets = WindowInsets.ime
            ) {
                // Sheet content
                PhotoPickerOptionBottomSheet(onGalleryClick = {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                    galleryLauncher.launch("image/*")
                }, onCameraClick = {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                    if (!cameraPermission.status.isGranted) {
                        cameraPermission.launchPermissionRequest()
                    }
                    if (cameraPermission.status.isGranted) {
                        requestCameraLauncher.launch(null)
                    }
                })
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "More Details", fontSize = 24.sp, fontWeight = FontWeight.Medium
                )
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Please provide more information for better experience")
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .border(BorderStroke(2.dp, Color.Black), CircleShape)
                    .size(100.dp)
                    .clickable {
                        showBottomSheet = true
                    }
            ) {
                Box(
                    Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "",
                        modifier = Modifier.padding(4.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (uiState.images == null) {
                        Icon(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = "Add photo",
                            tint = Color.Gray,
                            modifier = Modifier.size(70.dp)
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .crossfade(true)
                                .data(uiState.images).build(),
                            contentDescription = "Selected image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(40.dp))
            OutlinedTextField(
                value = uiState.phone,
                onValueChange = {
                    viewModel.updateMoreDetailState(uiState.copy(phone = it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Phone")
                },
                maxLines = 1,
                visualTransformation = VisualTransformation.None,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = uiState.location,
                onValueChange = {
                    viewModel.updateMoreDetailState(uiState.copy(location = it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Location")
                },
                trailingIcon = {

                    Icon(
                        imageVector = Icons.Outlined.MyLocation,
                        contentDescription = "Get location",
                        modifier = Modifier.clickable {
                            if (locationPermissionsState.allPermissionsGranted) {
                                if (!isGpsEnabled.value) {
                                    locationManager.checkGpsSettings()
                                } else {

                                    viewModel.updateMoreDetailState(
                                        uiState.copy(
                                            location = locationManager.getAddressFromCoordinate(
                                                latitude = locationState.value.latitude,
                                                longitude = locationState.value.longitude
                                            )
                                        )
                                    )
                                }
                            } else {
                                locationPermissionsState.launchMultiplePermissionRequest()
                            }

                        }
                    )
                },
                visualTransformation = VisualTransformation.None,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.clearFocus()
                }),
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .height(50.dp)
                    .clickable {
                        if (validateFields(uiState, context)) {

                            viewModel.updateUser()
                        }
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (updateDetailsStatus.value?.isLoading == true) {
                    CircularProgressIndicator(color = Color.White)
                } else
                    Text(text = "Submit", color = Color.White)
            }
        }

    }
}

fun validateFields(uiState: MoreDetailsUiState, context: Context): Boolean {
    if (uiState.images?.isEmpty() == true || uiState.images == null) {
        context.showToast("Image not selected")
        return false
    }

    if (uiState.phone.isBlank()) {
        context.showToast("Empty phone number")
        return false
    }

    if (uiState.location.isBlank() || uiState.location == "No address found") {
        context.showToast("Location not selected")
        return false
    }
    return true
}

private fun newLocation(): Location {
    val location = Location("MyLocationProvider")
    location.apply {
        latitude = 51.509865 + Random.nextFloat()
        longitude = -0.118092 + Random.nextFloat()
    }
    return location
}

