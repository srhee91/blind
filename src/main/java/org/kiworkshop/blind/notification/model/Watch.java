package org.kiworkshop.blind.notification.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.kiworkshop.blind.post.domain.Post;
import org.kiworkshop.blind.user.domain.User;
import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Watch {
    private static final String NOTIFICATION_MESAGE_FORMAT = "%d번 게시물 '%s' 게시글%s";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "post")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Builder
    private Watch(Post post, User user) {
        Assert.notNull(post, "post should not be null");
        Assert.notNull(user, "user should not be null");
        this.post = post;
        this.user = user;
    }

    public Notification createNotification() {
        Long postId = post.getId();
        return Notification.builder()
            .postId(postId)
            .userId(user.getId())
            .message(String.format(NOTIFICATION_MESAGE_FORMAT, postId, post.getTitleSummary(), "이 수정되었습니다."))
            .build();
    }

    public boolean isOwner(Long userId) {
        return user.getId().equals(userId);
    }
}
