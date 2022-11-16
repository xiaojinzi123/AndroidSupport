package com.xiaojinzi.support.retrofit

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

private class GsonResponseBodyConverterWrap(
    private val delegate: Converter<ResponseBody, *>?,
) : Converter<ResponseBody, Any> {

    override fun convert(value: ResponseBody): Any? {
        return if (value.contentLength() == 0L) {
            return null
        } else {
            delegate?.convert(value)
        }
    }

}

/**
 * 为了解决 gson 无法对 [ResponseBody] 的内容为空的时候的转化问题
 */
class GsonConverterFactoryWrap(
    val delegate: Converter.Factory,
) : Converter.Factory() {

    companion object {

        fun create(delegate: Converter.Factory): GsonConverterFactoryWrap {
            return GsonConverterFactoryWrap(
                delegate = delegate,
            )
        }

    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return delegate.requestBodyConverter(
            type,
            parameterAnnotations,
            methodAnnotations,
            retrofit
        )
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return GsonResponseBodyConverterWrap(
            delegate = delegate.responseBodyConverter(type, annotations, retrofit)
        )
    }

}