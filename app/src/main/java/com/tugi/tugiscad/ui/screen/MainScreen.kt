package com.tugi.tugiscad.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    Scaffold(
        topBar = {
            CADTopBar(
                viewModel = viewModel,
                onMenuClick = { menuType ->
                    // Menü tıklama işlemleri
                }
            )
        },
        bottomBar = {
            BottomToolbar(viewModel = viewModel)
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

            // Sağ Panel (Tabaka yöneticisi, özellikler vb. için)
            // TODO: İleride eklenecek
        }
    }
}

