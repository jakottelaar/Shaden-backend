package com.example.shaden.features;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ResponseData {
    
    @JsonProperty("status")
    private int statusCode;
    private String message;
    private Object results;

}
