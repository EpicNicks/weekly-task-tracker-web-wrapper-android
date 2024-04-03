package com.aspirant.weeklytasktrackerwebwrapper.model.entity.response

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.ParameterizedType

class ApiResponseAdapter<E>(private val adapter: TypeAdapter<E>) : TypeAdapter<ApiResponse<E>>() {

    companion object {
        val FACTORY: TypeAdapterFactory = object : TypeAdapterFactory {
            override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
                val rawType = type.rawType as? Class<T>
                if (rawType != ApiResponse::class.java) {
                    return null
                }
                val parameterizedType = type.type as? ParameterizedType ?: return null
                val actualType = parameterizedType.actualTypeArguments[0]
                val adapter = gson.getAdapter(TypeToken.get(actualType))
                @Suppress("UNCHECKED_CAST")
                return ApiResponseAdapter(adapter) as TypeAdapter<T>
            }
        }
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): ApiResponse<E> {
        `in`.beginObject()
        var success: Boolean? = null
        var value: E? = null
        while (`in`.hasNext()) {
            when (`in`.nextName()) {
                "success" -> success = `in`.nextBoolean()
                "value" -> {
                    value = adapter.read(`in`)
                }

                else -> `in`.skipValue() // Skip unexpected properties
            }
        }
        `in`.endObject()

        return if (success == true && value != null) {
            ApiResponse.success(value)
        } else {
            ApiResponse.failure("Failure message") // Provide an appropriate failure message
        }
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: ApiResponse<E>) {
        out.beginObject()
        out.name("success").value(value is ApiResponse.Success)
        out.name("value")
        when (value) {
            is ApiResponse.Success -> adapter.write(out, value.value)
            is ApiResponse.Failure -> out.nullValue()
        }
        out.endObject()
    }
}

