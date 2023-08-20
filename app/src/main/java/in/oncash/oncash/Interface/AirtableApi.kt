package `in`.oncash.oncash.Interface

import retrofit2.Call
import retrofit2.http.GET

interface AirtableApi {
    @GET("/")
    fun getRecords(): Call<String>
}