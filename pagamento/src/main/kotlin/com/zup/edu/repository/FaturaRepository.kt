package com.zup.edu.repository

import com.zup.edu.model.Fatura
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface FaturaRepository:JpaRepository<Fatura, String> {
    fun existsByQrCode(qrCode:String): Boolean
}