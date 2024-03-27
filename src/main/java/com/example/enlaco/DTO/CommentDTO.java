package com.example.enlaco.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
    private Integer cid;
    private String cbody;
    private String cwriter;
    private LocalDateTime regDate;
    private Integer recipeid;
    private Integer rgoodcnt;
}
