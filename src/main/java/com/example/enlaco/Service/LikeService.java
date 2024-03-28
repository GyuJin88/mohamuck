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



    /*
    @Transactional
    public boolean modifyLikeStatus(UsersEntity users, int rid) throws Exception {
        // recipeService를 통해 아이디에 대한 엔티티 객체를 받아옴
        RecipeEntity currentRecipe = recipeRepository.findByRid(rid);

        //DB에서 해당 회원이 좋아요를 누른 상태인지 확인
        if (existLikeFlag(users, currentRecipe)) {
            //좋앙요를 누른 상태일 경우
            //좋아요 취소를 위해 dB에서 삭제
            LikesEntity currentRecipeLike = likeRepository.findByUsersAndRecipe(users, currentRecipe);
            likeRepository.delete(currentRecipeLike);
            return false;
        } else {
            //좋아요를 누르지 않았을 경우
            //dB에 저장
            createNewLike(users, currentRecipe);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean existLikeFlag(UsersEntity users, RecipeEntity recipe) throws Exception {
        return likeRepository.existsByUsersAndRecipe(users, recipe);
    }

    @Transactional
    public void createNewLike(UsersEntity users, RecipeEntity currentRecipe) throws Exception {
        LikesEntity likesEntity = new LikesEntity(users, currentRecipe);
        likeRepository.save(likesEntity);

    }

     */

}
