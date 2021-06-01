package com.zup.edu.endpoints

import br.com.zup.edu.CarregaChavePixRequest
import br.com.zup.edu.CarregaChavePixResponse
import br.com.zup.edu.KeyManagerCarregaGrpc
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.qrcode.QRCodeWriter
import com.livraria.erro.ErrorHandler
import com.zup.edu.*
import com.zup.edu.extensao.toModel
import com.zup.edu.model.Pagamento
import com.zup.edu.repository.FaturaRepository
import com.zup.edu.repository.PagamentoRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import io.reactivex.Completable
import io.reactivex.Observable
import java.math.BigDecimal
import javax.inject.Singleton

@ErrorHandler
@Singleton
class PagamentoEndPoint(val grpcClient: KeyManagerCarregaGrpc.KeyManagerCarregaFutureStub,
                        val repository:PagamentoRepository,
                        val repositoryFatura:FaturaRepository): PagamentoServiceGrpc.PagamentoServiceImplBase() {

    override fun pagamento(request: PagamentoRequest, responseObserver: StreamObserver<PagamentoResponse>) {

        val pagador = grpcClient.carrega(CarregaChavePixRequest.newBuilder().setChave(request.chavePagador).build())
        val recebidor = grpcClient.carrega(CarregaChavePixRequest.newBuilder().setChave(request.chaveRecebidor).build())

        Futures.successfulAsList(listOf(pagador, recebidor)).run {
            val a = this.get()[0]
            val b = this.get()[1]

            if(a==null || b==null) throw Status.NOT_FOUND.asRuntimeException()

            val pag = toPagamento(a,b, request)

            repository.save(pag)
            responseObserver.onNext(pag.toResponse())
            responseObserver.onCompleted()
        }


    }

    override fun geraFatura(request: FaturaRequest, responseObserver: StreamObserver<FaturaResponse>) {

        Completable.fromAction {
            if(grpcClient.carrega(CarregaChavePixRequest.newBuilder().setChave(request.chaveRecebidor).build())
                    == null) throw Status.NOT_FOUND.withDescription("Nao encontrado").asRuntimeException()
        }.doOnError {
            responseObserver.onError(it)
        }.subscribe{
            val fatura = request.toModel()
            repositoryFatura.save(fatura)

            responseObserver.onNext(fatura.toResponse())
            responseObserver.onCompleted()

        }

    }

    private fun toPagamento(pagador: CarregaChavePixResponse, recebidor: CarregaChavePixResponse, request: PagamentoRequest) =
            Pagamento(pagador.chave.chave,
                    recebidor.chave.chave,
                    BigDecimal(request.valor),
                    pagador.chave.conta.nomeDoTitular,
                    recebidor.chave.conta.nomeDoTitular)


}