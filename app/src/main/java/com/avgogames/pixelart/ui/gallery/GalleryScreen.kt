package com.avgogames.pixelart.ui.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avgogames.pixelart.R
import com.avgogames.pixelart.core.model.ArtSummary
import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.ui.components.PixelArtImage
import com.avgogames.pixelart.ui.theme.PixelColors
import kotlin.math.roundToInt

/** Wiring: owns the ViewModel and hands plain state and callbacks to [GalleryContent]. */
@Composable
fun GalleryRoute(
    onOpenArt: (String) -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel = viewModel(factory = GalleryViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    GalleryContent(
        state = state,
        onSelectCategory = viewModel::selectCategory,
        onOpenArt = onOpenArt,
        onImport = onImport,
        onDeleteImported = viewModel::deleteImported,
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryContent(
    state: GalleryUiState,
    onSelectCategory: (Category) -> Unit,
    onOpenArt: (String) -> Unit,
    onImport: () -> Unit,
    onDeleteImported: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingDelete by remember { mutableStateOf<ArtSummary?>(null) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onImport,
                icon = { Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null) },
                text = { Text(stringResource(R.string.gallery_import)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 12.dp,
                bottom = padding.calculateBottomPadding() + 96.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                GalleryHeader(completed = state.completedCount, total = state.totalCount)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryTabs(selected = state.selectedCategory, onSelect = onSelectCategory)
            }
            when {
                state.isLoading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.items.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyImports(onImport = onImport)
                }
                else -> items(state.items, key = { it.art.id }) { summary ->
                    ArtCard(
                        summary = summary,
                        onClick = { onOpenArt(summary.art.id) },
                        onLongClick = if (summary.art.category == Category.IMPORTED) {
                            { pendingDelete = summary }
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }

    pendingDelete?.let { summary ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.gallery_delete_title, summary.art.title)) },
            text = { Text(stringResource(R.string.gallery_delete_body)) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteImported(summary.art.id)
                    pendingDelete = null
                }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

@Composable
private fun GalleryHeader(completed: Int, total: Int, modifier: Modifier = Modifier) {
    Column(modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(stringResource(R.string.gallery_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.gallery_subtitle, completed, total),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CategoryTabs(selected: Category, onSelect: (Category) -> Unit, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(Category.entries) { category ->
            val isSelected = category == selected
            Surface(
                onClick = { onSelect(category) },
                shape = RoundedCornerShape(50),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                contentColor = if (isSelected) Color.White else PixelColors.Ink,
                shadowElevation = if (isSelected) 2.dp else 0.dp,
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, PixelColors.Outline),
            ) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArtCard(
    summary: ArtSummary,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val progress = summary.progress
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PixelColors.CellLight),
            ) {
                PixelArtImage(art = summary.art, progress = progress, modifier = Modifier.fillMaxSize())
                if (progress.isComplete) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(PixelColors.Success),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(R.string.gallery_completed),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
            Row(
                Modifier.padding(start = 12.dp, end = 12.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    summary.art.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    stringResource(R.string.gallery_progress, (progress.fraction * 100).roundToInt()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LinearProgressIndicator(
                progress = { progress.fraction },
                color = if (progress.isComplete) PixelColors.Success else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                drawStopIndicator = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 2.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
            )
        }
    }
}

@Composable
private fun EmptyImports(onImport: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                Icons.Filled.AddPhotoAlternate,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp),
            )
            Text(
                stringResource(R.string.gallery_empty_imports),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = onImport) { Text(stringResource(R.string.gallery_import)) }
        }
    }
}
