package com.tugi.tugiscad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tugi.tugiscad.ui.viewmodel.CADViewModel
import com.tugi.tugiscad.ui.viewmodel.DrawingTool

/**
 * Ana Menü Çubuğu - TugisCAD menü sistemi
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CADTopBar(
    viewModel: CADViewModel,
    onMenuClick: (MenuType) -> Unit
) {
    var expanded by remember { mutableStateOf<MenuType?>(null) }

    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TugisCAD", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.width(16.dp))

                // Menüler
                CADMenuItem("Proje", MenuType.PROJECT) {
                    expanded = if (expanded == MenuType.PROJECT) null else MenuType.PROJECT
                }
                CADMenuItem("Çiz", MenuType.DRAW) {
                    expanded = if (expanded == MenuType.DRAW) null else MenuType.DRAW
                }
                CADMenuItem("Düzenle", MenuType.EDIT) {
                    expanded = if (expanded == MenuType.EDIT) null else MenuType.EDIT
                }
                CADMenuItem("Görüntü", MenuType.VIEW) {
                    expanded = if (expanded == MenuType.VIEW) null else MenuType.VIEW
                }
                CADMenuItem("Sorgu", MenuType.QUERY) {
                    expanded = if (expanded == MenuType.QUERY) null else MenuType.QUERY
                }
                CADMenuItem("Araçlar", MenuType.TOOLS) {
                    expanded = if (expanded == MenuType.TOOLS) null else MenuType.TOOLS
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )

    // Dropdown menüler
    expanded?.let { menuType ->
        CADDropdownMenu(menuType, viewModel) {
            expanded = null
            onMenuClick(menuType)
        }
    }
}

@Composable
private fun CADMenuItem(
    text: String,
    menuType: MenuType,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(text, color = Color.White)
    }
}

@Composable
private fun CADDropdownMenu(
    menuType: MenuType,
    viewModel: CADViewModel,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss
    ) {
        when (menuType) {
            MenuType.PROJECT -> ProjectMenuItems(viewModel, onDismiss)
            MenuType.DRAW -> DrawMenuItems(viewModel, onDismiss)
            MenuType.EDIT -> EditMenuItems(viewModel, onDismiss)
            MenuType.VIEW -> ViewMenuItems(viewModel, onDismiss)
            MenuType.QUERY -> QueryMenuItems(viewModel, onDismiss)
            MenuType.TOOLS -> ToolsMenuItems(viewModel, onDismiss)
        }
    }
}

@Composable
private fun ProjectMenuItems(viewModel: CADViewModel, onDismiss: () -> Unit) {
    DropdownMenuItem(
        text = { Text("Yeni Proje") },
        onClick = {
            viewModel.createNewProject("Yeni Proje")
            onDismiss()
        },
        leadingIcon = { Icon(Icons.Default.Add, null) }
    )
    DropdownMenuItem(
        text = { Text("Aç") },
        onClick = { onDismiss() },
        leadingIcon = { Icon(Icons.Default.FolderOpen, null) }
    )
    DropdownMenuItem(
        text = { Text("Kaydet") },
        onClick = { onDismiss() },
        leadingIcon = { Icon(Icons.Default.Save, null) }
    )
    DropdownMenuItem(
        text = { Text("Farklı Kaydet") },
        onClick = { onDismiss() },
        leadingIcon = { Icon(Icons.Default.SaveAs, null) }
    )
    HorizontalDivider()
    DropdownMenuItem(
        text = { Text("Yazdır") },
        onClick = { onDismiss() },
        leadingIcon = { Icon(Icons.Default.Print, null) }
    )
}

@Composable
private fun DrawMenuItems(viewModel: CADViewModel, onDismiss: () -> Unit) {
    DropdownMenuItem(
        text = { Text("Çizgi") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.LINE)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Çoklu Çizgi") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.POLYLINE)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Daire") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.CIRCLE)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Yay") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.ARC)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Elips") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.ELLIPSE)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Dikdörtgen") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.RECTANGLE)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Nokta") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.POINT)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Metin") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.TEXT)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Tarama") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.HATCH)
            onDismiss()
        }
    )
}

@Composable
private fun EditMenuItems(viewModel: CADViewModel, onDismiss: () -> Unit) {
    DropdownMenuItem(
        text = { Text("Sil") },
        onClick = {
            viewModel.deleteSelectedObjects()
            onDismiss()
        },
        leadingIcon = { Icon(Icons.Default.Delete, null) }
    )
    HorizontalDivider()
    DropdownMenuItem(
        text = { Text("Taşı") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.MOVE)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Kopyala") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.COPY)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Döndür") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.ROTATE)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Ölçekle") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.SCALE)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Aynala") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.MIRROR)
            onDismiss()
        }
    )
    HorizontalDivider()
    DropdownMenuItem(
        text = { Text("Kes/Kırp") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.TRIM)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Uzat") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.EXTEND)
            onDismiss()
        }
    )
    DropdownMenuItem(
        text = { Text("Paralel") },
        onClick = {
            viewModel.setActiveTool(DrawingTool.OFFSET)
            onDismiss()
        }
    )
}

@Composable
private fun ViewMenuItems(viewModel: CADViewModel, onDismiss: () -> Unit) {
    DropdownMenuItem(
        text = { Text("Yaklaş") },
        onClick = {
            viewModel.zoomIn()
            onDismiss()
        },
        leadingIcon = { Icon(Icons.Default.ZoomIn, null) }
    )
    DropdownMenuItem(
        text = { Text("Uzaklaş") },
        onClick = {
            viewModel.zoomOut()
            onDismiss()
        },
        leadingIcon = { Icon(Icons.Default.ZoomOut, null) }
    )
    DropdownMenuItem(
        text = { Text("Tümünü Gör") },
        onClick = {
            viewModel.resetView()
            onDismiss()
        },
        leadingIcon = { Icon(Icons.Default.FitScreen, null) }
    )
    HorizontalDivider()
    DropdownMenuItem(
        text = { Text(if (viewModel.showGrid.value) "Grid Gizle" else "Grid Göster") },
        onClick = {
            viewModel.toggleGrid()
            onDismiss()
        },
        leadingIcon = { Icon(Icons.Default.GridOn, null) }
    )
}

@Composable
private fun QueryMenuItems(viewModel: CADViewModel, onDismiss: () -> Unit) {
    DropdownMenuItem(
        text = { Text("Obje Özellikleri") },
        onClick = { onDismiss() },
        leadingIcon = { Icon(Icons.Default.Info, null) }
    )
    DropdownMenuItem(
        text = { Text("Alan Sorgu") },
        onClick = { onDismiss() }
    )
    DropdownMenuItem(
        text = { Text("Koordinat Sorgu") },
        onClick = { onDismiss() }
    )
    DropdownMenuItem(
        text = { Text("Mesafe Ölç") },
        onClick = { onDismiss() }
    )
}

@Composable
private fun ToolsMenuItems(viewModel: CADViewModel, onDismiss: () -> Unit) {
    DropdownMenuItem(
        text = { Text("Tabaka Yöneticisi") },
        onClick = { onDismiss() },
        leadingIcon = { Icon(Icons.Default.Layers, null) }
    )
    DropdownMenuItem(
        text = { Text("Ayarlar") },
        onClick = { onDismiss() },
        leadingIcon = { Icon(Icons.Default.Settings, null) }
    )
}

enum class MenuType {
    PROJECT, DRAW, EDIT, VIEW, QUERY, TOOLS
}

