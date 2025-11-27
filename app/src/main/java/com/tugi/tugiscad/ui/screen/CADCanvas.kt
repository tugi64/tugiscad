package com.tugi.tugiscad.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.tugi.tugiscad.data.model.*
import com.tugi.tugiscad.ui.viewmodel.CADViewModel
import kotlin.math.cos
import kotlin.math.sin

/**
 * CAD Canvas - TugisCAD çizim ekranı
 */
@Composable
fun CADCanvas(
    viewModel: CADViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject
    val zoom by viewModel.zoomLevel
    val panX by viewModel.panOffsetX
    val panY by viewModel.panOffsetY
    val showGrid by viewModel.showGrid

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Tıklama olaylarını işle
                    handleCanvasTap(offset, viewModel)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    // Pan (kaydırma) işlemi
                    viewModel.pan(dragAmount.x, dragAmount.y)
                }
            }
    ) {
        // Grid çiz
        if (showGrid) {
            drawGrid(zoom, panX, panY)
        }

        // Tüm objeleri çiz
        project?.objects?.forEach { obj ->
            when (obj) {
                is CADObject.Point -> drawPoint(obj, zoom, panX, panY)
                is CADObject.Line -> drawLine(obj, zoom, panX, panY)
                is CADObject.Polyline -> drawPolyline(obj, zoom, panX, panY)
                is CADObject.Circle -> drawCircle(obj, zoom, panX, panY)
                is CADObject.Arc -> drawArc(obj, zoom, panX, panY)
                is CADObject.Ellipse -> drawEllipse(obj, zoom, panX, panY)
                is CADObject.Rectangle -> drawRectangle(obj, zoom, panX, panY)
                is CADObject.Text -> drawText(obj, zoom, panX, panY)
                is CADObject.Hatch -> drawHatch(obj, zoom, panX, panY)
                is CADObject.Symbol -> drawSymbol(obj, zoom, panX, panY)
            }
        }
    }
}

/**
 * Grid çizimi
 */
