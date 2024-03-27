package com.example.enlaco.Entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "comment")
@SequenceGenerator(
        name = "comment_SEQ",
        sequenceName = "comment_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class CommentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_SEQ")
    private Integer cid;        //댓글 번호
    @Column(name = "cbody", length = 1000)
    private String  cbody;      //댓글 내용
    @Column(name = "cwriter", length = 20)
    private String  cwriter;    //작성자
    /*
    @ColumnDefault("0")
    @Column(name = "rgoodcnt")//레시피 좋아요
    private Integer rgoodcnt;

     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeid")
    private RecipeEntity recipeEntity;
}
