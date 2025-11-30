package com.tugi.tugiscad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tugi.tugiscad.ui.viewmodel.CADViewModel
import com.tugi.tugiscad.ui.viewmodel.SnapMode

/**
 * NETCAD Benzeri Yakalama Modu Ayarları Dialog
 */
@Composable
fun SnapSettingsDialog(
    viewModel: CADViewModel,
    onDismiss: () -> Unit
) {
    val currentSnapMode = viewModel.snapMode.value

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Başlık
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Yakalama Modları",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Kapat")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // NETCAD benzeri snap açıklamaları
                Text(
                    text = "Çizim yaparken noktaları otomatik yakalama ayarları",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Snap modları listesi
                SnapModeOption(
                    icon = Icons.Default.CancelPresentation,
                    title = "Kapalı (Free)",
                    description = "Yakalama yok - Tam mouse pozisyonu",
                    keyboardShortcut = "F3",
                    isSelected = currentSnapMode == SnapMode.NONE,
                    onClick = {
                        viewModel.setSnapMode(SnapMode.NONE)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SnapModeOption(
                    icon = Icons.Default.FiberManualRecord,
                    title = "Son Nokta (Endpoint)",
                    description = "Çizgi ve objelerin uç noktalarını yakala",
                    keyboardShortcut = "F4",
                    isSelected = currentSnapMode == SnapMode.ENDPOINT,
                    onClick = {
                        viewModel.setSnapMode(SnapMode.ENDPOINT)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SnapModeOption(
                    icon = Icons.Default.Circle,
                    title = "Orta Nokta (Midpoint)",
                    description = "Çizgilerin orta noktasını yakala",
                    keyboardShortcut = "F5",
                    isSelected = currentSnapMode == SnapMode.MIDPOINT,
                    onClick = {
                        viewModel.setSnapMode(SnapMode.MIDPOINT)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SnapModeOption(
                    icon = Icons.Default.GridOn,
                    title = "Kesişim (Intersection)",
                    description = "İki çizginin kesişim noktasını yakala",
                    keyboardShortcut = "F6",
                    isSelected = currentSnapMode == SnapMode.INTERSECTION,
                    onClick = {
                        viewModel.setSnapMode(SnapMode.INTERSECTION)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SnapModeOption(
                    icon = Icons.Default.CenterFocusStrong,
                    title = "Merkez (Center)",
                    description = "Daire ve yay merkezlerini yakala",
                    keyboardShortcut = "F7",
                    isSelected = currentSnapMode == SnapMode.CENTER,
                    onClick = {
                        viewModel.setSnapMode(SnapMode.CENTER)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SnapModeOption(
                    icon = Icons.Default.Apps,
                    title = "Izgara (Grid)",
                    description = "Grid noktalarını yakala",
                    keyboardShortcut = "F9",
                    isSelected = currentSnapMode == SnapMode.GRID,
                    onClick = {
                        viewModel.setSnapMode(SnapMode.GRID)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SnapModeOption(
                    icon = Icons.Default.NearMe,
                    title = "En Yakın (Nearest)",
                    description = "Herhangi bir objenin en yakın noktasını yakala",
                    keyboardShortcut = "A",
                    isSelected = currentSnapMode == SnapMode.NEAREST,
                    onClick = {
                        viewModel.setSnapMode(SnapMode.NEAREST)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SnapModeOption(
                    icon = Icons.Default.SwapVert,
                    title = "Dik Nokta (Perpendicular)",
                    description = "Çizgiye dik noktayı yakala",
                    keyboardShortcut = "",
                    isSelected = currentSnapMode == SnapMode.PERPENDICULAR,
                    onClick = {
                        viewModel.setSnapMode(SnapMode.PERPENDICULAR)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SnapModeOption(
                    icon = Icons.Default.RadioButtonChecked,
                    title = "Teğet (Tangent)",
                    description = "Daire ve yaya teğet noktayı yakala",
                    keyboardShortcut = "",
                    isSelected = currentSnapMode == SnapMode.TANGENT,
                    onClick = {
                        viewModel.setSnapMode(SnapMode.TANGENT)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bilgi notu
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Çizim sırasında F-tuşları ile hızlıca değiştirebilirsiniz",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SnapModeOption(
    icon: ImageVector,
    title: String,
    description: String,
    keyboardShortcut: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (keyboardShortcut.isNotEmpty()) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = keyboardShortcut,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Seçili",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

