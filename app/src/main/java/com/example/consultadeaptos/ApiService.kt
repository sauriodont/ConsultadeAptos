package com.example.consultadeaptos

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("recibos/anos")
    suspend fun getAnos(): List<DriveFile>

    @GET("recibos/meses/{anoId}")
    suspend fun getMeses(@Path("anoId") anoId: String): List<DriveFile>

    @GET("recibos/{mesId}/{apto}")
    suspend fun downloadRecibo(
        @Path("mesId") mesId: String,
        @Path("apto") apto: String
    ): Response<ResponseBody>
}
