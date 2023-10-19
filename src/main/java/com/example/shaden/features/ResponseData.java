package com.example.shaden.features;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"status", "message", "results"})
public class ResponseData {
    
    @JsonProperty("status")
    private int statusCode;
    private String message;
    private Object results;

}
