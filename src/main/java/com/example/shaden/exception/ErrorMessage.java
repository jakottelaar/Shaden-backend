package com.example.shaden.exception;

import java.util.Date;
import lombok.Data;

@Data
public class ErrorMessage {
    
    private int statusCode;
    private Date timestamp;
    private String message;

}
