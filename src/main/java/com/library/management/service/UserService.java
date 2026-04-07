package com.library.management.service;

import java.util.List;

import com.library.management.entity.*;
import com.library.management.dto.request.*;
import com.library.management.dto.response.*;


public interface UserService {
    List<UserResponseDTO> getUsers(UserFilterDTO request);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO createUser(UserRequestDTO request);
    UserResponseDTO updateUser(Long id, UserRequestDTO request);
    void deleteUser(Long id);
    
}