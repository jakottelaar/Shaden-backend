package com.example.shaden.features;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonPropertyOrder({"status", "message", "results"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
    
    @JsonProperty("status")
    private int statusCode;
    private String message;
    private Object results;

}
