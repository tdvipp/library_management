package com.library.management.dto.request;

import com.library.management.entity.Role;
import lombok.Data;

@Data
public class UserRequestDTO {
    private String name;
    private String email;
    private String password;
    private Role role;
    private int code;
}
