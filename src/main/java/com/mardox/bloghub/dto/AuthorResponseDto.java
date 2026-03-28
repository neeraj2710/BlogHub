package com.mardox.bloghub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorResponseDto {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String about;

}
