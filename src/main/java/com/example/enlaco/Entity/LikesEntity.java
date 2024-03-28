package com.example.enlaco.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString

@Table(name = "likes")
@SequenceGenerator(
        name = "likes_SEQ",
        sequenceName = "likes_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class LikesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "likes_SEQ")
    private Long likesid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private UsersEntity users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rid")
    private RecipeEntity recipe;

    //likesid는 생성자가 필요 없어서 @AllArgsConstructor 사용 X
    public LikesEntity(UsersEntity users, RecipeEntity recipe) {
        this.users = users;
        this.recipe = recipe;
    }

}
