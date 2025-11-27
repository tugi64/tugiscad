package com.tugi.tugiscad.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.tugi.tugiscad.data.model.*
import java.util.UUID

/**
 * CAD ViewModel - Tüm CAD işlemlerini yöneten ana ViewModel
 */
class CADViewModel : ViewModel() {

    // Mevcut proje
    var currentProject = mutableStateOf<CADProject?>(null)
        private set

    // Aktif tabaka
    var activeLayer = mutableStateOf<Layer?>(null)
        private set

    // Aktif çizgi tipi
    var activeLineType = mutableStateOf(LineType.CONTINUOUS)
        private set

    // Aktif renk
    var activeColor = mutableStateOf(Color.White)
        private set

    // Seçili objeler
    val selectedObjects = mutableStateListOf<CADObject>()

    // Zoom seviyesi
    var zoomLevel = mutableStateOf(1.0f)
        private set

    // Pan (kaydırma) ofsetleri
    var panOffsetX = mutableStateOf(0f)
        private set
    var panOffsetY = mutableStateOf(0f)
        private set

    // Snap (Yakalama) modu
    var snapMode = mutableStateOf(SnapMode.NONE)
        private set

    // Grid gösterim
    var showGrid = mutableStateOf(true)
        private set

    // Aktif çizim aracı
    var activeTool = mutableStateOf<DrawingTool>(DrawingTool.SELECT)
        private set

    init {
        // Varsayılan proje oluştur
        createNewProject("Yeni Proje", 1000.0, MeasureUnit.METER)
    }

    // Yeni proje oluştur - gelişmiş
    fun createNewProject(name: String, scale: Double = 1000.0, unit: MeasureUnit = MeasureUnit.METER) {
        val project = CADProject(
            id = UUID.randomUUID().toString(),
            name = name,
            scale = scale,
            unit = unit,
            layers = mutableListOf(
                Layer(
                    id = "layer_0",
                    name = "Tabaka 0",
                    color = Color.White,
                    lineType = LineType.CONTINUOUS,
                    isActive = true
                )
            )
        )
        currentProject.value = project
        activeLayer.value = project.layers.first()
        selectedObjects.clear()
        resetView()
    }

    // Proje güncelle
    fun updateProject(project: CADProject) {
        currentProject.value = project
    }

    // Proje yükle
    fun loadProject(project: CADProject) {
        currentProject.value = project
        activeLayer.value = project.layers.firstOrNull { it.isActive } ?: project.layers.firstOrNull()
    }

    // Obje ekle
    fun addObject(obj: CADObject) {
        currentProject.value?.objects?.add(obj)
    }

    // Obje sil
    fun deleteObject(obj: CADObject) {
        currentProject.value?.objects?.remove(obj)
        selectedObjects.remove(obj)
    }

    // Seçili objeleri sil
    fun deleteSelectedObjects() {
        selectedObjects.forEach { obj ->
            currentProject.value?.objects?.remove(obj)
        }
        selectedObjects.clear()
    }

    // Tabaka ekle
    fun addLayer(layer: Layer) {
        currentProject.value?.layers?.add(layer)
    }

    // Aktif tabaka değiştir
    fun setActiveLayer(layer: Layer) {
        currentProject.value?.layers?.forEach { it.copy(isActive = false) }
        activeLayer.value = layer.copy(isActive = true)
    }

    // Zoom işlemleri
    fun zoomIn() {
        zoomLevel.value = (zoomLevel.value * 1.2f).coerceAtMost(10f)
    }

    fun zoomOut() {
        zoomLevel.value = (zoomLevel.value / 1.2f).coerceAtLeast(0.1f)
    }

    fun setZoom(level: Float) {
        zoomLevel.value = level.coerceIn(0.1f, 10f)
    }

    // Pan işlemleri
    fun pan(deltaX: Float, deltaY: Float) {
        panOffsetX.value += deltaX
        panOffsetY.value += deltaY
    }

    fun resetView() {
        zoomLevel.value = 1.0f
        panOffsetX.value = 0f
        panOffsetY.value = 0f
    }

    // Snap modu değiştir
    fun setSnapMode(mode: SnapMode) {
        snapMode.value = mode
    }

    // Grid gösterim değiştir
    fun toggleGrid() {
        showGrid.value = !showGrid.value
    }

    // Aktif araç değiştir
    fun setActiveTool(tool: DrawingTool) {
        activeTool.value = tool
    }

    // Obje seç/seçimi kaldır
    fun toggleObjectSelection(obj: CADObject) {
        if (selectedObjects.contains(obj)) {
            selectedObjects.remove(obj)
        } else {
            selectedObjects.add(obj)
        }
    }

    fun clearSelection() {
        selectedObjects.clear()
    }
}

/**
 * Yakalama Modları - TugisCAD snap modları
 */
enum class SnapMode {
    NONE,           // Yakalama yok
    GRID,           // Grid noktalarına yakala
    ENDPOINT,       // Uç noktalara yakala
    MIDPOINT,       // Orta noktalara yakala
    CENTER,         // Merkez noktalarına yakala
    INTERSECTION,   // Kesişim noktalarına yakala
    PERPENDICULAR,  // Dik noktaya yakala
    TANGENT,        // Teğet noktaya yakala
    NEAREST         // En yakın noktaya yakala
}

/**
 * Çizim Araçları - TugisCAD çizim menüsü
 */
enum class DrawingTool {
    SELECT,         // Seçim aracı
    LINE,           // Çizgi
    POLYLINE,       // Çoklu çizgi
    CIRCLE,         // Daire
    ARC,            // Yay
    ELLIPSE,        // Elips
    RECTANGLE,      // Dikdörtgen
    POINT,          // Nokta
    TEXT,           // Metin
    HATCH,          // Tarama
    DIMENSION,      // Ölçülendirme
    MOVE,           // Taşı
    COPY,           // Kopyala
    ROTATE,         // Döndür
    SCALE,          // Ölçekle
    MIRROR,         // Aynala
    TRIM,           // Kes/Kırp
    EXTEND,         // Uzat
    OFFSET          // Paralel
}


