package com.tugi.tugiscad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tugi.tugiscad.ui.viewmodel.CADViewModel
import com.tugi.tugiscad.ui.viewmodel.DrawingTool
import com.tugi.tugiscad.ui.viewmodel.SnapMode
import java.util.Locale

/**
 * Çizim Araçları Toolbar - Sol tarafta yer alır
 */
@Composable
fun DrawingToolbar(
    viewModel: CADViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Seçim Aracı
        ToolButton(
            icon = Icons.Default.TouchApp,
            label = "Seç",
            isSelected = viewModel.activeTool.value == DrawingTool.SELECT,
            onClick = { viewModel.setActiveTool(DrawingTool.SELECT) }
        )

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        // Çizgi
        ToolButton(
            icon = Icons.Default.Timeline,
            label = "Çizgi",
            isSelected = viewModel.activeTool.value == DrawingTool.LINE,
            onClick = { viewModel.setActiveTool(DrawingTool.LINE) }
        )

        // Çoklu Çizgi
        ToolButton(
            icon = Icons.Default.ShowChart,
            label = "Çoklu",
            isSelected = viewModel.activeTool.value == DrawingTool.POLYLINE,
            onClick = { viewModel.setActiveTool(DrawingTool.POLYLINE) }
        )

        // Daire
        ToolButton(
            icon = Icons.Default.Circle,
            label = "Daire",
            isSelected = viewModel.activeTool.value == DrawingTool.CIRCLE,
            onClick = { viewModel.setActiveTool(DrawingTool.CIRCLE) }
        )

        // Dikdörtgen
        ToolButton(
            icon = Icons.Default.Rectangle,
            label = "Dikdörtgen",
            isSelected = viewModel.activeTool.value == DrawingTool.RECTANGLE,
            onClick = { viewModel.setActiveTool(DrawingTool.RECTANGLE) }
        )

        // Yay
        ToolButton(
            icon = Icons.Default.Architecture,
            label = "Yay",
            isSelected = viewModel.activeTool.value == DrawingTool.ARC,
            onClick = { viewModel.setActiveTool(DrawingTool.ARC) }
        )

        // Elips
        ToolButton(
            icon = Icons.Default.Adjust,
            label = "Elips",
            isSelected = viewModel.activeTool.value == DrawingTool.ELLIPSE,
            onClick = { viewModel.setActiveTool(DrawingTool.ELLIPSE) }
        )

        // Nokta
        ToolButton(
            icon = Icons.Default.FiberManualRecord,
            label = "Nokta",
            isSelected = viewModel.activeTool.value == DrawingTool.POINT,
            onClick = { viewModel.setActiveTool(DrawingTool.POINT) }
        )

        // Metin
        ToolButton(
            icon = Icons.Default.TextFields,
            label = "Metin",
            isSelected = viewModel.activeTool.value == DrawingTool.TEXT,
            onClick = { viewModel.setActiveTool(DrawingTool.TEXT) }
        )

        // Tarama (Hatch)
        ToolButton(
            icon = Icons.Default.GridOn,
            label = "Tarama",
            isSelected = viewModel.activeTool.value == DrawingTool.HATCH,
            onClick = { viewModel.setActiveTool(DrawingTool.HATCH) }
        )

        // Sembol
        ToolButton(
            icon = Icons.Default.AccountTree,
            label = "Sembol",
            isSelected = viewModel.activeTool.value == DrawingTool.SYMBOL,
            onClick = { viewModel.setActiveTool(DrawingTool.SYMBOL) }
        )

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        // Taşı
        ToolButton(
            icon = Icons.Default.OpenWith,
            label = "Taşı",
            isSelected = viewModel.activeTool.value == DrawingTool.MOVE,
            onClick = { viewModel.setActiveTool(DrawingTool.MOVE) }
        )

        // Kopyala
        ToolButton(
            icon = Icons.Default.ContentCopy,
            label = "Kopyala",
            isSelected = viewModel.activeTool.value == DrawingTool.COPY,
            onClick = { viewModel.setActiveTool(DrawingTool.COPY) }
        )

        // Döndür
        ToolButton(
            icon = Icons.Default.RotateRight,
            label = "Döndür",
            isSelected = viewModel.activeTool.value == DrawingTool.ROTATE,
            onClick = { viewModel.setActiveTool(DrawingTool.ROTATE) }
        )

        // Ölçekle
        ToolButton(
            icon = Icons.Default.ZoomOutMap,
            label = "Ölçekle",
            isSelected = viewModel.activeTool.value == DrawingTool.SCALE,
            onClick = { viewModel.setActiveTool(DrawingTool.SCALE) }
        )

        // Aynala
        ToolButton(
            icon = Icons.Default.FlipCameraAndroid,
            label = "Aynala",
            isSelected = viewModel.activeTool.value == DrawingTool.MIRROR,
            onClick = { viewModel.setActiveTool(DrawingTool.MIRROR) }
        )

        // Kırp
        ToolButton(
            icon = Icons.Default.ContentCut,
            label = "Kırp",
            isSelected = viewModel.activeTool.value == DrawingTool.TRIM,
            onClick = { viewModel.setActiveTool(DrawingTool.TRIM) }
        )

        // Uzat
        ToolButton(
            icon = Icons.Default.CallMade,
            label = "Uzat",
            isSelected = viewModel.activeTool.value == DrawingTool.EXTEND,
            onClick = { viewModel.setActiveTool(DrawingTool.EXTEND) }
        )

        // Ofset
        ToolButton(
            icon = Icons.Default.Polyline,
            label = "Ofset",
            isSelected = viewModel.activeTool.value == DrawingTool.OFFSET,
            onClick = { viewModel.setActiveTool(DrawingTool.OFFSET) }
        )
    }
}

/**
 * Alt Toolbar - Durum çubuğu ve hızlı erişim araçları
 */
@Composable
fun BottomToolbar(
    viewModel: CADViewModel,
    modifier: Modifier = Modifier,
    showLayerManager: Boolean = false,
    onToggleLayerManager: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sol taraf - Koordinatlar ve ölçek
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "X: 0.00  Y: 0.00",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Ölçek: ${viewModel.currentProject.value?.scale ?: 1000}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Sağ taraf - Snap modları ve diğer ayarlar
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tabakalar
            IconToggleButton(
                checked = showLayerManager,
                onCheckedChange = { onToggleLayerManager() }
            ) {
                Icon(
                    Icons.Default.Layers,
                    contentDescription = "Tabakalar",
                    tint = if (showLayerManager)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Grid
            IconToggleButton(
                checked = viewModel.showGrid.value,
                onCheckedChange = { viewModel.toggleGrid() }
            ) {
                Icon(
                    Icons.Default.GridOn,
                    contentDescription = "Grid",
                    tint = if (viewModel.showGrid.value)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Snap Modu
            SnapModeToggle(viewModel)

            // Zoom seviyesi
            Text(
                text = "Zoom: ${String.format(Locale.US, "%.1f", viewModel.zoomLevel.value * 100)}%",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ToolButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SnapModeToggle(viewModel: CADViewModel) {
    val expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded.value = true }) {
            Icon(
                Icons.Default.CenterFocusStrong,
                contentDescription = "Snap",
                tint = if (viewModel.snapMode.value != SnapMode.NONE)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            SnapMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.name) },
                    onClick = {
                        viewModel.setSnapMode(mode)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

