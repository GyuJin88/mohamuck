package com.example.enlaco.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "storage")
@SequenceGenerator(
        name = "storage_SEQ",
        sequenceName = "storage_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class StorageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "storage_SEQ")
    private Integer sid;        //번호
    @Column(name = "simg")
    private String  simg;       //재료사진
    @Column(name = "singre")
    private String  singre;     //식재료명
    @Column(name = "sbuydate")
    private String  sbuydate;   //구매일
    @Column(name = "syutong")
    private String  syutong;    //유통기한
    @Column(name = "squan")
    private Integer squan;      //식재료 개수
    @Column(name = "skeep")
    private String  skeep;       //보관 방법

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid")
    private MemberEntity memberEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rid")
    private RecipeEntity recipeEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private UsersEntity usersEntity;
}
