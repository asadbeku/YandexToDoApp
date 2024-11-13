package uz.foursquare.todoapp.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import uz.foursquare.todoapp.types.ToDoEditedResponse
import uz.foursquare.todoapp.types.ToDoItemRequest
import uz.foursquare.todoapp.types.ToDoItemsResponse
import uz.foursquare.todoapp.types.TodoItem

interface ApiService {

    @GET("list")
    suspend fun getTasks(): Response<ToDoItemsResponse>

    @POST("list")
    suspend fun addTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: String,
        @Body task: JsonObject
    ): Response<Any>

    @PUT("list/{id}")
    suspend fun updateTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: String,
        @Path("id") id: String,
        @Body task: JsonObject
    ): Response<Any>

    @DELETE("list/{id}")
    suspend fun removeTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: String,
        @Path("id") id: String
    ): Response<Any>

    @GET("list/{id}")
    suspend fun getTaskById(
        @Header("X-Last-Known-Revision") lastKnownRevision: String,
        @Path("id") id: String
    ): Response<ToDoEditedResponse>


}