package com.tugi.tugiscad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tugi.tugiscad.data.model.Layer
import com.tugi.tugiscad.data.model.LineType
import com.tugi.tugiscad.ui.viewmodel.CADViewModel
import java.util.UUID

@Composable
fun LayerManager(viewModel: CADViewModel, modifier: Modifier = Modifier) {
    val project by remember { viewModel.currentProject }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier
        .fillMaxHeight()
        .width(280.dp)
        .background(MaterialTheme.colorScheme.surface)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Tabakalar", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tabaka Ekle")
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        val layers = project?.layers ?: emptyList()
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(layers) { layer ->
                LayerItem(
                    layer = layer,
                    isActive = layer.id == viewModel.activeLayer.value?.id,
                    onLayerClick = { viewModel.setActiveLayer(layer) },
                    onVisibilityToggle = {
                        val idx = project?.layers?.indexOfFirst { it.id == layer.id } ?: -1
                        if (idx >= 0) project?.layers?.get(idx)?.let { project?.layers?.set(idx, it.copy(isVisible = !it.isVisible)) }
                    },
                    onLockToggle = {
                        val idx = project?.layers?.indexOfFirst { it.id == layer.id } ?: -1
                        if (idx >= 0) project?.layers?.get(idx)?.let { project?.layers?.set(idx, it.copy(isLocked = !it.isLocked)) }
                    },
                    onDelete = {
                        project?.layers?.removeIf { it.id == layer.id }
                    }
                )
            }
        }

        if (showAddDialog) {
            AddLayerDialog(onConfirm = { name, color, lineType ->
                val newLayer = Layer(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    color = color,
                    lineType = lineType,
                    isVisible = true,
                    isLocked = false
                )
                viewModel.addLayer(newLayer)
                showAddDialog = false
            }, onDismiss = { showAddDialog = false })
        }
    }
}

@Composable
private fun LayerItem(
    layer: Layer,
    isActive: Boolean,
    onLayerClick: () -> Unit,
    onVisibilityToggle: () -> Unit,
    onLockToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onLayerClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .size(24.dp)
                    .background(layer.color))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = layer.name, style = MaterialTheme.typography.bodyLarge)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = "Görünürlük")
                }
                IconButton(onClick = onLockToggle) {
                    Icon(if (layer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen, contentDescription = "Kilitle")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Sil")
                }
            }
        }
    }
}

@Composable
private fun AddLayerDialog(
    onConfirm: (String, Color, LineType) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("Yeni Tabaka") }
    var color by remember { mutableStateOf(Color.White) }
    var lineType by remember { mutableStateOf(LineType.CONTINUOUS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Tabaka Ekle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tabaka Adı") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // basit renk seçenekleri
                    listOf(Color.White, Color.Red, Color.Green, Color.Blue, Color.Yellow).forEach { c ->
                        Box(modifier = Modifier
                            .size(32.dp)
                            .background(c)
                            .clickable { color = c })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, color, lineType) }) { Text("Ekle") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}
