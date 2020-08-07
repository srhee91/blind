package org.kiworkshop.blind.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kiworkshop.blind.post.domain.PostTest.*;
import static org.kiworkshop.blind.user.domain.UserTest.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kiworkshop.blind.notification.model.Watch;
import org.kiworkshop.blind.notification.model.WatchRepository;
import org.kiworkshop.blind.post.domain.Post;
import org.kiworkshop.blind.post.repository.PostRepository;
import org.kiworkshop.blind.user.domain.User;
import org.kiworkshop.blind.user.repository.UserRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class WatchServiceTest {
    private static final Post POST = getPostFixture();
    private static final User USER = getWatcherFixture();
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
        given(userRepository.findById(anyLong())).willReturn(Optional.of(USER));
        given(watchRepository.existsByPostAndUser(POST, USER)).willReturn(false);

        watchService.watch(POST.getId(), USER.getId());

        verify(watchRepository).save(any(Watch.class));
    }

    @Test
    void watchException() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(POST));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(USER));
        given(watchRepository.existsByPostAndUser(POST, USER)).willReturn(true);

        assertThatThrownBy(() -> watchService.watch(POST.getId(), USER.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancelWatch() {
        Watch watch = getWatchFixture();
        given(watchRepository.findById(anyLong())).willReturn(Optional.of(watch));

        watchService.cancelWatch(watch.getId(), watch.getUser().getId());

        verify(watchRepository).deleteById(watch.getId());
    }

    @Test
    void cancelWatchException() {
        Watch watch = getWatchFixture();
        given(watchRepository.findById(anyLong())).willReturn(Optional.of(watch));

        assertThatThrownBy(() -> watchService.cancelWatch(watch.getId(), null))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void isWatching() {
        Watch watch = getWatchFixture();
        given(watchRepository.findByPostIdAndUserId(POST.getId(), USER.getId())).willReturn(Optional.of(watch));

        Long watchId = watchService.getWatchId(POST.getId(), USER.getId());

        assertThat(watchId).isEqualTo(watch.getId());
    }

    @Test
    void isNotWatching() {
        given(watchRepository.findByPostIdAndUserId(POST.getId(), USER.getId())).willThrow(new EntityNotFoundException());

        Long watchId = watchService.getWatchId(POST.getId(), USER.getId());

        assertThat(watchId).isEqualTo(0L);
    }

    public static Watch getWatchFixture() {
        Watch watch = Watch.builder()
            .post(POST)
            .user(USER)
            .build();
        ReflectionTestUtils.setField(watch, "id", 1L);
        ReflectionTestUtils.setField(watch, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(watch, "updatedAt", LocalDateTime.now());
        return watch;
    }
}
