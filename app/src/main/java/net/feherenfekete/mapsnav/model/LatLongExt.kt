package net.feherenfekete.mapsnav.model

import java.math.BigDecimal
import java.math.RoundingMode

// https://en.wikipedia.org/wiki/Decimal_degrees#Precision
enum class LatLongTolerance(val digits: Int) {
    Tolerance_111_km(0),
    Tolerance_11_km(1),
    Tolerance_1_km(2),
    Tolerance_111_m(3),
    Tolerance_11_m(4),
    Tolerance_1_m(5),
    Tolerance_11_cm(6),
    Tolerance_1_cm(7)
}

fun LatLongData.isEqualWithTolerance(
    b: LatLongData,
    tolerance: LatLongTolerance = LatLongTolerance.Tolerance_11_m
): Boolean {
    return isEqualWithTolerance(this.latitude, b.latitude, tolerance.digits) &&
            isEqualWithTolerance(this.longitude, b.longitude, tolerance.digits)
}

private fun isEqualWithTolerance(a: Double, b: Double, digits: Int): Boolean {
    val ra = round(a, digits)
    val rb = round(b, digits)
    return ra == rb
}

// This rounding method (using BigDecimal) avoids issues with floating point
// inaccuracies. Specifically, don't do this for rounding: round(a * 1000) / 1000
private fun round(value: Double, places: Int): Double {
    var bd = BigDecimal(value.toString())
    bd = bd.setScale(places, RoundingMode.HALF_UP)
    return bd.toDouble()
}
