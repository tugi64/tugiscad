package com.tugi.tugiscad.ui.components
}
    )
        }
            }
                Text("İptal")
            TextButton(onClick = onDismiss) {
        dismissButton = {
        },
            }
                Text("Ekle")
            TextButton(onClick = { onConfirm(layerName, selectedColor, selectedLineType) }) {
        confirmButton = {
        },
            }
                // TODO: Çizgi tipi seçici
                Text("Çizgi Tipi:", style = MaterialTheme.typography.bodySmall)

                }
                    }
                        )
                                )
                                    else Modifier
                                        Modifier.padding(2.dp)
                                    if (selectedColor == color)
                                .then(
                                .background(color)
                                .size(32.dp)
                            modifier = Modifier
                        Box(
                    ).forEach { color ->
                        Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray
                        Color.White, Color.Red, Color.Green, Color.Blue,
                    listOf(
                ) {
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                    modifier = Modifier.fillMaxWidth(),
                Row(
                Text("Renk:", style = MaterialTheme.typography.bodySmall)

                )
                    modifier = Modifier.fillMaxWidth()
                    label = { Text("Tabaka Adı") },
                    onValueChange = { layerName = it },
                    value = layerName,
                OutlinedTextField(
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        text = {
        title = { Text("Yeni Tabaka Ekle") },
        onDismissRequest = onDismiss,
    AlertDialog(

    var selectedLineType by remember { mutableStateOf(LineType.CONTINUOUS) }
    var selectedColor by remember { mutableStateOf(Color.White) }
    var layerName by remember { mutableStateOf("Tabaka ${System.currentTimeMillis() % 1000}") }
) {
    onConfirm: (String, Color, LineType) -> Unit
    onDismiss: () -> Unit,
private fun AddLayerDialog(
@Composable

}
    }
        }
            }
                }
                    )
                        modifier = Modifier.size(16.dp)
                        contentDescription = null,
                        if (layer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    Icon(
                ) {
                    modifier = Modifier.size(32.dp)
                    onClick = onLockToggle,
                IconButton(

                }
                    )
                        modifier = Modifier.size(16.dp)
                        contentDescription = null,
                        if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    Icon(
                ) {
                    modifier = Modifier.size(32.dp)
                    onClick = onVisibilityToggle,
                IconButton(
            Row {
            // Kontroller

            )
                style = MaterialTheme.typography.bodyMedium
                    .padding(horizontal = 8.dp),
                    .weight(1f)
                modifier = Modifier
                text = layer.name,
            Text(
            // Tabaka adı

            )
                    .background(layer.color)
                    .size(24.dp)
                modifier = Modifier
            Box(
            // Renk göstergesi
        ) {
            verticalAlignment = Alignment.CenterVertically
            horizontalArrangement = Arrangement.SpaceBetween,
                .padding(8.dp),
                .fillMaxWidth()
            modifier = Modifier
        Row(
    ) {
        )
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.primaryContainer
            containerColor = if (isActive)
        colors = CardDefaults.cardColors(
        modifier = Modifier.fillMaxWidth(),
    Card(
) {
    onDelete: () -> Unit
    onLockToggle: () -> Unit,
    onVisibilityToggle: () -> Unit,
    onLayerClick: () -> Unit,
    isActive: Boolean,
    layer: Layer,
private fun LayerItem(
@Composable

}
    }
        )
            }
                showAddLayerDialog = false
                )
                    )
                        isLocked = false
                        isVisible = true,
                        lineType = lineType,
                        color = color,
                        name = layerName,
                        id = UUID.randomUUID().toString(),
                    Layer(
                viewModel.addLayer(
            onConfirm = { layerName, color, lineType ->
            onDismiss = { showAddLayerDialog = false },
        AddLayerDialog(
    if (showAddLayerDialog) {
    // Yeni Tabaka Dialog

    }
        }
            }
                }
                    )
                        }
                            // TODO: Tabaka sil
                        onDelete = {
                        },
                            // TODO: Tabaka kilitleme
                        onLockToggle = {
                        },
                            // TODO: Tabaka görünürlüğünü değiştir
                        onVisibilityToggle = {
                        onLayerClick = { viewModel.setActiveLayer(layer) },
                        isActive = layer.id == viewModel.activeLayer.value?.id,
                        layer = layer,
                    LayerItem(
                items(layers) { layer ->
            project?.layers?.let { layers ->
        ) {
            verticalArrangement = Arrangement.spacedBy(4.dp)
            modifier = Modifier.weight(1f),
        LazyColumn(
        // Tabaka Listesi

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        }
            }
                Icon(Icons.Default.Add, "Tabaka Ekle")
            IconButton(onClick = { showAddLayerDialog = true }) {
            )
                style = MaterialTheme.typography.titleMedium
                "Tabakalar",
            Text(
        ) {
            verticalAlignment = Alignment.CenterVertically
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        Row(
        // Başlık
    ) {
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxHeight()
            .width(280.dp)
        modifier = modifier
    Column(

    val project by viewModel.currentProject
    var showAddLayerDialog by remember { mutableStateOf(false) }
) {
    modifier: Modifier = Modifier
    viewModel: CADViewModel,
fun LayerManager(
@Composable
 */
 * Tabaka (Layer) Yöneticisi - Sağ panel
/**

import java.util.UUID
import com.tugi.tugiscad.ui.viewmodel.CADViewModel
import com.tugi.tugiscad.data.model.LineType
import com.tugi.tugiscad.data.model.Layer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background


