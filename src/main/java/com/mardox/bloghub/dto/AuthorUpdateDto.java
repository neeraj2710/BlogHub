package com.mardox.bloghub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorUpdateDto {

    @Size(min = 1,message = "Name is required")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 1,message = "about is required")
    private String about;

}
