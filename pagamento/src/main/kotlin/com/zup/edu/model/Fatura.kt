package com.zup.edu.model

import com.google.protobuf.Timestamp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.zup.edu.FaturaResponse
import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Introspected
class Fatura (@field:NotBlank val chaveRecebidor:String,
              @field:NotNull val valor:BigDecimal){


    val criadaEm = LocalDateTime.now()
    val pagada = false;

    @Id
    val qrCode = UUID.randomUUID().toString()
   // val qrCode = QRCodeWriter().encode(UUID.randomUUID().toString(), BarcodeFormat.QR_CODE, 350, 350).toString()

    fun toResponse(): FaturaResponse {
        return FaturaResponse.newBuilder()
                .setCriadaEm(Timestamp.newBuilder()
                        .setNanos(criadaEm.nano)
                        .setSeconds(criadaEm.second.toLong())
                        .build())
                .build()
    }


}