package com.example.laba_8;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeocodingResponse {
    @SerializedName("results")
    public List<Address> addressList;
}
