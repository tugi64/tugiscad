package com.tugi.tugiscad.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.tugi.tugiscad.data.model.CADProject
import com.tugi.tugiscad.data.model.Layer

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

    fun addLayer(layer: Layer) {
        currentProject.value?.layers?.add(layer)
    }

    fun setActiveLayer(layer: Layer) {
        activeLayer.value = layer
    }

    fun setActiveTool(tool: DrawingTool) {
        activeTool.value = tool
    }

    fun deleteSelectedObjects() {
        // TODO: Implement delete selected objects
    }

    fun zoomIn() {
        // TODO: Implement zoom in
    }

    fun zoomOut() {
        // TODO: Implement zoom out
    }

    fun resetView() {
        // TODO: Implement reset view
    }

    fun toggleGrid() {
        showGrid.value = !showGrid.value
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

