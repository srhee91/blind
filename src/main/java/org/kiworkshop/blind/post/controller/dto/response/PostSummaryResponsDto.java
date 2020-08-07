package org.kiworkshop.blind.post.controller.dto.response;

import org.kiworkshop.blind.post.domain.Post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PostSummaryResponsDto {
    private Long id;
    private String title;
    private String username;
    private int likeCount;

    @Builder
    private PostSummaryResponsDto(Long id, String title, String username, int likeCount) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.likeCount = likeCount;
    }

    public static PostSummaryResponsDto from(Post post) {
        return PostSummaryResponsDto.builder()
            .id(post.getId())
            .title(post.getTitleSummary())
            .username(post.getAuthor().getName())
            .likeCount(post.getLikeCount())
            .build();
    }
}
