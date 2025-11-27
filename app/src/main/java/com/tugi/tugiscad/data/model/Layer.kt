package com.tugi.tugiscad.data.model

import androidx.compose.ui.graphics.Color

/**
 * Tabaka (Layer) - TugisCAD tabaka sistemi
 * Her tabaka kendine özgü renk, çizgi tipi ve görünürlük özelliklerine sahiptir
 */
data class Layer(
    val id: String,
    val name: String,
    val color: Color = Color.White,
    val lineType: LineType = LineType.CONTINUOUS,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val isActive: Boolean = false,
    val description: String = ""
)

/**
 * Çizgi Tipi - TugisCAD çizgi tipleri
 */
data class LineType(
    val id: String,
    val name: String,
    val pattern: List<Float> = listOf(), // Boş liste sürekli çizgi anlamına gelir
    val description: String = ""
) {
    companion object {
        val CONTINUOUS = LineType("CONTINUOUS", "Sürekli", listOf())
        val DASHED = LineType("DASHED", "Kesikli", listOf(10f, 5f))
        val DOTTED = LineType("DOTTED", "Noktalı", listOf(2f, 3f))
        val DASH_DOT = LineType("DASH_DOT", "Çizgi-Nokta", listOf(10f, 3f, 2f, 3f))
        val CENTER = LineType("CENTER", "Merkez", listOf(15f, 3f, 3f, 3f))
    }
}


