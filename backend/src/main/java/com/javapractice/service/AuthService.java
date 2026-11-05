package com.javapractice.service;

import com.javapractice.dto.LoginRequest;
import com.javapractice.dto.LoginResponse;
import com.javapractice.dto.RegisterRequest;
import com.javapractice.entity.User;
import com.javapractice.exception.BusinessException;
import com.javapractice.repository.UserRepository;
import com.javapractice.util.JwtUtil;
import com.javapractice.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (!StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getNickname())
                || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("用户名、昵称和密码不能为空");
        }
        if (request.getPassword().length() < 6) {
            throw new BusinessException("密码至少 6 位");
        }
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setNickname(request.getNickname().trim());
        user.setPassword(PasswordUtil.sha256(request.getPassword()));
        user.setLevel(0);
        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("请输入用户名和密码");
        }
        User user = userRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        if (!PasswordUtil.sha256(request.getPassword()).equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return new LoginResponse(jwtUtil.generate(user.getId()), user);
    }

    public User me(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }
}
