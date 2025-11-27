package com.tugi.tugiscad.data.model

/**
 * CAD Projesi - TugisCAD proje yapısı
 */
data class CADProject(
    val id: String,
    val name: String,
    val description: String = "",
    val scale: Double = 1000.0, // 1:1000 gibi
    val unit: MeasureUnit = MeasureUnit.METER,
    val layers: MutableList<Layer> = mutableListOf(),
    val objects: MutableList<CADObject> = mutableListOf(),
    val createdDate: Long = System.currentTimeMillis(),
    val modifiedDate: Long = System.currentTimeMillis(),
    val author: String = "",
    val bounds: Bounds? = null
)

/**
 * Ölçü Birimi
 */
enum class MeasureUnit {
    MILLIMETER, CENTIMETER, METER, KILOMETER, INCH, FOOT, MILE
}

/**
 * Proje Sınırları
 */
data class Bounds(
    val minX: Double,
    val minY: Double,
    val maxX: Double,
    val maxY: Double
) {
    val width: Double get() = maxX - minX
    val height: Double get() = maxY - minY
    val centerX: Double get() = (minX + maxX) / 2
    val centerY: Double get() = (minY + maxY) / 2
}

