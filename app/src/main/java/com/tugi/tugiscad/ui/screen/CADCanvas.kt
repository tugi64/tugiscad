package com.tugi.tugiscad.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.awaitPointerEventScope
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

    // Obje sayısını reactive hale getir
    val objectCount = project?.objects?.size ?: 0

    // Debug: Obje sayısını konsola yazdır
    LaunchedEffect(objectCount) {
        println("TugisCAD: Canvas'ta ${objectCount} obje var")
    }

    // Çizim için geçici state'ler
    var drawingStartPoint by remember { mutableStateOf<Offset?>(null) }
    var drawingPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var currentMousePosition by remember { mutableStateOf<Offset?>(null) }

    // Escape tuşu ile polyline'ı bitir
    LaunchedEffect(activeTool) {
        // Araç değiştiğinde çizimi temizle
        if (activeTool != DrawingTool.POLYLINE) {
            if (drawingPoints.size >= 2) {
                // Polyline'dan çıkıyoruz, son çizimi kaydet
                finishDrawing(
                    viewModel = viewModel,
                    startPoint = null,
                    points = drawingPoints,
                    currentPoint = drawingPoints.last()
                )
            }
            drawingPoints = emptyList()
            drawingStartPoint = null
        }
    }

    // Koordinat dönüşüm fonksiyonları
    fun screenToWorld(screenPos: Offset): Offset {
        return Offset(
            (screenPos.x - panX) / zoom.toFloat(),
            (screenPos.y - panY) / zoom.toFloat()
        )
    }

    fun worldToScreen(worldPos: Offset): Offset {
        return Offset(
            worldPos.x * zoom.toFloat() + panX,
            worldPos.y * zoom.toFloat() + panY
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(activeTool) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        event.changes.forEach { change ->
                            // Mouse pozisyonunu sürekli güncelle
                            currentMousePosition = screenToWorld(change.position)

                            when (change.pressed) {
                                true -> {
                                    // Mouse'un hangi tuşuna basıldığını kontrol et
                                    val isRightClick = change.id.value > 0 // Basit sağ tık kontrolü
                                    val worldOffset = screenToWorld(change.position)

                                    if (isRightClick || change.previousPressed != change.pressed) {
                                        // Sağ tıklama - Çizimi bitir
                                        if (activeTool == DrawingTool.LINE && drawingStartPoint != null) {
                                            // LINE için son çizgiyi çiz ve bitir
                                            finishDrawing(
                                                viewModel = viewModel,
                                                startPoint = drawingStartPoint,
                                                points = drawingPoints,
                                                currentPoint = worldOffset
                                            )
                                            drawingStartPoint = null
                                            drawingPoints = emptyList()
                                            println("TugisCAD: Sağ tık - Çizim tamamlandı")
                                        } else if (activeTool == DrawingTool.POLYLINE && drawingPoints.isNotEmpty()) {
                                            // Polyline'ı bitir
                                            finishDrawing(
                                                viewModel = viewModel,
                                                startPoint = null,
                                                points = drawingPoints,
                                                currentPoint = worldOffset
                                            )
                                            drawingPoints = emptyList()
                                        }
                                        change.consume()
                                        continue
                                    }

                                    // Sol tıklama
                                    when (activeTool) {
                                        DrawingTool.LINE -> {
                                            if (drawingStartPoint == null) {
                                                // İlk tıklama - başlangıç noktası
                                                drawingStartPoint = worldOffset
                                                println("TugisCAD: LINE - Başlangıç: $worldOffset")
                                            } else {
                                                // Sonraki tıklamalar - çizgi çiz ve devam et
                                                println("TugisCAD: LINE - Segment: ${drawingStartPoint} -> $worldOffset")
                                                finishDrawing(
                                                    viewModel = viewModel,
                                                    startPoint = drawingStartPoint,
                                                    points = emptyList(),
                                                    currentPoint = worldOffset
                                                )
                                                // Yeni çizgi için bu nokta başlangıç olsun
                                                drawingStartPoint = worldOffset
                                            }
                                        }
                                        DrawingTool.RECTANGLE, DrawingTool.CIRCLE, DrawingTool.ARC, DrawingTool.ELLIPSE -> {
                                            if (drawingStartPoint == null) {
                                                drawingStartPoint = worldOffset
                                                println("TugisCAD: ${activeTool.name} - Başlangıç: $worldOffset")
                                            } else {
                                                println("TugisCAD: ${activeTool.name} - Bitiş: $worldOffset")
                                                finishDrawing(
                                                    viewModel = viewModel,
                                                    startPoint = drawingStartPoint,
                                                    points = drawingPoints,
                                                    currentPoint = worldOffset
                                                )
                                                drawingStartPoint = null
                                                drawingPoints = emptyList()
                                            }
                                        }
                                        DrawingTool.POINT -> {
                                            viewModel.activeLayer.value?.let { layer ->
                                                val point = DrawingHelper.createPoint(
                                                    position = worldOffset,
                                                    layer = layer,
                                                    lineType = viewModel.activeLineType.value,
                                                    color = viewModel.activeColor.value
                                                )
                                                viewModel.addObject(point)
                                            }
                                        }
                                        DrawingTool.POLYLINE -> {
                                            drawingPoints = drawingPoints + worldOffset
                                            if (drawingPoints.size >= 3) {
                                                val first = drawingPoints.first()
                                                val distance = kotlin.math.sqrt(
                                                    ((worldOffset.x - first.x) * (worldOffset.x - first.x) +
                                                     (worldOffset.y - first.y) * (worldOffset.y - first.y)).toDouble()
                                                )
                                                if (distance < 20) {
                                                    finishDrawing(
                                                        viewModel = viewModel,
                                                        startPoint = null,
                                                        points = drawingPoints,
                                                        currentPoint = worldOffset
                                                    )
                                                    drawingPoints = emptyList()
                                                }
                                            }
                                        }
                                        else -> {}
                                    }
                                    change.consume()
                                }
                            }
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                // Pan için ayrı bir gesture detector
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        if (activeTool == DrawingTool.SELECT) {
                            change.consume()
                            viewModel.pan(dragAmount.x, dragAmount.y)
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
                // World koordinatlarını screen koordinatlarına çevir
                val startScreen = worldToScreen(start)
                val currentScreen = worldToScreen(current)

                when (activeTool) {
                    DrawingTool.LINE -> {
                        drawLine(
                            color = Color.Yellow.copy(alpha = 0.7f),
                            start = startScreen,
                            end = currentScreen,
                            strokeWidth = 2f
                        )
                    }
                    DrawingTool.RECTANGLE -> {
                        val width = currentScreen.x - startScreen.x
                        val height = currentScreen.y - startScreen.y
                        drawRect(
                            color = Color.Yellow.copy(alpha = 0.7f),
                            topLeft = startScreen,
                            size = androidx.compose.ui.geometry.Size(width, height),
                            style = Stroke(width = 2f)
                        )
                    }
                    DrawingTool.CIRCLE -> {
                        val radius = kotlin.math.sqrt(
                            ((currentScreen.x - startScreen.x) * (currentScreen.x - startScreen.x) +
                            (currentScreen.y - startScreen.y) * (currentScreen.y - startScreen.y)).toDouble()
                        ).toFloat()
                        drawCircle(
                            color = Color.Yellow.copy(alpha = 0.7f),
                            radius = radius,
                            center = startScreen,
                            style = Stroke(width = 2f)
                        )
                    }
                    DrawingTool.ARC -> {
                        val radius = kotlin.math.sqrt(
                            ((currentScreen.x - startScreen.x) * (currentScreen.x - startScreen.x) +
                            (currentScreen.y - startScreen.y) * (currentScreen.y - startScreen.y)).toDouble()
                        ).toFloat()
                        drawCircle(
                            color = Color.Yellow.copy(alpha = 0.5f),
                            radius = radius,
                            center = startScreen,
                            style = Stroke(width = 2f)
                        )
                    }
                    DrawingTool.ELLIPSE -> {
                        val width = kotlin.math.abs(currentScreen.x - startScreen.x)
                        val height = kotlin.math.abs(currentScreen.y - startScreen.y)
                        drawOval(
                            color = Color.Yellow.copy(alpha = 0.7f),
                            topLeft = Offset(
                                kotlin.math.min(startScreen.x, currentScreen.x),
                                kotlin.math.min(startScreen.y, currentScreen.y)
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
            // İlk noktayı world'den screen'e çevir
            val firstScreen = worldToScreen(drawingPoints.first())
            path.moveTo(firstScreen.x, firstScreen.y)

            // Diğer noktaları da çevir
            drawingPoints.drop(1).forEach { point ->
                val screenPoint = worldToScreen(point)
                path.lineTo(screenPoint.x, screenPoint.y)
            }

            // Mevcut mouse pozisyonunu da ekle
            currentMousePosition?.let {
                val currentScreen = worldToScreen(it)
                path.lineTo(currentScreen.x, currentScreen.y)
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

