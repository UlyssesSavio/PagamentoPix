package com.zup.edu.grpcClient

import br.com.zup.edu.KeyManagerCarregaGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory {

    @Singleton
    fun buscaClientStub(@GrpcChannel("keyManager") channel: ManagedChannel): KeyManagerCarregaGrpc.KeyManagerCarregaFutureStub?{
        return KeyManagerCarregaGrpc.newFutureStub(channel)
    }
}