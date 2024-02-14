package com.niyaj.data.repository

import kotlinx.coroutines.flow.Flow

interface QRCodeScanner {

    fun startScanning(): Flow<String?>
}