package com.livraria.erro

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import java.lang.IllegalStateException
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerServerInterceptor : MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {

        try {
            return context.proceed()
        } catch (e: StatusRuntimeException) {

            val error = when (e) {
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException()
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message).asRuntimeException()
                else -> {
                    Status.fromCode(e.status.code).withDescription(e.message).asRuntimeException()
                    //Status.UNKNOWN.withDescription("Erro inesperado: ${e.message}").asRuntimeException()
                }

            }
            val responseObserver = context.parameterValues[1] as StreamObserver<*>
            responseObserver.onError(error)

            return null
        }


    }
}

/*
public static byte[] intToByteArray(int value) {
    byte[] b = new byte[4];
    for (int i = 0; i >> offset) & 0xFF);
}
return b;
}*/
