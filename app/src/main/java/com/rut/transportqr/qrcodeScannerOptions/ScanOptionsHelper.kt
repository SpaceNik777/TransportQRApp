package com.rut.transportqr.qrcodeScannerOptions

import com.journeyapps.barcodescanner.ScanOptions

class ScanOptionsHelper {
    fun createOptions(): ScanOptions {
        val options = ScanOptions()
        setupBarcodeFormat(options)
        setupPrompt(options)
        setupCameraId(options)
        setupBeepEnabled(options)
        setupBarcodeImageEnabled(options)
        return options
    }
    private fun setupBarcodeFormat(options: ScanOptions) {
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
    }
    private fun setupPrompt(options: ScanOptions) {
        options.setPrompt("Отсканируйте QR Code")
    }
    private fun setupCameraId(options: ScanOptions) {
        options.setCameraId(1)
    }
    private fun setupBeepEnabled(options: ScanOptions) {
        options.setBeepEnabled(false)
    }
    private fun setupBarcodeImageEnabled(options: ScanOptions) {
        options.setBarcodeImageEnabled(true)
    }
}
