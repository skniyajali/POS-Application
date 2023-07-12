package com.niyaj.popos.features.qrcode_scanner.domain.repository

import kotlinx.coroutines.flow.Flow

interface QRCodeScanner {

    fun startScanning(): Flow<String?>
}