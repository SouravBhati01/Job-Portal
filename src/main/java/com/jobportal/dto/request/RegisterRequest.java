package com.jobportal.dto.request;

import com.jobportal.enums.RoleName;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be 2-100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be 2-100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be 8-64 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    private String password;

    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid phone number")
    private String phone;

    @NotNull(message = "Role is required (ROLE_APPLICANT or ROLE_RECRUITER)")
    private RoleName role;
}
