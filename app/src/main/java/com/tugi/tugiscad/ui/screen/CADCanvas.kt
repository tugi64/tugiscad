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
import androidx.compose.ui.input.pointer.*
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

    // Klavye olaylarını dinle
    DisposableEffect(Unit) {
        val keyEventHandler = android.view.View.OnKeyListener { _, keyCode, event ->
            if (event.action == android.view.KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    // ESC - İşlemi iptal et
                    android.view.KeyEvent.KEYCODE_ESCAPE -> {
                        if (activeTool == DrawingTool.LINE && drawingStartPoint != null) {
                            // LINE modunda sürekli çizimden çık
                            drawingStartPoint = null
                            println("TugisCAD: ESC - LINE çizimi iptal edildi")
                        } else {
                            // Diğer modlarda çizimi iptal et
                            drawingStartPoint = null
                            drawingPoints = emptyList()
                            println("TugisCAD: ESC - Çizim iptal edildi")
                        }
                        true
                    }
                    // F4 - ENDPOINT snap
                    android.view.KeyEvent.KEYCODE_F4 -> {
                        viewModel.snapMode.value = com.tugi.tugiscad.ui.viewmodel.SnapMode.ENDPOINT
                        true
                    }
                    // F5 - MIDPOINT snap
                    android.view.KeyEvent.KEYCODE_F5 -> {
                        viewModel.snapMode.value = com.tugi.tugiscad.ui.viewmodel.SnapMode.MIDPOINT
                        true
                    }
                    // F6 - INTERSECTION snap
                    android.view.KeyEvent.KEYCODE_F6 -> {
                        viewModel.snapMode.value = com.tugi.tugiscad.ui.viewmodel.SnapMode.INTERSECTION
                        true
                    }
                    // F7 - CENTER snap
                    android.view.KeyEvent.KEYCODE_F7 -> {
                        viewModel.snapMode.value = com.tugi.tugiscad.ui.viewmodel.SnapMode.CENTER
                        true
                    }
                    // F9 - Grid snap
                    android.view.KeyEvent.KEYCODE_F9 -> {
                        viewModel.snapMode.value = com.tugi.tugiscad.ui.viewmodel.SnapMode.GRID
                        true
                    }
                    // + Zoom in
                    android.view.KeyEvent.KEYCODE_PLUS, android.view.KeyEvent.KEYCODE_EQUALS -> {
                        viewModel.zoomIn()
                        true
                    }
                    // - Zoom out
                    android.view.KeyEvent.KEYCODE_MINUS -> {
                        viewModel.zoomOut()
                        true
                    }
                    else -> false
                }
            } else false
        }
        onDispose { }
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

    // Snap fonksiyonu - En yakın nokta/merkez/orta noktayı bul
    fun snapToPoint(worldPos: Offset, snapMode: com.tugi.tugiscad.ui.viewmodel.SnapMode): Offset {
        if (snapMode == com.tugi.tugiscad.ui.viewmodel.SnapMode.NONE) {
            return worldPos // Snap kapalıysa direkt pozisyonu döndür
        }

        val snapRadius = 15.0 / zoom // Piksel cinsinden snap mesafesi
        var closestPoint: Offset? = null
        var minDistance = snapRadius

        project?.objects?.forEach { obj ->
            when (obj) {
                is CADObject.Point -> {
                    if (snapMode == com.tugi.tugiscad.ui.viewmodel.SnapMode.ENDPOINT) {
                        val dist = kotlin.math.sqrt(
                            ((worldPos.x - obj.x) * (worldPos.x - obj.x) +
                             (worldPos.y - obj.y) * (worldPos.y - obj.y)).toDouble()
                        )
                        if (dist < minDistance) {
                            minDistance = dist
                            closestPoint = Offset(obj.x.toFloat(), obj.y.toFloat())
                        }
                    }
                }
                is CADObject.Line -> {
                    // Endpoint snap - çizginin uç noktaları
                    if (snapMode == com.tugi.tugiscad.ui.viewmodel.SnapMode.ENDPOINT) {
                        val startDist = kotlin.math.sqrt(
                            ((worldPos.x - obj.startPoint.x) * (worldPos.x - obj.startPoint.x) +
                             (worldPos.y - obj.startPoint.y) * (worldPos.y - obj.startPoint.y)).toDouble()
                        )
                        if (startDist < minDistance) {
                            minDistance = startDist
                            closestPoint = Offset(obj.startPoint.x.toFloat(), obj.startPoint.y.toFloat())
                        }

                        val endDist = kotlin.math.sqrt(
                            ((worldPos.x - obj.endPoint.x) * (worldPos.x - obj.endPoint.x) +
                             (worldPos.y - obj.endPoint.y) * (worldPos.y - obj.endPoint.y)).toDouble()
                        )
                        if (endDist < minDistance) {
                            minDistance = endDist
                            closestPoint = Offset(obj.endPoint.x.toFloat(), obj.endPoint.y.toFloat())
                        }
                    }
                    // Midpoint snap - çizginin orta noktası
                    else if (snapMode == com.tugi.tugiscad.ui.viewmodel.SnapMode.MIDPOINT) {
                        val midX = (obj.startPoint.x + obj.endPoint.x) / 2.0
                        val midY = (obj.startPoint.y + obj.endPoint.y) / 2.0
                        val midDist = kotlin.math.sqrt(
                            ((worldPos.x - midX) * (worldPos.x - midX) +
                             (worldPos.y - midY) * (worldPos.y - midY)).toDouble()
                        )
                        if (midDist < minDistance) {
                            minDistance = midDist
                            closestPoint = Offset(midX.toFloat(), midY.toFloat())
                        }
                    }
                    // Intersection snap - başka bir çizgiyle kesişim
                    else if (snapMode == com.tugi.tugiscad.ui.viewmodel.SnapMode.INTERSECTION) {
                        // Diğer çizgilerle kesişim kontrol et
                        project?.objects?.forEach { otherObj ->
                            if (otherObj is CADObject.Line && otherObj.id != obj.id) {
                                // İki çizginin kesişim noktasını bul
                                val intersection = findLineIntersection(obj, otherObj)
                                intersection?.let { intersectPt ->
                                    val dist = kotlin.math.sqrt(
                                        ((worldPos.x - intersectPt.x) * (worldPos.x - intersectPt.x) +
                                         (worldPos.y - intersectPt.y) * (worldPos.y - intersectPt.y)).toDouble()
                                    )
                                    if (dist < minDistance) {
                                        minDistance = dist
                                        closestPoint = intersectPt
                                    }
                                }
                            }
                        }
                    }
                }
                is CADObject.Circle -> {
                    // Center snap - dairenin merkezi
                    if (snapMode == com.tugi.tugiscad.ui.viewmodel.SnapMode.CENTER) {
                        val centerDist = kotlin.math.sqrt(
                            ((worldPos.x - obj.center.x) * (worldPos.x - obj.center.x) +
                             (worldPos.y - obj.center.y) * (worldPos.y - obj.center.y)).toDouble()
                        )
                        if (centerDist < minDistance) {
                            minDistance = centerDist
                            closestPoint = Offset(obj.center.x.toFloat(), obj.center.y.toFloat())
                        }
                    }
                }
                is CADObject.Arc -> {
                    if (snapMode == com.tugi.tugiscad.ui.viewmodel.SnapMode.CENTER) {
                        val centerDist = kotlin.math.sqrt(
                            ((worldPos.x - obj.center.x) * (worldPos.x - obj.center.x) +
                             (worldPos.y - obj.center.y) * (worldPos.y - obj.center.y)).toDouble()
                        )
                        if (centerDist < minDistance) {
                            minDistance = centerDist
                            closestPoint = Offset(obj.center.x.toFloat(), obj.center.y.toFloat())
                        }
                    }
                }
                else -> {}
            }
        }

        return closestPoint ?: worldPos
    }


    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(activeTool) {
                // Mouse pozisyonunu sürekli takip et
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        event.changes.firstOrNull()?.let { change ->
                            val worldPos = screenToWorld(change.position)
                            // ViewModel'e koordinatları kaydet
                            viewModel.currentMouseX.value = worldPos.x.toDouble()
                            viewModel.currentMouseY.value = worldPos.y.toDouble()

                            if (drawingStartPoint != null || activeTool == DrawingTool.POLYLINE) {
                                currentMousePosition = worldPos
                            }
                        }
                    }
                }
            }
            .pointerInput(activeTool) {
                // Sol tıklama için tap gesture
                detectTapGestures { offset ->
                    val worldOffset = screenToWorld(offset)
                    // Snap uygula
                    val snappedOffset = snapToPoint(worldOffset, viewModel.snapMode.value)

                    when (activeTool) {
                        DrawingTool.LINE -> {
                            if (drawingStartPoint == null) {
                                // İlk tıklama - başlangıç noktası
                                drawingStartPoint = snappedOffset
                                println("TugisCAD: LINE - Başlangıç: $snappedOffset (Snap: ${viewModel.snapMode.value})")
                            } else {
                                // İkinci tıklama - çizgi çiz VE son nokta yeni başlangıç olsun
                                println("TugisCAD: LINE - Segment: ${drawingStartPoint} -> $snappedOffset")
                                finishDrawing(
                                    viewModel = viewModel,
                                    startPoint = drawingStartPoint,
                                    points = emptyList(),
                                    currentPoint = snappedOffset
                                )
                                // ÖNEMLİ: Son nokta yeni başlangıç (sürekli çizim için)
                                drawingStartPoint = snappedOffset
                                println("TugisCAD: LINE - Yeni başlangıç: $snappedOffset")
                            }
                        }
                        DrawingTool.RECTANGLE, DrawingTool.CIRCLE, DrawingTool.ARC, DrawingTool.ELLIPSE -> {
                            if (drawingStartPoint == null) {
                                drawingStartPoint = snappedOffset
                                println("TugisCAD: ${activeTool.name} - Başlangıç: $snappedOffset")
                            } else {
                                println("TugisCAD: ${activeTool.name} - Bitiş: $snappedOffset")
                                finishDrawing(
                                    viewModel = viewModel,
                                    startPoint = drawingStartPoint,
                                    points = drawingPoints,
                                    currentPoint = snappedOffset
                                )
                                // Bu şekiller için başlangıç null (tek şekil çizimi)
                                drawingStartPoint = null
                                drawingPoints = emptyList()
                            }
                        }
                        DrawingTool.POINT -> {
                            viewModel.activeLayer.value?.let { layer ->
                                val point = DrawingHelper.createPoint(
                                    position = snappedOffset,
                                    layer = layer,
                                    lineType = viewModel.activeLineType.value,
                                    color = viewModel.activeColor.value
                                )
                                viewModel.addObject(point)
                            }
                        }
                        DrawingTool.POLYLINE -> {
                            drawingPoints = drawingPoints + snappedOffset
                            if (drawingPoints.size >= 3) {
                                val first = drawingPoints.first()
                                val distance = kotlin.math.sqrt(
                                    ((snappedOffset.x - first.x) * (snappedOffset.x - first.x) +
                                     (snappedOffset.y - first.y) * (snappedOffset.y - first.y)).toDouble()
                                )
                                if (distance < 20) {
                                    finishDrawing(
                                        viewModel = viewModel,
                                        startPoint = null,
                                        points = drawingPoints,
                                        currentPoint = snappedOffset
                                    )
                                    drawingPoints = emptyList()
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
            .pointerInput(Unit) {
                // Pan için drag gesture (sadece SELECT modunda)
                detectDragGestures { change, dragAmount ->
                    if (activeTool == DrawingTool.SELECT) {
                        change.consume()
                        viewModel.pan(dragAmount.x, dragAmount.y)
                    }
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

        // Snap göstergesi - Yakınında snap noktası varsa göster
        if (viewModel.snapMode.value != com.tugi.tugiscad.ui.viewmodel.SnapMode.NONE) {
            currentMousePosition?.let { mousePos ->
                val snappedPos = snapToPoint(mousePos, viewModel.snapMode.value)
                if (snappedPos != mousePos) {
                    // Snap noktası bulundu, kırmızı daire göster
                    val screenPos = worldToScreen(snappedPos)
                    drawCircle(
                        color = Color.Red,
                        radius = 6f,
                        center = screenPos,
                        style = Stroke(width = 2f)
                    )
                    // Snap tipi göstergesi (küçük kare)
                    drawRect(
                        color = Color.Red,
                        topLeft = Offset(screenPos.x - 3f, screenPos.y - 3f),
                        size = androidx.compose.ui.geometry.Size(6f, 6f)
                    )
                }
            }
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

        println("TugisCAD: finishDrawing - Tool: ${viewModel.activeTool.value}, Start: $startPoint, End: $currentPoint")

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
                    println("TugisCAD: LINE oluşturuldu - Start:(${start.x},${start.y}) End:(${currentPoint.x},${currentPoint.y})")
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
                    println("TugisCAD: CIRCLE oluşturuldu - Center:(${start.x},${start.y}) Radius:$radius")
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

/**
 * İki çizginin kesişim noktasını bul
 */
private fun findLineIntersection(line1: CADObject.Line, line2: CADObject.Line): Offset? {
    val x1 = line1.startPoint.x
    val y1 = line1.startPoint.y
    val x2 = line1.endPoint.x
    val y2 = line1.endPoint.y

    val x3 = line2.startPoint.x
    val y3 = line2.startPoint.y
    val x4 = line2.endPoint.x
    val y4 = line2.endPoint.y

    val denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
    if (kotlin.math.abs(denom) < 0.001) return null // Paralel çizgiler

    val t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denom
    val u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denom

    // Kesişim çizgi segment sınırları içinde mi kontrol et
    if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
        val intersectX = x1 + t * (x2 - x1)
        val intersectY = y1 + t * (y2 - y1)
        return Offset(intersectX.toFloat(), intersectY.toFloat())
    }

    return null
}
