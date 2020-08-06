package org.kiworkshop.blind.user.service;

import lombok.RequiredArgsConstructor;
import org.kiworkshop.blind.user.controller.LoginRequest;
import org.kiworkshop.blind.user.controller.dto.UserRequestDto;
import org.kiworkshop.blind.user.controller.dto.UserResponseDto;
import org.kiworkshop.blind.user.domain.User;
import org.kiworkshop.blind.user.repository.UserRepository;
import org.kiworkshop.blind.user.util.PasswordEncryptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public Long createUser(UserRequestDto userRequestDto) {
        User user = userRequestDto.toEntity();
        return userRepository.save(user).getId();
    }

    public UserResponseDto readUserBy(Long id) {
        return UserResponseDto.from(findById(id));
    }

    public void updateById(Long id, UserRequestDto userRequestDto) {
        User userToUpdate = userRequestDto.toEntity();
        User user = findById(id);
        user.update(userToUpdate);
        userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void login(HttpSession session, LoginRequest loginRequest) {
        Object loginedUser = session.getAttribute("LOGIN_USER");
        validateAlreadyLogined(loginedUser);
        User user = findByEmail(loginRequest.getEmail());
        String encryptedPassword = PasswordEncryptor.encrypt(loginRequest.getPassword());
        matchPassword(user, encryptedPassword);
        session.setAttribute("LOGIN_USER", user);
    }

    public void logout(HttpSession session) {
        Object loginedUser = session.getAttribute("LOGIN_USER");
        if (Objects.isNull(loginedUser)) {
            throw new IllegalArgumentException("로그인 되어 있지 않습니다");
        }
        session.invalidate();
    }

    private void validateAlreadyLogined(Object loginedUser) {
        if (Objects.nonNull(loginedUser)) {
            throw new IllegalArgumentException("이미 로그인 되었습니다.");
        }
    }

    private void matchPassword(User user, String encryptedPassword) {
        if (!user.matchPassword(encryptedPassword)) {
            throw new IllegalArgumentException("잘못된 패스워드 입니다.");
        }
    }

    User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email);
            //.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 email입니다."));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }
}
