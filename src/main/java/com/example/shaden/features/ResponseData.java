package com.example.shaden.features;

import lombok.Data;

@Data
public class ResponseData {
    
    private int statusCode;
    private String message;
    private Object results;

}
