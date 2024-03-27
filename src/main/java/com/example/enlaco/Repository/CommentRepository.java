package com.example.enlaco.Repository;

import com.example.enlaco.Entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    @Query(value = "SELECT * FROM Comment WHERE recipeid =:rid", nativeQuery = true)
    List<CommentEntity> findByRecipeId(Integer rid);

    @Query(value = "delete from comment where recipeid=:rid",nativeQuery = true)
    void deleteByRecipeId(Integer rid);

    @Query(value = "SELECT c FROM CommentEntity c, UsersEntity u WHERE c.cwriter = u.nickname AND u.email IN (SELECT u.email FROM UsersEntity u WHERE u.email =:email)", nativeQuery = false)
    Page<CommentEntity> findByCwriterUserid(String email, Pageable pageable);
}
