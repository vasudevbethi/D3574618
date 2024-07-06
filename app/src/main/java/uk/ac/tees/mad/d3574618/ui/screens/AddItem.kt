package uk.ac.tees.mad.d3574618.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.data.domain.AddItemUiState
import uk.ac.tees.mad.d3574618.data.domain.ItemCategory
import uk.ac.tees.mad.d3574618.data.domain.ItemCondition
import uk.ac.tees.mad.d3574618.handleImageCapture
import uk.ac.tees.mad.d3574618.handleImageSelection
import uk.ac.tees.mad.d3574618.showToast
import uk.ac.tees.mad.d3574618.ui.components.DropdownMenu
import uk.ac.tees.mad.d3574618.ui.components.PhotoPickerOptionBottomSheet
import uk.ac.tees.mad.d3574618.ui.components.SwapPopupBox
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination
import uk.ac.tees.mad.d3574618.ui.theme.primaryGreen
import uk.ac.tees.mad.d3574618.ui.viewmodels.AddItemViewModel

object AddItemsDestination : NavigationDestination {
    override val route = "add_item"
    override val titleRes: Int = R.string.add_item
}


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddItems(
    viewModel: AddItemViewModel = hiltViewModel(),
    onAddItemSuccess: (String) -> Unit,
    onNavigateUp: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showBottomSheet by remember { mutableStateOf(false) }
    val addItemState = viewModel.itemAddState.collectAsState(initial = null)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scope = rememberCoroutineScope()

    //Taking images from gallary
    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }
    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            selectedImage = uri
            if (uri != null) {
                val result = handleImageSelection(uri, context)
                val images = uiState.value.images
                viewModel.updateUiState(uiState.value.copy(images = images + result))
            }
        }

    val requestCameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                val result = handleImageCapture(it)
                val images = uiState.value.images
                viewModel.updateUiState(uiState.value.copy(images = images + result))
            }
        }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(addItemState.value?.data) {
        scope.launch {
            if (addItemState.value?.data?.isNotEmpty() == true) {
                addItemState.value?.data?.let {
                    context.showToast("Item added")
                    onAddItemSuccess(it)
                }
            }
        }
    }

    LaunchedEffect(addItemState.value?.error) {
        scope.launch {
            if (addItemState.value?.error?.isNotEmpty() == true) {
                addItemState.value?.error?.let { context.showToast(it) }
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
            Icon(Icons.Default.Clear,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        viewModel.resetUiState()
                        onNavigateUp()
                    })

            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Create Offer", fontSize = 24.sp, fontWeight = FontWeight.Medium
                )
            }
        }

        HorizontalDivider()
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                if (uiState.value.images.isNotEmpty()) {
                    items(uiState.value.images) {
                        Box(modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .height(80.dp)
                            .aspectRatio(1f)
                            .clickable { showBottomSheet = true }
                            .border(BorderStroke(1.dp, Color.Gray), MaterialTheme.shapes.small),
                            contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).crossfade(true)
                                    .data(it).build(),
                                contentDescription = "Selected image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                item {
                    Box(modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .height(80.dp)
                        .aspectRatio(1f)
                        .clickable { showBottomSheet = true }
                        .border(BorderStroke(1.dp, Color.Gray), MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.AddAPhoto,
                            contentDescription = "Add photo",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.value.name,
                    onValueChange = {
                        viewModel.updateUiState(uiState.value.copy(name = it))
                    },
                    label = {
                        Text(text = "Title")
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )
                OutlinedTextField(
                    value = uiState.value.description,
                    onValueChange = {
                        viewModel.updateUiState(uiState.value.copy(description = it))
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Description")
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )
                OutlinedTextField(
                    value = uiState.value.keywords,
                    onValueChange = {
                        viewModel.updateUiState(uiState.value.copy(keywords = it))
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Keywords")
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )
                DropdownMenu(
                    list = ItemCategory.entries,
                    category = {
                        viewModel.updateUiState(uiState.value.copy(category = it.name))
                    },
                    focusManager = focusManager,
                    displayText = {
                        it.name
                    },
                    placeholder = "Category"
                )
                DropdownMenu(
                    list = ItemCondition.entries,
                    category = {
                        viewModel.updateUiState(uiState.value.copy(condition = it.name))
                    },
                    focusManager = focusManager,
                    displayText = {
                        it.name
                    },
                    placeholder = "Item condition"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(primaryGreen)
                    .clickable {
                        if (validateFields(uiState, context)) {
                            Log.d("UIS", uiState.value.toString())
                            viewModel.addItem()
                        }
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (addItemState.value?.isLoading == true) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(text = "List", fontSize = 20.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

    }
}


fun validateFields(uiState: State<AddItemUiState>, context: Context): Boolean {
    if (uiState.value.name.isBlank()) {
        context.showToast("Empty item name")
        return false
    }

    if (uiState.value.description.isBlank()) {
        context.showToast("Empty item description")
        return false
    }

    if (uiState.value.category.isBlank()) {
        context.showToast("Category not selected")
        return false
    }
    if (uiState.value.condition.isBlank()) {
        context.showToast("Item Condition not selected")
        return false
    }
    if (uiState.value.images.isEmpty()) {
        context.showToast("Item image is null")
        return false
    }
    return true
}