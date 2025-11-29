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
import com.tugi.tugiscad.ui.viewmodel.DrawingTool
import com.tugi.tugiscad.ui.drawing.DrawingHelper
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
    val activeTool by viewModel.activeTool

    // Çizim için geçici state'ler
    var drawingStartPoint by remember { mutableStateOf<Offset?>(null) }
    var drawingPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var currentMousePosition by remember { mutableStateOf<Offset?>(null) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(activeTool) {
                detectTapGestures { offset ->
                    when (activeTool) {
                        DrawingTool.LINE, DrawingTool.RECTANGLE, DrawingTool.CIRCLE, DrawingTool.ARC, DrawingTool.ELLIPSE -> {
                            if (drawingStartPoint == null) {
                                // İlk tıklama - başlangıç noktası
                                drawingStartPoint = offset
                            } else {
                                // İkinci tıklama - çizimi tamamla
                                finishDrawing(
                                    viewModel = viewModel,
                                    startPoint = drawingStartPoint,
                                    points = drawingPoints,
                                    currentPoint = offset
                                )
                                drawingStartPoint = null
                                drawingPoints = emptyList()
                            }
                        }
                        DrawingTool.POINT -> {
                            // Nokta oluştur ve hemen ekle
                            viewModel.activeLayer.value?.let { layer ->
                                val point = DrawingHelper.createPoint(
                                    position = offset,
                                    layer = layer,
                                    lineType = viewModel.activeLineType.value,
                                    color = viewModel.activeColor.value
                                )
                                viewModel.addObject(point)
                            }
                        }
                        DrawingTool.POLYLINE -> {
                            // Her tıklamada nokta ekle
                            drawingPoints = drawingPoints + offset
                        }
                        else -> {}
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Drag başladığında mouse position'ı güncelle
                        if (drawingStartPoint != null) {
                            currentMousePosition = offset
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        when (activeTool) {
                            DrawingTool.SELECT -> {
                                // Pan (kaydırma) işlemi
                                viewModel.pan(dragAmount.x, dragAmount.y)
                            }
                            else -> {
                                // Diğer araçlar için fare pozisyonunu güncelle
                                currentMousePosition = change.position
                            }
                        }
                    }
                )
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

        // Çizim önizlemesi
        drawingStartPoint?.let { start ->
            currentMousePosition?.let { current ->
                when (activeTool) {
                    DrawingTool.LINE -> {
                        drawLine(
                            color = Color.Yellow.copy(alpha = 0.7f),
                            start = start,
                            end = current,
                            strokeWidth = 2f
                        )
                    }
                    DrawingTool.RECTANGLE -> {
                        val width = current.x - start.x
                        val height = current.y - start.y
                        drawRect(
                            color = Color.Yellow.copy(alpha = 0.7f),
                            topLeft = start,
                            size = androidx.compose.ui.geometry.Size(width, height),
                            style = Stroke(width = 2f)
                        )
                    }
                    DrawingTool.CIRCLE -> {
                        val radius = kotlin.math.sqrt(
                            ((current.x - start.x) * (current.x - start.x) +
                            (current.y - start.y) * (current.y - start.y)).toDouble()
                        ).toFloat()
                        drawCircle(
                            color = Color.Yellow.copy(alpha = 0.7f),
                            radius = radius,
                            center = start,
                            style = Stroke(width = 2f)
                        )
                    }
                    DrawingTool.ARC -> {
                        val radius = kotlin.math.sqrt(
                            ((current.x - start.x) * (current.x - start.x) +
                            (current.y - start.y) * (current.y - start.y)).toDouble()
                        ).toFloat()
                        drawCircle(
                            color = Color.Yellow.copy(alpha = 0.5f),
                            radius = radius,
                            center = start,
                            style = Stroke(width = 2f)
                        )
                    }
                    DrawingTool.ELLIPSE -> {
                        val width = kotlin.math.abs(current.x - start.x)
                        val height = kotlin.math.abs(current.y - start.y)
                        drawOval(
                            color = Color.Yellow.copy(alpha = 0.7f),
                            topLeft = Offset(
                                kotlin.math.min(start.x, current.x),
                                kotlin.math.min(start.y, current.y)
                            ),
                            size = androidx.compose.ui.geometry.Size(width, height),
                            style = Stroke(width = 2f)
                        )
                    }
                    else -> {}
                }
            }
        }

        // Polyline önizlemesi
        if (drawingPoints.isNotEmpty() && activeTool == DrawingTool.POLYLINE) {
            val path = Path()
            path.moveTo(drawingPoints.first().x, drawingPoints.first().y)
            drawingPoints.drop(1).forEach { point ->
                path.lineTo(point.x, point.y)
            }
            currentMousePosition?.let {
                path.lineTo(it.x, it.y)
            }
            drawPath(
                path = path,
                color = Color.Yellow.copy(alpha = 0.7f),
                style = Stroke(width = 2f)
            )
        }
    }
}

/**
 * Grid çizimi
 */
private fun DrawScope.drawGrid(zoom: Double, panX: Float, panY: Float) {
    val gridSpacing = (50f * zoom).toFloat()
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
private fun DrawScope.drawPoint(point: CADObject.Point, zoom: Double, panX: Float, panY: Float) {
    val screenX = (point.x * zoom).toFloat() + panX
    val screenY = (point.y * zoom).toFloat() + panY

    drawCircle(
        color = point.color,
        radius = 3f,
        center = Offset(screenX, screenY)
    )
}

/**
 * Çizgi çizimi
 */
private fun DrawScope.drawLine(line: CADObject.Line, zoom: Double, panX: Float, panY: Float) {
    val start = Offset(
        (line.startPoint.x * zoom).toFloat() + panX,
        (line.startPoint.y * zoom).toFloat() + panY
    )
    val end = Offset(
        (line.endPoint.x * zoom).toFloat() + panX,
        (line.endPoint.y * zoom).toFloat() + panY
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
private fun DrawScope.drawPolyline(polyline: CADObject.Polyline, zoom: Double, panX: Float, panY: Float) {
    if (polyline.points.size < 2) return

    val path = Path()
    val firstPoint = polyline.points.first()
    path.moveTo(
        (firstPoint.x * zoom).toFloat() + panX,
        (firstPoint.y * zoom).toFloat() + panY
    )

    polyline.points.drop(1).forEach { point ->
        path.lineTo(
            (point.x * zoom).toFloat() + panX,
            (point.y * zoom).toFloat() + panY
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
private fun DrawScope.drawCircle(circle: CADObject.Circle, zoom: Double, panX: Float, panY: Float) {
    val center = Offset(
        (circle.center.x * zoom).toFloat() + panX,
        (circle.center.y * zoom).toFloat() + panY
    )

    val pathEffect = if (circle.lineType.pattern.isNotEmpty()) {
        PathEffect.dashPathEffect(circle.lineType.pattern.toFloatArray(), 0f)
    } else null

    drawCircle(
        color = circle.color,
        radius = (circle.radius * zoom).toFloat(),
        center = center,
        style = Stroke(width = 2f, pathEffect = pathEffect)
    )
}

/**
 * Yay çizimi
 */
private fun DrawScope.drawArc(arc: CADObject.Arc, zoom: Double, panX: Float, panY: Float) {
    val center = Offset(
        (arc.center.x * zoom).toFloat() + panX,
        (arc.center.y * zoom).toFloat() + panY
    )
    val radius = (arc.radius * zoom).toFloat()

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
private fun DrawScope.drawEllipse(ellipse: CADObject.Ellipse, zoom: Double, panX: Float, panY: Float) {
    val center = Offset(
        (ellipse.center.x * zoom).toFloat() + panX,
        (ellipse.center.y * zoom).toFloat() + panY
    )

    drawOval(
        color = ellipse.color,
        topLeft = Offset(
            center.x - (ellipse.radiusX * zoom).toFloat(),
            center.y - (ellipse.radiusY * zoom).toFloat()
        ),
        size = androidx.compose.ui.geometry.Size(
            (ellipse.radiusX * zoom * 2).toFloat(),
            (ellipse.radiusY * zoom * 2).toFloat()
        ),
        style = Stroke(width = 2f)
    )
}

/**
 * Dikdörtgen çizimi
 */
private fun DrawScope.drawRectangle(rect: CADObject.Rectangle, zoom: Double, panX: Float, panY: Float) {
    val topLeft = Offset(
        (rect.topLeft.x * zoom).toFloat() + panX,
        (rect.topLeft.y * zoom).toFloat() + panY
    )
    val bottomRight = Offset(
        (rect.bottomRight.x * zoom).toFloat() + panX,
        (rect.bottomRight.y * zoom).toFloat() + panY
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
private fun DrawScope.drawText(text: CADObject.Text, zoom: Double, panX: Float, panY: Float) {
    // TODO: Metin çizimi için TextPaint kullanılacak
}

/**
 * Tarama çizimi
 */
private fun DrawScope.drawHatch(hatch: CADObject.Hatch, zoom: Double, panX: Float, panY: Float) {
    // TODO: Tarama deseni çizimi
}

/**
 * Sembol çizimi
 */
private fun DrawScope.drawSymbol(symbol: CADObject.Symbol, zoom: Double, panX: Float, panY: Float) {
    // TODO: Sembol çizimi
}


/**
 * Çizimi tamamla
 */
private fun finishDrawing(
    viewModel: CADViewModel,
    startPoint: Offset?,
    points: List<Offset>,
    currentPoint: Offset
) {
    viewModel.activeLayer.value?.let { layer ->
        val lineType = viewModel.activeLineType.value
        val color = viewModel.activeColor.value

        when (viewModel.activeTool.value) {
            DrawingTool.LINE -> {
                startPoint?.let { start ->
                    val line = DrawingHelper.createLine(
                        start = start,
                        end = currentPoint,
                        layer = layer,
                        lineType = lineType,
                        color = color
                    )
                    viewModel.addObject(line)
                }
            }
            DrawingTool.RECTANGLE -> {
                startPoint?.let { start ->
                    val rectangle = DrawingHelper.createRectangle(
                        topLeft = start,
                        bottomRight = currentPoint,
                        layer = layer,
                        lineType = lineType,
                        color = color
                    )
                    viewModel.addObject(rectangle)
                }
            }
            DrawingTool.CIRCLE -> {
                startPoint?.let { start ->
                    val radius = DrawingHelper.calculateDistance(start, currentPoint)
                    val circle = DrawingHelper.createCircle(
                        center = start,
                        radius = radius,
                        layer = layer,
                        lineType = lineType,
                        color = color
                    )
                    viewModel.addObject(circle)
                }
            }
            DrawingTool.ARC -> {
                startPoint?.let { start ->
                    val radius = DrawingHelper.calculateDistance(start, currentPoint)
                    val startAngle = 0.0
                    val endAngle = 90.0 // Varsayılan 90 derece yay
                    val arc = DrawingHelper.createArc(
                        center = start,
                        radius = radius,
                        startAngle = startAngle,
                        endAngle = endAngle,
                        layer = layer,
                        lineType = lineType,
                        color = color
                    )
                    viewModel.addObject(arc)
                }
            }
            DrawingTool.ELLIPSE -> {
                startPoint?.let { start ->
                    val radiusX = kotlin.math.abs(currentPoint.x - start.x).toDouble()
                    val radiusY = kotlin.math.abs(currentPoint.y - start.y).toDouble()
                    val ellipse = DrawingHelper.createEllipse(
                        center = start,
                        radiusX = radiusX,
                        radiusY = radiusY,
                        rotation = 0.0,
                        layer = layer,
                        lineType = lineType,
                        color = color
                    )
                    viewModel.addObject(ellipse)
                }
            }
            DrawingTool.POLYLINE -> {
                if (points.size >= 2) {
                    val polyline = DrawingHelper.createPolyline(
                        points = points,
                        isClosed = false,
                        layer = layer,
                        lineType = lineType,
                        color = color
                    )
                    viewModel.addObject(polyline)
                } else {
                    // Polyline için en az 2 nokta gerekir
                }
            }
            else -> {}
        }
    }
}

