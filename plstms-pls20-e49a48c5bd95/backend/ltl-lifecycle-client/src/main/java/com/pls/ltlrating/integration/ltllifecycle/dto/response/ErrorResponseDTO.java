package com.pls.ltlrating.integration.ltllifecycle.dto.response;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    String message;
    String code = "INTERNAL";
}
