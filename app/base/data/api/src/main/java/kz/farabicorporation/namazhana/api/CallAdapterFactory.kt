package kz.farabicorporation.namazhana.api

import kz.farabicorporation.namazhana.arch.api.JsonSerializer
import kz.farabicorporation.namazhana.arch.models.Outcome
import kz.farabicorporation.namazhana.arch.models.ResponseErrorParams
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class CallDelegate<TIn, TOut>(protected val proxy: Call<TIn>) : Call<TOut> {
    override fun execute(): Response<TOut> = throw NotImplementedError()
    final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
    final override fun clone(): Call<TOut> = cloneImpl()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun isExecuted() = proxy.isExecuted
    override fun isCanceled() = proxy.isCanceled

    abstract fun enqueueImpl(callback: Callback<TOut>)
    abstract fun cloneImpl(): Call<TOut>
}

class CallAdapterFactory(
    private val serializer: JsonSerializer
) : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit) =
        when (getRawType(returnType)) {
            Call::class.java -> {
                val callType = getParameterUpperBound(0, returnType as ParameterizedType)
                when (getRawType(callType)) {
                    Outcome::class.java -> {
                        val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                        OutcomeAdapter(resultType, serializer)
                    }
                    else -> null
                }
            }
            else -> null
        }
}

class OutcomeCall<T>(proxy: Call<T>, private val serializer: JsonSerializer) : CallDelegate<T, Outcome<T>>(proxy) {

    override fun enqueueImpl(callback: Callback<Outcome<T>>) =
        proxy.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val code = response.code()
                val body = response.body()
                val result = when (code) {
                    in 200 until 300 -> Outcome.Success(body, code)
                    else -> {
                        val errorBody = response.errorBody()?.string()
                        Outcome.Error.ResponseError(
                                errorBody.parseResponseBodyError(serializer),
                                ResponseErrorParams(code, call.getUrl(), errorBody)
                        )
                    }
                }
                callback.onResponse(this@OutcomeCall, Response.success(result))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val result = when (t) {
                    is IOException -> Outcome.Error.ConnectionError
                    is HttpException -> {
                        val errorBody = t.response()?.errorBody()?.string()
                        Outcome.Error.ResponseError(
                                errorBody.parseResponseBodyError(serializer),
                                ResponseErrorParams(t.code(), call.getUrl(), errorBody)
                        )
                    }
                    else -> Outcome.Error.UnknownError(t.message)
                }
                t.printStackTrace()
                callback.onResponse(this@OutcomeCall, Response.success(result))
            }
        })

    override fun cloneImpl() = OutcomeCall(proxy.clone(), serializer)

    override fun timeout(): Timeout {
        return Timeout()
    }

    private fun Call<T>.getUrl(): String {
        return (request() as Request).url.toString()
    }
}

class OutcomeAdapter(
    private val type: Type,
    private val serializer: JsonSerializer
) : CallAdapter<Type, Call<Outcome<Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<Outcome<Type>> = OutcomeCall(call, serializer)
}