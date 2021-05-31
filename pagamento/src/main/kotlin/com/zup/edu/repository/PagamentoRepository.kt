package com.zup.edu.repository

import com.zup.edu.model.Pagamento
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface PagamentoRepository : JpaRepository<Pagamento, Long>{
}