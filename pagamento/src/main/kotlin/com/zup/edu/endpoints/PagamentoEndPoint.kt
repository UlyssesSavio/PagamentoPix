package com.zup.edu.endpoints

import br.com.zup.edu.CarregaChavePixRequest
import br.com.zup.edu.CarregaChavePixResponse
import br.com.zup.edu.KeyManagerCarregaGrpc
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
class PagamentoEndPoint(val grpcClient: KeyManagerCarregaGrpc.KeyManagerCarregaBlockingStub,
                        val repository:PagamentoRepository,
                        val repositoryFatura:FaturaRepository): PagamentoServiceGrpc.PagamentoServiceImplBase() {

    override fun pagamento(request: PagamentoRequest, responseObserver: StreamObserver<PagamentoResponse>) {
        var pagador: CarregaChavePixResponse? =null
        var recebidor:CarregaChavePixResponse?=null

        Completable.fromAction {
            pagador = grpcClient.carrega(CarregaChavePixRequest.newBuilder().setChave(request.chavePagador).build())
            recebidor = grpcClient.carrega(CarregaChavePixRequest.newBuilder().setChave(request.chaveRecebidor).build())

        }.doOnError {
            responseObserver.onError(it)
        }.subscribe{
            val pag= pagador?.let { recebidor?.let { it1 -> toPagamento(it, it1, request) } }
            repository.save(pag)
            responseObserver.onNext(pag?.toResponse())
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