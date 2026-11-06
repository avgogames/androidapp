package com.avgogames.pixelart.ui.importer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avgogames.pixelart.R
import com.avgogames.pixelart.core.convert.ConversionOptions
import com.avgogames.pixelart.ui.components.PixelArtImage
import com.avgogames.pixelart.ui.components.SquareIconButton
import com.avgogames.pixelart.ui.theme.PixelColors
import kotlin.math.roundToInt

/** Wiring: photo picker, ViewModel, and the one-shot "artwork created" navigation event. */
@Composable
fun ImportRoute(
    onBack: () -> Unit,
    onCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImportViewModel = viewModel(factory = ImportViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // The system photo picker needs no storage permission on any supported API level.
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(viewModel::onImagePicked)
    }
    val currentOnCreated by rememberUpdatedState(onCreated)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(viewModel, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.createdArtIds.collect { currentOnCreated(it) }
        }
    }
    val defaultTitle = stringResource(R.string.import_default_name)
    ImportContent(
        state = state,
        onBack = onBack,
        onPickImage = {
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onGridWidthChange = viewModel::onGridWidthChange,
        onMaxColorsChange = viewModel::onMaxColorsChange,
        onTitleChange = viewModel::onTitleChange,
        onCreate = { viewModel.save(defaultTitle) },
        modifier = modifier,
    )
}

@Composable
fun ImportContent(
    state: ImportUiState,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
    onGridWidthChange: (Int) -> Unit,
    onMaxColorsChange: (Int) -> Unit,
    onTitleChange: (String) -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SquareIconButton(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.coloring_back), onBack)
            Text(stringResource(R.string.import_title), style = MaterialTheme.typography.titleMedium)
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (!state.hasImage) {
                PickerCard(isWorking = state.isWorking, error = state.error, onPick = onPickImage)
            } else {
                PreviewCard(state)
                TextButton(onClick = onPickImage, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(stringResource(R.string.import_pick_other))
                }
                SettingSlider(
                    label = stringResource(R.string.import_grid, state.gridWidth),
                    value = state.gridWidth,
                    range = ConversionOptions.MIN_GRID..ConversionOptions.MAX_GRID,
                    step = 8,
                    onChange = onGridWidthChange,
                )
                SettingSlider(
                    label = stringResource(R.string.import_colors, state.maxColors),
                    value = state.maxColors,
                    range = ImportViewModel.MIN_COLORS..ConversionOptions.MAX_COLORS,
                    step = 1,
                    onChange = onMaxColorsChange,
                )
                OutlinedTextField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    label = { Text(stringResource(R.string.import_name)) },
                    placeholder = { Text(stringResource(R.string.import_default_name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (state.hasImage) {
            Button(
                onClick = onCreate,
                enabled = state.canSave,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
            ) {
                Text(stringResource(R.string.import_create))
            }
        }
    }
}

@Composable
private fun PickerCard(isWorking: Boolean, error: Boolean, onPick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onPick,
        enabled = !isWorking,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth().heightIn(min = 280.dp),
    ) {
        Column(
            Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            if (isWorking) {
                CircularProgressIndicator()
            } else {
                Icon(
                    Icons.Filled.AddPhotoAlternate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp),
                )
            }
            Text(stringResource(R.string.import_pick), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(if (error) R.string.import_error else R.string.import_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PreviewCard(state: ImportUiState, modifier: Modifier = Modifier) {
    val preview = state.preview
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .aspectRatio(preview?.let { it.width.toFloat() / it.height }?.coerceIn(0.5f, 2f) ?: 1f)
                .clip(RoundedCornerShape(10.dp))
                .background(PixelColors.CellLight),
            contentAlignment = Alignment.Center,
        ) {
            if (preview != null) {
                PixelArtImage(art = preview, progress = null, revealAll = true, modifier = Modifier.fillMaxSize())
            }
            if (state.isWorking) CircularProgressIndicator()
        }
    }
    if (preview != null) {
        Text(
            stringResource(R.string.import_summary, preview.width, preview.height, preview.palette.size),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingSlider(
    label: String,
    value: Int,
    range: IntRange,
    step: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = { raw ->
                val snapped = range.first + ((raw - range.first) / step).roundToInt() * step
                onChange(snapped.coerceIn(range))
            },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = if (step > 1) (range.last - range.first) / step - 1 else 0,
        )
    }
}
