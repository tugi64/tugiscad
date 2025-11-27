package com.tugi.tugiscad.data.model
}
    SOLID, LINE, CROSS, DOTS, DIAGONAL
enum class HatchPattern {
// Tarama deseni

}
    LEFT, CENTER, RIGHT, TOP, MIDDLE, BOTTOM
enum class TextAlignment {
// Metin hizalama

}
    ) : CADObject()
        val scale: Double = 1.0
        val angle: Double = 0.0,
        val pattern: HatchPattern,
        val boundary: List<Point>,
        override val color: Color,
        override val lineType: LineType,
        override val layer: Layer,
        override val id: String,
    data class Hatch(
    // Tarama (Hatch)

    ) : CADObject()
        val rotation: Double = 0.0
        val scale: Double = 1.0,
        val symbolName: String,
        val position: Point,
        override val color: Color,
        override val lineType: LineType,
        override val layer: Layer,
        override val id: String,
    data class Symbol(
    // Sembol

    ) : CADObject()
        val alignment: TextAlignment = TextAlignment.LEFT
        val rotation: Double = 0.0,
        val fontName: String = "Arial",
        val fontSize: Float,
        val content: String,
        val position: Point,
        override val color: Color,
        override val lineType: LineType,
        override val layer: Layer,
        override val id: String,
    data class Text(
    // Metin

    ) : CADObject()
        val bottomRight: Point
        val topLeft: Point,
        override val color: Color,
        override val lineType: LineType,
        override val layer: Layer,
        override val id: String,
    data class Rectangle(
    // Dikdörtgen

    ) : CADObject()
        val rotation: Double = 0.0
        val radiusY: Double,
        val radiusX: Double,
        val center: Point,
        override val color: Color,
        override val lineType: LineType,
        override val layer: Layer,
        override val id: String,
    data class Ellipse(
    // Elips

    ) : CADObject()
        val endAngle: Double
        val startAngle: Double,
        val radius: Double,
        val center: Point,
        override val color: Color,
        override val lineType: LineType,
        override val layer: Layer,
        override val id: String,
    data class Arc(
    // Yay (Arc)

    ) : CADObject()
        val radius: Double
        val center: Point,
        override val color: Color,
        override val lineType: LineType,
        override val layer: Layer,
        override val id: String,
    data class Circle(
    // Daire

    ) : CADObject()
        val endPoint: Point
        val startPoint: Point,
        override val color: Color,
        override val lineType: LineType,
        override val layer: Layer,
        override val id: String,
    data class Line(
    // Çizgi

    ) : CADObject()
        val isClosed: Boolean = false
        val points: List<Point>,
        override val color: Color,
        override val lineType: LineType,
        override val layer: Layer,
        override val id: String,
    data class Polyline(
    // Çoklu Çizgi (Polyline)

    ) : CADObject()
        val label: String? = null
        val z: Double = 0.0,
        val y: Double,
        val x: Double,
        override val color: Color,
        override val lineType: LineType,
        override val layer: Layer,
        override val id: String,
    data class Point(
    // Nokta

    abstract val color: Color
    abstract val lineType: LineType
    abstract val layer: Layer
    abstract val id: String
sealed class CADObject {
 */
 * CAD Çizim Objeleri - TugisCAD obje yapısı
/**

import androidx.compose.ui.graphics.Color



