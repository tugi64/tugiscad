package com.tugi.tugiscad.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tugi.tugiscad.data.model.CADProject
import com.tugi.tugiscad.data.model.MeasureUnit

/**
 * Proje Özellikleri Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectPropertiesDialog(
    project: CADProject,
    onDismiss: () -> Unit,
    onConfirm: (CADProject) -> Unit
) {
    var projectName by remember { mutableStateOf(project.name) }
    var description by remember { mutableStateOf(project.description) }
    var scale by remember { mutableStateOf(project.scale.toString()) }
    var selectedUnit by remember { mutableStateOf(project.unit) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Proje Özellikleri") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Proje Adı") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                OutlinedTextField(
                    value = scale,
                    onValueChange = { scale = it },
                    label = { Text("Ölçek (1:?)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Birim seçici
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedUnit.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ölçü Birimi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        MeasureUnit.entries.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit.name) },
                                onClick = {
                                    selectedUnit = unit
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedProject = project.copy(
                        name = projectName,
                        description = description,
                        scale = scale.toDoubleOrNull() ?: project.scale,
                        unit = selectedUnit,
                        modifiedDate = System.currentTimeMillis()
                    )
                    onConfirm(updatedProject)
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

/**
 * Yeni Proje Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, MeasureUnit) -> Unit
) {
    var projectName by remember { mutableStateOf("Yeni Proje") }
    var scale by remember { mutableStateOf("1000") }
    var selectedUnit by remember { mutableStateOf(MeasureUnit.METER) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Proje Oluştur") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Proje Adı") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = scale,
                    onValueChange = { scale = it },
                    label = { Text("Ölçek (1:?)") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedUnit.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ölçü Birimi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        MeasureUnit.entries.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit.name) },
                                onClick = {
                                    selectedUnit = unit
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        projectName,
                        scale.toDoubleOrNull() ?: 1000.0,
                        selectedUnit
                    )
                }
            ) {
                Text("Oluştur")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

/**
 * Obje Özellikleri Dialog
 */
@Suppress("unused")
@Composable
fun ObjectPropertiesDialog(
    objectInfo: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Obje Özellikleri") },
        text = {
            Text(objectInfo)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam")
            }
        }
    )
}

