package org.kiworkshop.blind.notification.model;

import org.kiworkshop.blind.post.domain.Post;
import org.kiworkshop.blind.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchRepository extends JpaRepository<Watch, Long> {
    boolean existsByPostAndUser(Post post, User user);
}
