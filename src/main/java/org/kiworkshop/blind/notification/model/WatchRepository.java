package org.kiworkshop.blind.notification.model;

import java.util.Optional;

import org.kiworkshop.blind.post.domain.Post;
import org.kiworkshop.blind.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WatchRepository extends JpaRepository<Watch, Long> {
    boolean existsByPostAndUser(Post post, User user);
    @Query(value = "SELECT * FROM watch w WHERE w.post = :postId AND w.user = :userId", nativeQuery = true)
    Optional<Watch> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}
