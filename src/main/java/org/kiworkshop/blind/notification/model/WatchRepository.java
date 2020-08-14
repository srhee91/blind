package org.kiworkshop.blind.notification.model;

import java.util.List;
import java.util.Optional;

import org.kiworkshop.blind.post.domain.Post;
import org.kiworkshop.blind.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WatchRepository extends JpaRepository<Watch, Long> {
    boolean existsByPostAndUser(Post post, User user);

    @Query(value = "SELECT count(*) > 0 FROM Watch WHERE post_id = :postId AND user_id = :userId", nativeQuery = true)
    boolean existsByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query(value = "SELECT * FROM Watch WHERE post_id = :postId AND user_id = :userId", nativeQuery = true)
    Optional<Watch> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @EntityGraph(attributePaths = {"post", "post.author", "post.likes"})
    @Query(value = "SELECT DISTINCT * FROM Watch WHERE user_id = :userId", nativeQuery = true)
    List<Watch> findAllByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"user"})
    @Query(value = "SELECT DISTINCT * FROM Watch WHERE post_id = :postId", nativeQuery = true)
    List<Watch> findAllByPostId(@Param("postId") Long postId);
}
