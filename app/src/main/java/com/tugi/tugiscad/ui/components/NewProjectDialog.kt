package com.tugi.tugiscad.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tugi.tugiscad.data.model.MeasureUnit
import com.tugi.tugiscad.ui.viewmodel.CADViewModel

/**
 * Yeni Proje Oluşturma Dialog'u
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProjectDialog(
    viewModel: CADViewModel,
    onDismiss: () -> Unit
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Proje Adı
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Proje Adı") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Ölçek
                OutlinedTextField(
                    value = scale,
                    onValueChange = { scale = it },
                    label = { Text("Ölçek (1:...)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Birim Seçimi
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = when (selectedUnit) {
                            MeasureUnit.METER -> "Metre"
                            MeasureUnit.CENTIMETER -> "Santimetre"
                            MeasureUnit.MILLIMETER -> "Milimetre"
                            MeasureUnit.KILOMETER -> "Kilometre"
                            else -> "Metre"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Birim") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Metre") },
                            onClick = {
                                selectedUnit = MeasureUnit.METER
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Santimetre") },
                            onClick = {
                                selectedUnit = MeasureUnit.CENTIMETER
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Milimetre") },
                            onClick = {
                                selectedUnit = MeasureUnit.MILLIMETER
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Kilometre") },
                            onClick = {
                                selectedUnit = MeasureUnit.KILOMETER
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val scaleValue = scale.toDoubleOrNull() ?: 1000.0
                    viewModel.createNewProject(projectName, scaleValue, selectedUnit)
                    onDismiss()
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

