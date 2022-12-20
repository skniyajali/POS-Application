package com.niyaj.popos.domain.util

fun safeString(price: String): Int{
    return if(price.isEmpty()){
        0
    } else{
        try {
            price.toInt()
        }catch (e: NumberFormatException) {
            0
        }
    }
}