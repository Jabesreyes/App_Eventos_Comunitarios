package com.example.login.retrofit
import com.example.login.model.Event
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("api/events/create/")
    fun createEvent(
        @Header("Authorization") token: String,
        @Body event: Event
    ): Call<Event>

    @GET("api/events/")
    fun listEvents(): Call<List<Event>>

    @GET("api/events/{id}/")
    fun getEventDetails(
        @Path("id") eventId: Int
    ): Call<Event>

    @POST("api/events/{id}/rsvp/")
    fun rsvpEvent(
        @Path("id") eventId: Int,
        @Header("Authorization") token: String,
        @Body status: Map<String, String>
    ): Call<Map<String, String>>

    @POST("api/events/{id}/comment/")
    fun commentEvent(
        @Path("id") eventId: Int,
        @Header("Authorization") token: String,
        @Body comment: Map<String, String>
    ): Call<Map<String, String>>

    @GET("api/users/{user_id}/history/")
    fun getUserEventHistory(
        @Path("user_id") userId: Int,
        @Header("Authorization") token: String
    ): Call<List<Event>>

    @GET("api/test-user/")
    fun testUser(
        @Header("Authorization") token: String
    ): Call<Map<String, String>>

    @GET("api/events/today-and-future-events/")
    fun getCurrentAndFutureEvents(): Call<List<Event>>
}