package org.kiworkshop.blind.notification.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.kiworkshop.blind.notification.exception.WatchException;
import org.kiworkshop.blind.notification.model.Watch;
import org.kiworkshop.blind.notification.model.WatchRepository;
import org.kiworkshop.blind.post.controller.dto.response.PostSummaryResponsDto;
import org.kiworkshop.blind.post.domain.Post;
import org.kiworkshop.blind.post.repository.PostRepository;
import org.kiworkshop.blind.user.controller.dto.UserSummaryResponseDto;
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

    public Long startWath(Long postId, Long userId) {
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

    public void stopWatch(Long postId, Long userId) {
        Watch watch = findWatchByPostIdAndUserId(postId, userId);
        if (!watch.isOwner(userId)) {
            throw new IllegalStateException("본인이 아닌 경우에 받아보기를 취소할 수 없습니다.");
        }
        watchRepository.delete(watch);
    }

    public Boolean isWatching(Long postId, Long userId) {
        return watchRepository.existsByPostIdAndUserId(postId, userId);
    }

    public List<PostSummaryResponsDto> getWatchList(Long userId) {
        List<Watch> watches = watchRepository.findAllByUserId(userId);
        return watches.stream()
            .map(Watch::getPost)
            .map(PostSummaryResponsDto::from)
            .collect(Collectors.toList());
    }

    public List<UserSummaryResponseDto> getWatchers(Long postId) {
        List<Watch> watches = watchRepository.findAllByPostId(postId);
        return watches.stream()
            .map(Watch::getUser)
            .map(UserSummaryResponseDto::from)
            .collect(Collectors.toList());
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 post입니다."));
    }

    private User findUserBy(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 user입니다."));
    }

    private Watch findWatchByPostIdAndUserId(Long postId, Long userId) {
        return watchRepository.findByPostIdAndUserId(postId, userId)
            .orElseThrow(() -> new WatchException("현재 받아보기 상태가 아닙니다."));
    }
}
