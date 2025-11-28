package com.tugi.tugiscad.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * CAD Çizim Objeleri - TugisCAD obje yapısı
 */

sealed class CADObject {
    abstract val id: String
    abstract val layer: Layer
    abstract val color: Color
    abstract val lineType: LineType
}

// Basit nokta
data class Point(
    override val id: String,
    override val layer: Layer,
    override val color: Color = Color.Black,
    override val lineType: LineType = LineType.CONTINUOUS,
    val x: Double,
    val y: Double
) : CADObject()

// Çizgi
data class Line(
    override val id: String,
    override val layer: Layer,
    override val color: Color = Color.Black,
    override val lineType: LineType = LineType.CONTINUOUS,
    val start: Point,
    val end: Point
) : CADObject()

// Çoklu çizgi (polyline)
data class Polyline(
    override val id: String,
    override val layer: Layer,
    override val color: Color = Color.Black,
    override val lineType: LineType = LineType.CONTINUOUS,
    val points: List<Point>,
    val isClosed: Boolean = false
) : CADObject()

// Daire
data class Circle(
    override val id: String,
    override val layer: Layer,
    override val color: Color = Color.Black,
    override val lineType: LineType = LineType.CONTINUOUS,
    val center: Point,
    val radius: Double
) : CADObject()

// Yay (Arc)
data class Arc(
    override val id: String,
    override val layer: Layer,
    override val color: Color = Color.Black,
    override val lineType: LineType = LineType.CONTINUOUS,
    val center: Point,
    val radius: Double,
    val startAngle: Double,
    val sweepAngle: Double
) : CADObject()

// Elips
data class Ellipse(
    override val id: String,
    override val layer: Layer,
    override val color: Color = Color.Black,
    override val lineType: LineType = LineType.CONTINUOUS,
    val center: Point,
    val radiusX: Double,
    val radiusY: Double,
    val rotation: Double = 0.0
) : CADObject()

// Dikdörtgen
data class Rectangle(
    override val id: String,
    override val layer: Layer,
    override val color: Color = Color.Black,
    override val lineType: LineType = LineType.CONTINUOUS,
    val topLeft: Point,
    val bottomRight: Point,
    val rotation: Double = 0.0
) : CADObject()

// Metin
data class TextObject(
    override val id: String,
    override val layer: Layer,
    override val color: Color = Color.Black,
    override val lineType: LineType = LineType.CONTINUOUS,
    val content: String,
    val position: Point,
    val fontSize: Float = 12f,
    val fontName: String = "Default",
    val alignment: TextAlignment = TextAlignment.LEFT
) : CADObject()

// Sembol
data class Symbol(
    override val id: String,
    override val layer: Layer,
    override val color: Color = Color.Black,
    override val lineType: LineType = LineType.CONTINUOUS,
    val symbolName: String,
    val position: Point,
    val rotation: Double = 0.0,
    val scale: Double = 1.0
) : CADObject()

// Tarama (Hatch)
data class Hatch(
    override val id: String,
    override val layer: Layer,
    override val color: Color = Color.Black,
    override val lineType: LineType = LineType.CONTINUOUS,
    val patternName: String,
    val boundary: List<Point>
) : CADObject()

// Yardımcı türler
enum class TextAlignment {
    LEFT, CENTER, RIGHT
}
