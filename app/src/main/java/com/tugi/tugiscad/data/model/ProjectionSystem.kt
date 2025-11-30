package com.tugi.tugiscad.data.model

/**
 * Koordinat Referans Sistemi (CRS) - Coordinate Reference System
 */
data class CoordinateReferenceSystem(
    val name: String,
    val datum: Datum,
    val projection: Projection,
    val zoneNumber: Int = 0, // UTM/TM için dilim numarası
    val epsgCode: String = "" // EPSG kodu (örn: EPSG:32636)
) {
    override fun toString(): String {
        return if (zoneNumber > 0) {
            "$name - ${datum.name} - Dilim $zoneNumber"
        } else {
            "$name - ${datum.name}"
        }
    }
}

/**
 * Datum Sistemleri
 */
enum class Datum(val displayName: String, val ellipsoid: String) {
    ITRF96("ITRF 96", "GRS 1980"),
    WGS84("WGS 84", "WGS 84"),
    ED50("ED 50", "International 1924"),
    TUREF("TUREF", "GRS 1980"),
    ISTANBUL("İstanbul Datum", "International 1924");

    override fun toString(): String = displayName
}

/**
 * Projeksiyon Sistemleri
 */
enum class Projection(val displayName: String, val code: String) {
    // Türkiye için yaygın sistemler
    TM30("TM 30° (3° Dilim)", "TM30"),
    TM36("TM 36° (3° Dilim)", "TM36"),
    TM39("TM 39° (3° Dilim)", "TM39"),
    TM42("TM 42° (3° Dilim)", "TM42"),

    // UTM Sistemleri
    UTM("UTM (Universal Transverse Mercator)", "UTM"),

    // Diğer
    GEOGRAPHIC("Coğrafi (Lat/Lon)", "GEOG"),
    LOCAL("Yerel Sistem", "LOCAL");

    override fun toString(): String = displayName
}

/**
 * Türkiye için yaygın CRS'ler
 */
object TurkishCRS {
    // ITRF96 - TM Sistemleri
    val ITRF96_TM30_36 = CoordinateReferenceSystem(
        name = "ITRF96 / TM30",
        datum = Datum.ITRF96,
        projection = Projection.TM30,
        zoneNumber = 36,
        epsgCode = "EPSG:5254"
    )

    val ITRF96_TM36_39 = CoordinateReferenceSystem(
        name = "ITRF96 / TM36",
        datum = Datum.ITRF96,
        projection = Projection.TM36,
        zoneNumber = 39,
        epsgCode = "EPSG:5255"
    )

    val ITRF96_TM42_42 = CoordinateReferenceSystem(
        name = "ITRF96 / TM42",
        datum = Datum.ITRF96,
        projection = Projection.TM42,
        zoneNumber = 42,
        epsgCode = "EPSG:5256"
    )

    // WGS84 - UTM Sistemleri
    val WGS84_UTM36N = CoordinateReferenceSystem(
        name = "WGS 84 / UTM",
        datum = Datum.WGS84,
        projection = Projection.UTM,
        zoneNumber = 36,
        epsgCode = "EPSG:32636"
    )

    val WGS84_UTM37N = CoordinateReferenceSystem(
        name = "WGS 84 / UTM",
        datum = Datum.WGS84,
        projection = Projection.UTM,
        zoneNumber = 37,
        epsgCode = "EPSG:32637"
    )

    // ED50 - UTM
    val ED50_UTM36N = CoordinateReferenceSystem(
        name = "ED 50 / UTM",
        datum = Datum.ED50,
        projection = Projection.UTM,
        zoneNumber = 36,
        epsgCode = "EPSG:23036"
    )

    // Coğrafi Koordinat (Lat/Lon)
    val WGS84_GEOGRAPHIC = CoordinateReferenceSystem(
        name = "WGS 84 Geographic",
        datum = Datum.WGS84,
        projection = Projection.GEOGRAPHIC,
        epsgCode = "EPSG:4326"
    )

    /**
     * Tüm hazır sistemler
     */
    val ALL_SYSTEMS = listOf(
        ITRF96_TM30_36,
        ITRF96_TM36_39,
        ITRF96_TM42_42,
        WGS84_UTM36N,
        WGS84_UTM37N,
        ED50_UTM36N,
        WGS84_GEOGRAPHIC
    )
}

/**
 * Koordinat formatı
 */
enum class CoordinateFormat {
    DECIMAL,        // 450123.45
    DMS,           // 41° 02' 34.5" (Derece, Dakika, Saniye)
    DDM;           // 41° 02.575' (Derece, Ondalık Dakika)

    fun format(x: Double, y: Double, crs: CoordinateReferenceSystem): String {
        return when (this) {
            DECIMAL -> {
                if (crs.projection == Projection.GEOGRAPHIC) {
                    "Lat: %.6f° Lon: %.6f°".format(y, x)
                } else {
                    "X: %.2f  Y: %.2f".format(x, y)
                }
            }
            DMS -> {
                if (crs.projection == Projection.GEOGRAPHIC) {
                    "${toDMS(y, true)}  ${toDMS(x, false)}"
                } else {
                    "X: %.2f  Y: %.2f".format(x, y)
                }
            }
            DDM -> {
                if (crs.projection == Projection.GEOGRAPHIC) {
                    "${toDDM(y, true)}  ${toDDM(x, false)}"
                } else {
                    "X: %.2f  Y: %.2f".format(x, y)
                }
            }
        }
    }

    private fun toDMS(decimal: Double, isLatitude: Boolean): String {
        val abs = kotlin.math.abs(decimal)
        val degrees = abs.toInt()
        val minutesDecimal = (abs - degrees) * 60
        val minutes = minutesDecimal.toInt()
        val seconds = (minutesDecimal - minutes) * 60

        val direction = when {
            isLatitude -> if (decimal >= 0) "N" else "S"
            else -> if (decimal >= 0) "E" else "W"
        }

        return "%d° %02d' %05.2f\" %s".format(degrees, minutes, seconds, direction)
    }

    private fun toDDM(decimal: Double, isLatitude: Boolean): String {
        val abs = kotlin.math.abs(decimal)
        val degrees = abs.toInt()
        val minutesDecimal = (abs - degrees) * 60

        val direction = when {
            isLatitude -> if (decimal >= 0) "N" else "S"
            else -> if (decimal >= 0) "E" else "W"
        }

        return "%d° %07.4f' %s".format(degrees, minutesDecimal, direction)
    }
}

