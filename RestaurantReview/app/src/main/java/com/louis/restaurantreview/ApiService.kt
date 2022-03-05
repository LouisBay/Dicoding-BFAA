package com.louis.restaurantreview

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("detail/{id}")
    fun getRestaurant(
        @Path("id") id: String
    ) : Call<RestaurantResponse>
}