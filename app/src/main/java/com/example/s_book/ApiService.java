package com.example.s_book;

import com.example.s_book.Vendor;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/vendors")
    Call<List<Vendor>> getVendors();
}