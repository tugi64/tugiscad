package com.tugi.tugiscad.ui.drawing

import androidx.compose.ui.geometry.Offset
import com.tugi.tugiscad.data.model.CADObject
import com.tugi.tugiscad.data.model.Layer
import com.tugi.tugiscad.data.model.LineType
import androidx.compose.ui.graphics.Color
import java.util.UUID

/**
 * Çizim Yardımcı Fonksiyonları
 */
object DrawingHelper {

    /**
     * İki nokta arasında çizgi oluştur
     */
    fun createLine(
        start: Offset,
        end: Offset,
        layer: Layer,
        lineType: LineType,
        color: Color
    ): CADObject.Line {
        return CADObject.Line(
            id = UUID.randomUUID().toString(),
            layer = layer,
            lineType = lineType,
            color = color,
            startPoint = CADObject.Point(
                id = UUID.randomUUID().toString(),
                layer = layer,
                lineType = lineType,
                color = color,
                x = start.x.toDouble(),
                y = start.y.toDouble()
            ),
            endPoint = CADObject.Point(
                id = UUID.randomUUID().toString(),
                layer = layer,
                lineType = lineType,
                color = color,
                x = end.x.toDouble(),
                y = end.y.toDouble()
            )
        )
    }

    /**
     * Nokta oluştur
     */
    fun createPoint(
        position: Offset,
        layer: Layer,
        lineType: LineType,
        color: Color,
        label: String? = null
    ): CADObject.Point {
        return CADObject.Point(
            id = UUID.randomUUID().toString(),
            layer = layer,
            lineType = lineType,
            color = color,
            x = position.x.toDouble(),
            y = position.y.toDouble(),
            label = label
        )
    }

    /**
     * Daire oluştur
     */
    fun createCircle(
        center: Offset,
        radius: Double,
        layer: Layer,
        lineType: LineType,
        color: Color
    ): CADObject.Circle {
        return CADObject.Circle(
            id = UUID.randomUUID().toString(),
            layer = layer,
            lineType = lineType,
            color = color,
            center = CADObject.Point(
                id = UUID.randomUUID().toString(),
                layer = layer,
                lineType = lineType,
                color = color,
                x = center.x.toDouble(),
                y = center.y.toDouble()
            ),
            radius = radius
        )
    }

    /**
     * Dikdörtgen oluştur
     */
    fun createRectangle(
        topLeft: Offset,
        bottomRight: Offset,
        layer: Layer,
        lineType: LineType,
        color: Color
    ): CADObject.Rectangle {
        return CADObject.Rectangle(
            id = UUID.randomUUID().toString(),
            layer = layer,
            lineType = lineType,
            color = color,
            topLeft = CADObject.Point(
                id = UUID.randomUUID().toString(),
                layer = layer,
                lineType = lineType,
                color = color,
                x = topLeft.x.toDouble(),
                y = topLeft.y.toDouble()
            ),
            bottomRight = CADObject.Point(
                id = UUID.randomUUID().toString(),
                layer = layer,
                lineType = lineType,
                color = color,
                x = bottomRight.x.toDouble(),
                y = bottomRight.y.toDouble()
            )
        )
    }

    /**
     * Çoklu çizgi oluştur
     */
    fun createPolyline(
        points: List<Offset>,
        isClosed: Boolean,
        layer: Layer,
        lineType: LineType,
        color: Color
    ): CADObject.Polyline {
        val cadPoints = points.map { offset ->
            CADObject.Point(
                id = UUID.randomUUID().toString(),
                layer = layer,
                lineType = lineType,
                color = color,
                x = offset.x.toDouble(),
                y = offset.y.toDouble()
            )
        }

        return CADObject.Polyline(
            id = UUID.randomUUID().toString(),
            layer = layer,
            lineType = lineType,
            color = color,
            points = cadPoints,
            isClosed = isClosed
        )
    }

    /**
     * Yay oluştur
     */
    fun createArc(
        center: Offset,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        layer: Layer,
        lineType: LineType,
        color: Color
    ): CADObject.Arc {
        return CADObject.Arc(
            id = UUID.randomUUID().toString(),
            layer = layer,
            lineType = lineType,
            color = color,
            center = CADObject.Point(
                id = UUID.randomUUID().toString(),
                layer = layer,
                lineType = lineType,
                color = color,
                x = center.x.toDouble(),
                y = center.y.toDouble()
            ),
            radius = radius,
            startAngle = startAngle,
            endAngle = endAngle
        )
    }

    /**
     * Elips oluştur
     */
    fun createEllipse(
        center: Offset,
        radiusX: Double,
        radiusY: Double,
        rotation: Double,
        layer: Layer,
        lineType: LineType,
        color: Color
    ): CADObject.Ellipse {
        return CADObject.Ellipse(
            id = UUID.randomUUID().toString(),
            layer = layer,
            lineType = lineType,
            color = color,
            center = CADObject.Point(
                id = UUID.randomUUID().toString(),
                layer = layer,
                lineType = lineType,
                color = color,
                x = center.x.toDouble(),
                y = center.y.toDouble()
            ),
            radiusX = radiusX,
            radiusY = radiusY,
            rotation = rotation
        )
    }

    /**
     * Metin oluştur
     */
    fun createText(
        position: Offset,
        content: String,
        fontSize: Float,
        fontName: String,
        layer: Layer,
        lineType: LineType,
        color: Color
    ): CADObject.Text {
        return CADObject.Text(
            id = UUID.randomUUID().toString(),
            layer = layer,
            lineType = lineType,
            color = color,
            position = CADObject.Point(
                id = UUID.randomUUID().toString(),
                layer = layer,
                lineType = lineType,
                color = color,
                x = position.x.toDouble(),
                y = position.y.toDouble()
            ),
            content = content,
            fontSize = fontSize,
            fontName = fontName
        )
    }

    /**
     * İki nokta arası mesafe hesapla
     */
    fun calculateDistance(point1: Offset, point2: Offset): Double {
        val dx = point2.x - point1.x
        val dy = point2.y - point1.y
        return kotlin.math.sqrt((dx * dx + dy * dy).toDouble())
    }

    /**
     * Alan hesapla (polygon için)
     */
    fun calculateArea(points: List<Offset>): Double {
        if (points.size < 3) return 0.0

        var area = 0.0
        for (i in points.indices) {
            val j = (i + 1) % points.size
            area += points[i].x * points[j].y
            area -= points[j].x * points[i].y
        }
        return kotlin.math.abs(area / 2.0)
    }

    /**
     * Açı hesapla (radyan cinsinden)
     */
    fun calculateAngle(center: Offset, point: Offset): Double {
        val dx = point.x - center.x
        val dy = point.y - center.y
        return kotlin.math.atan2(dy.toDouble(), dx.toDouble())
    }
}

