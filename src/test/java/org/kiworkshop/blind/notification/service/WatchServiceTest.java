package org.kiworkshop.blind.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kiworkshop.blind.post.domain.PostTest.*;
import static org.kiworkshop.blind.user.domain.UserTest.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kiworkshop.blind.notification.model.Watch;
import org.kiworkshop.blind.notification.model.WatchRepository;
import org.kiworkshop.blind.post.controller.dto.response.PostSummaryResponseDto;
import org.kiworkshop.blind.post.domain.Post;
import org.kiworkshop.blind.post.repository.PostRepository;
import org.kiworkshop.blind.user.controller.dto.UserSummaryResponseDto;
import org.kiworkshop.blind.user.domain.User;
import org.kiworkshop.blind.user.repository.UserRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class WatchServiceTest {
    private static final Post POST = getPostFixture();
    private static final User WATCHER = getWatcherFixture();
    private static final Watch WATCH = getWatchFixture();
    private WatchService watchService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WatchRepository watchRepository;

    @BeforeEach
    void setUp() {
        watchService = new WatchService(postRepository, userRepository, watchRepository);
    }

    @Test
    void watch() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(POST));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(WATCHER));
        given(watchRepository.existsByPostAndUser(POST, WATCHER)).willReturn(false);
        given(watchRepository.save(any(Watch.class))).willReturn(WATCH);

        watchService.startWatch(POST.getId(), WATCHER.getId());

        verify(watchRepository).save(any(Watch.class));
    }

    @Test
    void watchException() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(POST));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(WATCHER));
        given(watchRepository.existsByPostAndUser(POST, WATCHER)).willReturn(true);

        assertThatThrownBy(() -> watchService.startWatch(POST.getId(), WATCHER.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancelWatch() {
        given(watchRepository.findById(anyLong())).willReturn(Optional.of(WATCH));

        watchService.stopWatch(WATCH.getId(), WATCH.getUser().getId());

        verify(watchRepository).deleteById(WATCH.getId());
    }

    @Test
    void cancelWatchException() {
        given(watchRepository.findById(anyLong())).willReturn(Optional.of(WATCH));

        assertThatThrownBy(() -> watchService.stopWatch(WATCH.getId(), null))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void isWatching() {
        Watch watch = getWatchFixture();
        given(watchRepository.findByPostIdAndUserId(POST.getId(), WATCHER.getId())).willReturn(Optional.of(watch));

        Boolean isWatching = watchService.isWatching(POST.getId(), WATCHER.getId());

        assertThat(isWatching).isEqualTo(true);
    }

    @Test
    void isNotWatching() {
        given(watchRepository.findByPostIdAndUserId(POST.getId(), WATCHER.getId())).willThrow(new EntityNotFoundException());

        Boolean isWatching = watchService.isWatching(POST.getId(), WATCHER.getId());

        assertThat(isWatching).isEqualTo(false);
    }

    @Test
    void getWatchList() {
        List<Watch> watches = Collections.singletonList(WATCH);
        given(watchRepository.findAllByUserId(anyLong())).willReturn(watches);

        List<PostSummaryResponseDto> posts = watchService.getWatchList(WATCHER.getId());

        assertThat(posts).size().isEqualTo(1);
        assertThat(posts.get(0).getId()).isEqualTo(POST.getId());
    }

    @Test
    void getWatcherList() {
        List<Watch> watches = Collections.singletonList(WATCH);
        given(watchRepository.findAllByPostId(anyLong())).willReturn(watches);

        List<UserSummaryResponseDto> users = watchService.getWatchers(POST.getId());

        assertThat(users).size().isEqualTo(1);
        assertThat(users.get(0).getId()).isEqualTo(WATCHER.getId());
    }

    public static Watch getWatchFixture() {
        Watch watch = Watch.builder()
            .post(POST)
            .user(WATCHER)
            .build();
        ReflectionTestUtils.setField(watch, "id", 1L);
        ReflectionTestUtils.setField(watch, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(watch, "updatedAt", LocalDateTime.now());
        return watch;
    }
}
