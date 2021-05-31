package com.zup.edu.extensao

import com.zup.edu.FaturaRequest
import com.zup.edu.model.Fatura
import java.math.BigDecimal

fun FaturaRequest.toModel():Fatura{
    return Fatura(this.chaveRecebidor, BigDecimal(this.valor))
}