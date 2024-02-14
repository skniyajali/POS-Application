package com.niyaj.ui.util

fun safeString(price : String) : Int {
    return if (price.isEmpty()) {
        0
    } else {
        try {
            price.toInt()
        } catch (e : NumberFormatException) {
            0
        }
    }
}

fun String.safeInt() : Int {
    return if (this.isEmpty()) {
        0
    } else {
        try {
            this.toInt()
        } catch (e : NumberFormatException) {
            0
        }
    }
}

fun String.safeFloat() : Float {
    return if (this.isEmpty()) {
        0f
    } else {
        try {
            this.toFloat()
        } catch (e : NumberFormatException) {
            0f
        }
    }
}