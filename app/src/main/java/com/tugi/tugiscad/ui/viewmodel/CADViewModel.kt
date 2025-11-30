package com.tugi.tugiscad.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.tugi.tugiscad.data.model.CADObject
import com.tugi.tugiscad.data.model.CADProject
import com.tugi.tugiscad.data.model.Layer
import com.tugi.tugiscad.data.model.LineType

enum class DrawingTool {
    SELECT, LINE, POLYLINE, CIRCLE, RECTANGLE, ARC, POINT, TEXT, ELLIPSE, HATCH, SYMBOL,
    MOVE, COPY, ROTATE, SCALE, MIRROR, TRIM, EXTEND, OFFSET
}

enum class SnapMode {
    NONE, GRID, ENDPOINT, MIDPOINT, CENTER, INTERSECTION, PERPENDICULAR, TANGENT, NEAREST
}

class CADViewModel : ViewModel() {
    val currentProject = mutableStateOf<CADProject?>(null)
    val activeLayer = mutableStateOf<Layer?>(null)
    val activeTool = mutableStateOf(DrawingTool.SELECT)
    val showGrid = mutableStateOf(true)
    val snapMode = mutableStateOf(SnapMode.NONE)
    val zoomLevel = mutableStateOf(1.0)
    // Canvas'ı merkeze almak için başlangıç değerleri
    val panOffsetX = mutableStateOf(400f) // Ekranın ortasına yakın
    val panOffsetY = mutableStateOf(400f)
    val activeLineType = mutableStateOf(LineType.CONTINUOUS)
    val activeColor = mutableStateOf(Color.White) // Siyah zemin için beyaz çizgi

    // Mouse koordinatları (world coordinates)
    val currentMouseX = mutableStateOf(0.0)
    val currentMouseY = mutableStateOf(0.0)

    fun addLayer(layer: Layer) {
        currentProject.value?.let { project ->
            project.layers.add(layer)
            // State'i tetiklemek için yeni referans oluştur
            currentProject.value = project.copy(
                layers = project.layers.toMutableList()
            )
        }
    }

    fun setActiveLayer(layer: Layer) {
        activeLayer.value = layer
    }

    fun setActiveTool(tool: DrawingTool) {
        activeTool.value = tool
    }

    fun setSnapMode(mode: SnapMode) {
        snapMode.value = mode
        println("TugisCAD: Snap modu değişti: $mode")
    }

    fun addObject(obj: CADObject) {
        currentProject.value?.let { project ->
            project.objects.add(obj)
            // State'i tetiklemek için yeni referans oluştur
            currentProject.value = project.copy(
                objects = project.objects.toMutableList()
            )
            println("TugisCAD: Obje eklendi. Toplam: ${project.objects.size}")
        }
    }

    fun pan(deltaX: Float, deltaY: Float) {
        panOffsetX.value += deltaX
        panOffsetY.value += deltaY
    }

    fun deleteSelectedObjects() {
        // TODO: Implement delete selected objects
    }

    fun zoomIn() {
        zoomLevel.value *= 1.2
    }

    fun zoomOut() {
        zoomLevel.value /= 1.2
    }

    fun resetView() {
        zoomLevel.value = 1.0
        panOffsetX.value = 400f
        panOffsetY.value = 400f
    }

    fun toggleGrid() {
        showGrid.value = !showGrid.value
    }

    fun setSnapMode(mode: SnapMode) {
        snapMode.value = mode
    }

    fun createNewProject(name: String, scale: Double, unit: com.tugi.tugiscad.data.model.MeasureUnit) {
        val project = com.tugi.tugiscad.data.model.CADProject(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            scale = scale,
            unit = unit
        )
        // Varsayılan tabaka ekle
        val defaultLayer = com.tugi.tugiscad.data.model.Layer(
            id = java.util.UUID.randomUUID().toString(),
            name = "Tabaka 0",
            color = androidx.compose.ui.graphics.Color.White,
            lineType = com.tugi.tugiscad.data.model.LineType.CONTINUOUS,
            isActive = true
        )
        project.layers.add(defaultLayer)
        currentProject.value = project
        activeLayer.value = defaultLayer
    }

    fun updateProject(project: CADProject) {
        currentProject.value = project
    }
}

