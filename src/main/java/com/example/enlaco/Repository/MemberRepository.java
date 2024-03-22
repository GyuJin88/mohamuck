package com.example.enlaco.Repository;

import com.example.enlaco.DTO.MemberDTO;
import com.example.enlaco.Entity.MemberEntity;
import com.example.enlaco.Entity.RecipeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
    //로그인 시 이메일로 조회
    MemberEntity findByMemail(String email);

    @Query("SELECT m FROM MemberEntity m WHERE m.memail = :email")
    Optional<MemberEntity> findByEmail(String email);
    //레시피에서 회원 별로 뽑아내기
    /*@Query(value = "SELECT * FROM Recipe WHERE mid=:mid", nativeQuery = true)
    Page<RecipeEntity> myList(Integer mid, Pageable pageable);*/

    @Query(value = "SELECT * FROM RecipeEntity WHERE memail=:memail", nativeQuery = true)
    MemberEntity findByMemail1(String memail);

/*
    @Query(value = "SELECT * FROM StorageEntity WHERE memail=:memail", nativeQuery = true)
    MemberEntity findByMemail2(String memail);
*/
}
