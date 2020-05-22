package com.example.laba_8;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {
    @SerializedName("location")
    @Expose
    public Coordinates coordinates;

    public class Coordinates {
        public Double lat;
        public Double lng;
    }
}

