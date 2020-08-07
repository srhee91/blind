package org.kiworkshop.blind.notification.service;

import javax.persistence.EntityNotFoundException;

import org.kiworkshop.blind.notification.model.Watch;
import org.kiworkshop.blind.notification.model.WatchRepository;
import org.kiworkshop.blind.post.domain.Post;
import org.kiworkshop.blind.post.repository.PostRepository;
import org.kiworkshop.blind.user.domain.User;
import org.kiworkshop.blind.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WatchService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final WatchRepository watchRepository;

    public Long watch(Long postId, Long userId) {
        Post post = findPostBy(postId);
        User user = findUserBy(userId);
        if (watchRepository.existsByPostAndUser(post, user)) {
            throw new IllegalStateException("이미 받아보기 상태입니다.");
        }
        Watch watch = Watch.builder()
            .post(post)
            .user(user)
            .build();
        return watchRepository.save(watch).getId();
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 post입니다."));
    }

    private User findUserBy(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 user입니다."));
    }
}
