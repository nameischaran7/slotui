package com.example.s_book;

import com.example.s_book.Vendor;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/vendors")
    Call<List<Vendor>> getVendors();
    @GET("api/slots/vendor/{vendorId}")
    Call<List<Slot>> getSlotsByVendor(@Path("vendorId") long vendorId);

    @POST("api/slots/{slotId}/book")
    Call<Slot> bookSlot(
            @Path("slotId") long slotId,
            @Body User userRequest // Change from Slot to User
    );
    @POST("api/users/login")
    Call<User> loginUser(@Body User user);
    @POST("api/users/signup")
    Call<User> signupUser(@Body User user);
    @POST("api/vendors/signup")
    Call<Vendor> signupVendor(@Body Vendor vendor);
    @GET("api/vendors/search")
    Call<List<Vendor>> searchVendors(@Query("query") String query);
    @GET("api/slots/user/{userId}")
    Call<List<Slot>> getUserBookings(@Path("userId") long userId);
    @POST("api/slots/{slotId}/verify-checkin")
    Call<ResponseBody> verifyCheckIn(@Path("slotId") long slotId);
    @POST("api/slots/{slotId}/cancel")
    Call<ResponseBody> cancelBooking(@Path("slotId") long slotId);
}