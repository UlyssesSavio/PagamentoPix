package com.zup.edu.model

import com.google.protobuf.Timestamp
import com.google.type.Money
import com.zup.edu.PagamentoResponse
import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Introspected
class Pagamento (@field:NotBlank val chavePagador:String,
                 @field:NotBlank val chaveRecebidor:String,
                 @field:NotNull val valor: BigDecimal,
                 @field:NotBlank val nomePagador:String,
                 @field:NotBlank val nomeRecebidor:String){

    @Id
    @GeneratedValue
    var id:Long? = null

    val dataRealizada = LocalDateTime.now()

    fun toResponse(): PagamentoResponse{
        val inteiro=valor.toInt()
        val fracionado=valor.minus(inteiro.toBigDecimal())

        return PagamentoResponse.newBuilder()
                .setNomePagador(nomePagador)
                .setNomeRecebidor(nomeRecebidor)
                .setValor(valor.toString())
                .setDataRealizada(Timestamp.newBuilder()
                        .setSeconds(dataRealizada.second.toLong())
                        .setNanos(dataRealizada.nano).build())
                .build()
    }


}