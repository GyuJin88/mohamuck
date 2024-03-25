package com.example.enlaco.Repository;

import com.example.enlaco.DTO.RecipeDTO;
import com.example.enlaco.DTO.StorageDTO;
import com.example.enlaco.Entity.RecipeEntity;
import com.example.enlaco.Entity.StorageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Integer> {
    //조회수
    @Modifying
    @Query(value = "UPDATE recipe u SET  u.rviewcnt=u.rviewcnt+1 WHERE u.rid=:rid",
    nativeQuery = true)
    public void rviewcnt(@Param("rid") Integer rid);

    //좋아요
    @Modifying
    @Query(value = "UPDATE RecipeEntity u SET u.rgoodcnt=u.rgoodcnt+1 WHERE u.rid=:rid")
    public void rgoodcnt(int rid);

    //제목, 내용, 식재료로 검색
    /*@Query("SELECT u FROM GuestbookEntity u WHERE u.title like %:keyword% or u.content like %:keyword% or u.writer like %:keyword%")*/
    @Query("SELECT u FROM RecipeEntity u WHERE u.rmenu like %:keyword% or u.rcontent like %:keyword% or u.rselect like %:keyword%")
    Page<RecipeEntity> searchRecipe(String keyword, Pageable pageable);

    //조리 시간
    @Query("SELECT u FROM RecipeEntity u WHERE u.rtime =:rtime")
    Page<RecipeEntity> searchRtime(String rtime, Pageable pageable);
    //분류 검색
    @Query("SELECT u FROM RecipeEntity u WHERE u.rclass =:rclass")
    Page<RecipeEntity> searchRclass( String rclass, Pageable pageable);
    //조리 + 분류 검색
    @Query("SELECT u FROM RecipeEntity u WHERE u.rtime =:rtime AND u.rclass =:rclass")
    Page<RecipeEntity> searchRtimeRclass(String rtime, String rclass, Pageable pageable);

    //Mid로 조회해서 목록으로 가져오기
    @Query(value = "SELECT * FROM  Recipe WHERE mid=:mid", nativeQuery = true)
    List<RecipeEntity> findByMid(@Param("mid") Integer mid);

    //Rid로 조회
    RecipeEntity findByRid(int rid);

    //내가 가지고 있는 식재료로 레시피 검색
    @Query(value = "select r.* from Recipe r left join Storage s on s.singre where r.rselect like %:recom%", nativeQuery = true)
    List<RecipeEntity> recipeRecom(String recom);

    @Query(value = "SELECT r FROM RecipeEntity r JOIN StorageEntity s ON r.rselect LIKE CONCAT('%', s.singre, '%') WHERE r.memberEntity.mid=s.memberEntity.mid")
    List<RecipeEntity> findByRecipeRecom();

    //마이페이지에서 폼로그인 시 내가 쓴 레시피 조회
    @Query(value = "SELECT r.* FROM Recipe r, Member m WHERE r.mid = m.mid AND m.memail IN (SELECT m.memail FROM Member m WHERE m.memail =:email)", nativeQuery = true)
    List<RecipeEntity> findByMidOnRecipe(@Param("email") String email);

    @Query(value = "SELECT r.* FROM Recipe r, Member m WHERE r.mid = m.mid AND m.memail IN (SELECT m.memail FROM Member m WHERE m.memail =:email)", nativeQuery = true)
    Page<RecipeEntity> findByRwriterMid(String email, Pageable pageable);

    //마이페이지에서 토큰로그인 시 내가 쓴 레시피 조회
    @Query(value = "SELECT r.* FROM Recipe r, Users u WHERE r.userid = u.userid AND u.email IN (SELECT u.email FROM Users u WHERE u.email =:email)", nativeQuery = true)
    List<RecipeEntity> findByUseridOnRecipe(@Param("email") String email);

    @Query(value = "SELECT r FROM RecipeEntity r, UsersEntity u WHERE r.usersEntity.userid = u.userid AND u.email IN (SELECT u.email FROM UsersEntity u WHERE u.email =:email)", nativeQuery = false)
    Page<RecipeEntity> findByRwriterUserid(String email, Pageable pageable);








}
