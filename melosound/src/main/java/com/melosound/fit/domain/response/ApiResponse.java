package com.melosound.fit.domain.response;

import lombok.Data;

@Data
public class ApiResponse {
    private int code;
    private String message;
    private Object data;
}

