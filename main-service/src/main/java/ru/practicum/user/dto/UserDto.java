package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    private long id;

    @NotBlank(message = "Field: name. Error: must not be blank")
    @Size(min = 2, max = 250)
    private String name;

    @NotBlank(message = "Field: email. Error: must not be blank")
    @Email(message = "Field: email. Error: must be in email format")
    @Size(min = 6, max = 254)
    private String email;
}