package org.kiworkshop.blind.notification.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kiworkshop.blind.post.domain.PostTest.*;
import static org.kiworkshop.blind.user.domain.UserTest.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

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

@ExtendWith(MockitoExtension.class)
class WatchServiceTest {
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
        Post post = getPostFixture();
        User user = getWatcherFixture();
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(watchRepository.existsByPostAndUser(post, user)).willReturn(false);

        watchService.watch(post.getId(), user.getId());

        verify(watchRepository).save(any(Watch.class));
    }

    @Test
    void watchException() {
        Post post = getPostFixture();
        User user = getWatcherFixture();
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(watchRepository.existsByPostAndUser(post, user)).willReturn(true);

        assertThatThrownBy(() -> watchService.watch(post.getId(), user.getId()))
            .isInstanceOf(IllegalStateException.class);
    }
}
