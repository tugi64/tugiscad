package com.tugi.tugiscad.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tugi.tugiscad.ui.components.*
import com.tugi.tugiscad.ui.viewmodel.CADViewModel

/**
 * Ana Ekran - TugisCAD'in ana kullanıcı arayüzü
 * Modern CAD layout: Üst menü, sol araç çubuğu, ortada canvas, alt durum çubuğu
 */
@Composable
fun MainScreen(
    viewModel: CADViewModel = viewModel()
) {
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showProjectPropertiesDialog by remember { mutableStateOf(false) }
    var showLayerManager by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CADTopBar(
                viewModel = viewModel,
                onMenuClick = { menuType ->
                    when (menuType) {
                        MenuType.PROJECT -> {
                            // Proje menüsü işlemleri burada handle edilecek
                        }
                        MenuType.TOOLS -> {
                            showLayerManager = !showLayerManager
                        }
                        else -> {
                            // Diğer menü işlemleri
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomToolbar(
                viewModel = viewModel,
                showLayerManager = showLayerManager,
                onToggleLayerManager = { showLayerManager = !showLayerManager }
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sol Araç Çubuğu
            DrawingToolbar(viewModel = viewModel)

            // Merkez - Çizim Alanı
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                CADCanvas(viewModel = viewModel)
            }

            // Sağ Panel - Tabaka Yöneticisi
            if (showLayerManager) {
                LayerManager(viewModel = viewModel)
            }
        }
    }

    // Dialog'lar
    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onConfirm = { name, scale, unit ->
                viewModel.createNewProject(name, scale, unit)
                showNewProjectDialog = false
            }
        )
    }

    if (showProjectPropertiesDialog) {
        viewModel.currentProject.value?.let { project ->
            ProjectPropertiesDialog(
                project = project,
                onDismiss = { showProjectPropertiesDialog = false },
                onConfirm = { updatedProject ->
                    viewModel.updateProject(updatedProject)
                    showProjectPropertiesDialog = false
                }
            )
        }
    }
}

