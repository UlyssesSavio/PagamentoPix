package com.zup.edu.testeQrCode

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.util.*


fun main(){
    val qr = QRCodeWriter().encode(UUID.randomUUID().toString(), BarcodeFormat.QR_CODE, 350, 350)


    val base = Base64.getEncoder().encodeToString(qr.toString().toByteArray())
    println(base)

    println(Base64.getDecoder().decode(base))

}