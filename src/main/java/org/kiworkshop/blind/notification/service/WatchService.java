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

    public void cancelWatch(Long watchId, Long userId) {
        Watch watch = findWatchBy(watchId);
        if (!watch.isOwner(userId)) {
            throw new IllegalStateException("본인이 아닌 경우에 받아보기를 취소할 수 없습니다.");
        }
        watchRepository.deleteById(watchId);
    }

    public Long isWatching(Long postId, Long userId) {
        try {
            Watch watch = findWatchByPostAndUser(postId, userId);
            return watch.getId();
        } catch (EntityNotFoundException e) {
            return 0L;
        }
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 post입니다."));
    }

    private User findUserBy(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 user입니다."));
    }

    private Watch findWatchBy(Long watchId) {
        return watchRepository.findById(watchId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 watch입니다."));
    }

    private Watch findWatchByPostAndUser(Long postId, Long userId) {
        return watchRepository.findByPostIdAndUserId(postId, userId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 watch입니다."));
    }
}
