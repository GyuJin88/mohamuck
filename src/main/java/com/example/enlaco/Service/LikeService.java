package com.example.enlaco.Service;

import com.example.enlaco.Entity.LikesEntity;
import com.example.enlaco.Entity.RecipeEntity;
import com.example.enlaco.Entity.UsersEntity;
import com.example.enlaco.Repository.LikeRepository;
import com.example.enlaco.Repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;
    private final LikeRepository likeRepository;

    public void addLike(int rid, UsersEntity users) {
        RecipeEntity recipe = recipeRepository.findByRid(rid);
        if (!likeRepository.existsByUsersAndRecipe(users, recipe)) {
            //호출되면 board에 있는 count 증가
            recipe.setRgoodcnt(recipe.getRgoodcnt()+1);
            // likeRepository에 userid 값이랑 recipe값 저장해버림
            likeRepository.save(new LikesEntity(users, recipe));
        }
        else {
            recipe.setRgoodcnt(recipe.getRgoodcnt()-1);
            likeRepository.deleteByUsersAndRecipe(users, recipe);
        }
    }

}
