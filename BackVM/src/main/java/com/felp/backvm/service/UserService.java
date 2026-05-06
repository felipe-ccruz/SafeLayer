package com.felp.backvm.service;

import com.felp.backvm.domain.User;
import com.felp.backvm.domain.enums.UserPlan;
import com.felp.backvm.dto.LoginRequest;
import com.felp.backvm.dto.RegisterRequest;
import com.felp.backvm.dto.UserDTO;
import com.felp.backvm.exception.EmailAlreadyExistsException;
import com.felp.backvm.exception.InvalidCredentialsException;
import com.felp.backvm.exception.UserNotFoundException;
import com.felp.backvm.repository.UserRepository;
import com.felp.backvm.util.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public UserDTO register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(PasswordHasher.hash(request.getPassword()))
                .plan(UserPlan.BASIC)
                .build();
        return toDto(repository.save(user));
    }

    public UserDTO login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = repository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!PasswordHasher.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return toDto(user);
    }

    public UserDTO changePlan(Long userId, UserPlan plan) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setPlan(plan);
        return toDto(repository.save(user));
    }

    public User getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public UserDTO toDto(User u) {
        UserPlan plan = u.getPlan();
        return UserDTO.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .plan(plan.name())
                .planLabel(plan.getLabel())
                .maxVms(plan.isUnlimited() ? -1 : plan.getMaxVms())
                .unlimited(plan.isUnlimited())
                .build();
    }
}
