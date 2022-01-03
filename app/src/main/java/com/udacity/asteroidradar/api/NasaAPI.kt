package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.utils.Constants.API_KEY
import com.udacity.asteroidradar.utils.Constants.BASE_URL
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type

interface NasaApiService {

    @GET("neo/rest/v1/feed")
    @ScalarConverter
    suspend fun getAsteroids(@Query("start_date") startDate: String, @Query("end_date") endDate: String, @Query("api_key") APIkey: String = API_KEY): String

    @GET("planetary/apod")
    @MoshiConverter
    suspend fun getPictureOfDay(@Query("api_key") APIkey: String = API_KEY): PictureOfDay

}

@Target(AnnotationTarget.FUNCTION)
annotation class ScalarConverter

@Target(AnnotationTarget.FUNCTION)
annotation class MoshiConverter

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

class ScalarOrMoshiConverterFactory: Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        for(annotation: Annotation in annotations) {
            return when(annotation.annotationClass) {
                ScalarConverter::class ->
                    ScalarsConverterFactory.create().responseBodyConverter(type, annotations, retrofit)
                MoshiConverter::class ->
                    MoshiConverterFactory.create(moshi).responseBodyConverter(type, annotations, retrofit)
                else ->
                    ScalarsConverterFactory.create().responseBodyConverter(type, annotations, retrofit)
            }
        }

        return super.responseBodyConverter(type, annotations, retrofit)
    }
}

object Network {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarOrMoshiConverterFactory())
        .build()

    val nasaApi: NasaApiService = retrofit.create(NasaApiService::class.java)

}
