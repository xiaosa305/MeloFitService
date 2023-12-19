package com.melosound.fit.domain.rsp;

import lombok.Data;

@Data
public class ApiResponse {
    private int code;
    private String message;
    private Object data;
}