private fun DrawScope.drawGrid(zoom: Float, panX: Float, panY: Float) {
    val gridSpacing = 50f * zoom
    val width = size.width
    val height = size.height

    // Dikey çizgiler
    var x = (panX % gridSpacing)
    while (x < width) {
        drawLine(
            color = Color.DarkGray.copy(alpha = 0.3f),
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1f
        )
        x += gridSpacing
    }

    // Yatay çizgiler
    var y = (panY % gridSpacing)
    while (y < height) {
        drawLine(
            color = Color.DarkGray.copy(alpha = 0.3f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
        y += gridSpacing
    }
}

/**
 * Nokta çizimi
 */
private fun DrawScope.drawPoint(point: CADObject.Point, zoom: Float, panX: Float, panY: Float) {
    val screenX = point.x.toFloat() * zoom + panX
    val screenY = point.y.toFloat() * zoom + panY

    drawCircle(
        color = point.color,
        radius = 3f,
        center = Offset(screenX, screenY)
    )
}

/**
 * Çizgi çizimi
 */
private fun DrawScope.drawLine(line: CADObject.Line, zoom: Float, panX: Float, panY: Float) {
    val start = Offset(
        line.startPoint.x.toFloat() * zoom + panX,
        line.startPoint.y.toFloat() * zoom + panY
    )
    val end = Offset(
        line.endPoint.x.toFloat() * zoom + panX,
        line.endPoint.y.toFloat() * zoom + panY
    )

    val pathEffect = if (line.lineType.pattern.isNotEmpty()) {
        PathEffect.dashPathEffect(line.lineType.pattern.toFloatArray(), 0f)
    } else null

    drawLine(
        color = line.color,
        start = start,
        end = end,
        strokeWidth = 2f,
        pathEffect = pathEffect
    )
}

/**
 * Çoklu çizgi çizimi
 */
private fun DrawScope.drawPolyline(polyline: CADObject.Polyline, zoom: Float, panX: Float, panY: Float) {
    if (polyline.points.size < 2) return

    val path = Path()
    val firstPoint = polyline.points.first()
    path.moveTo(
        firstPoint.x.toFloat() * zoom + panX,
        firstPoint.y.toFloat() * zoom + panY
    )

    polyline.points.drop(1).forEach { point ->
        path.lineTo(
            point.x.toFloat() * zoom + panX,
            point.y.toFloat() * zoom + panY
        )
    }

    if (polyline.isClosed) {
        path.close()
    }

    val pathEffect = if (polyline.lineType.pattern.isNotEmpty()) {
        PathEffect.dashPathEffect(polyline.lineType.pattern.toFloatArray(), 0f)
    } else null

    drawPath(
        path = path,
        color = polyline.color,
        style = Stroke(width = 2f, pathEffect = pathEffect)
    )
}

/**
 * Daire çizimi
 */
private fun DrawScope.drawCircle(circle: CADObject.Circle, zoom: Float, panX: Float, panY: Float) {
    val center = Offset(
        circle.center.x.toFloat() * zoom + panX,
        circle.center.y.toFloat() * zoom + panY
    )

    val pathEffect = if (circle.lineType.pattern.isNotEmpty()) {
        PathEffect.dashPathEffect(circle.lineType.pattern.toFloatArray(), 0f)
    } else null

    drawCircle(
        color = circle.color,
        radius = circle.radius.toFloat() * zoom,
        center = center,
        style = Stroke(width = 2f, pathEffect = pathEffect)
    )
}

/**
 * Yay çizimi
 */
private fun DrawScope.drawArc(arc: CADObject.Arc, zoom: Float, panX: Float, panY: Float) {
    val center = Offset(
        arc.center.x.toFloat() * zoom + panX,
        arc.center.y.toFloat() * zoom + panY
    )
    val radius = arc.radius.toFloat() * zoom

    val path = Path()
    val sweepAngle = arc.endAngle - arc.startAngle

    path.addArc(
        oval = androidx.compose.ui.geometry.Rect(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        ),
        startAngleDegrees = arc.startAngle.toFloat(),
        sweepAngleDegrees = sweepAngle.toFloat()
    )

    drawPath(
        path = path,
        color = arc.color,
        style = Stroke(width = 2f)
    )
}

/**
 * Elips çizimi
 */
private fun DrawScope.drawEllipse(ellipse: CADObject.Ellipse, zoom: Float, panX: Float, panY: Float) {
    val center = Offset(
        ellipse.center.x.toFloat() * zoom + panX,
        ellipse.center.y.toFloat() * zoom + panY
    )

    drawOval(
        color = ellipse.color,
        topLeft = Offset(
            center.x - ellipse.radiusX.toFloat() * zoom,
            center.y - ellipse.radiusY.toFloat() * zoom
        ),
        size = androidx.compose.ui.geometry.Size(
            ellipse.radiusX.toFloat() * zoom * 2,
            ellipse.radiusY.toFloat() * zoom * 2
        ),
        style = Stroke(width = 2f)
    )
}

/**
 * Dikdörtgen çizimi
 */
private fun DrawScope.drawRectangle(rect: CADObject.Rectangle, zoom: Float, panX: Float, panY: Float) {
    val topLeft = Offset(
        rect.topLeft.x.toFloat() * zoom + panX,
        rect.topLeft.y.toFloat() * zoom + panY
    )
    val bottomRight = Offset(
        rect.bottomRight.x.toFloat() * zoom + panX,
        rect.bottomRight.y.toFloat() * zoom + panY
    )

    drawRect(
        color = rect.color,
        topLeft = topLeft,
        size = androidx.compose.ui.geometry.Size(
            bottomRight.x - topLeft.x,
            bottomRight.y - topLeft.y
        ),
        style = Stroke(width = 2f)
    )
}

/**
 * Metin çizimi
 */
private fun DrawScope.drawText(text: CADObject.Text, zoom: Float, panX: Float, panY: Float) {
    // TODO: Metin çizimi için TextPaint kullanılacak
}

/**
 * Tarama çizimi
 */
private fun DrawScope.drawHatch(hatch: CADObject.Hatch, zoom: Float, panX: Float, panY: Float) {
    // TODO: Tarama deseni çizimi
}

/**
 * Sembol çizimi
 */
private fun DrawScope.drawSymbol(symbol: CADObject.Symbol, zoom: Float, panX: Float, panY: Float) {
    // TODO: Sembol çizimi
}

/**
 * Canvas tıklama olayını işle
 */
private fun handleCanvasTap(offset: Offset, viewModel: CADViewModel) {
    // TODO: Obje seçimi ve araç bazlı işlemler
}

