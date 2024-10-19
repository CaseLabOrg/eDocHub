package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSignatureResponse {
    private Integer hash;
    private String placeholderTitle;
    private CreateUserResponse user;
}
