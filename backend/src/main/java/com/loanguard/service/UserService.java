package com.loanguard.service;

import com.loanguard.dto.ChangePasswordRequest;
import com.loanguard.dto.ProfileUpdateRequest;
import com.loanguard.dto.UserResponse;
import com.loanguard.exception.ApiException;
import com.loanguard.model.User;
import com.loanguard.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        return UserResponse.from(userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND)));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, ProfileUpdateRequest req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        if (req.fullName() != null) user.setFullName(req.fullName());
        if (req.phone() != null)    user.setPhone(req.phone());
        if (req.address() != null)  user.setAddress(req.address());

        return UserResponse.from(userRepo.save(user));
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        throw new ApiException("Password changes are disabled. This account uses Google sign-in only.", HttpStatus.GONE);
    }

    @Transactional
    public void toggleActive(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        user.setIsActive(!user.getIsActive());
        userRepo.save(user);
    }
}
