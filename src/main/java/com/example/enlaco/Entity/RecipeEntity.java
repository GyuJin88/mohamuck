package com.example.enlaco.Entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "recipe")
@SequenceGenerator(
        name = "recipe_SEQ",
        sequenceName = "recipe_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class RecipeEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipe_SEQ")
    private Integer rid;        //레시피 번호
    @Column(name = "rimg")
    private String  rimg;       //사진
    @Column(name = "rmenu")
    private String  rmenu;      //메뉴명
    @Lob
    @Column(name = "rcontent", length = 5000)
    private String  rcontent;   //내용
    @Column(name = "rwriter", length = 20)
    private String  rwriter;    //작성자
    @Column(name = "rclass")
    private String  rclass;     //분류
    @Column(name = "rtime")
    private String  rtime;      //조리시간
    @Column(name = "rselect")
    private String  rselect;    //식재료 선택
    @Column(name = "rviewcnt")
    private Integer rviewcnt;   //조회수
    @Column(name = "rgoodcnt")
    private Integer rgoodcnt;   //좋아요 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid")
    private MemberEntity memberEntity;        //회원 번호 / 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private UserEntity userEntity;              //api 로그인



}
