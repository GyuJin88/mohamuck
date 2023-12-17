package com.example.enlaco.Repository;

import com.example.enlaco.Entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    @Query(value = "SELECT * FROM Comment WHERE recipeid =:rid", nativeQuery = true)
    List<CommentEntity> findByRecipeId(Integer rid);

    @Query(value = "delete from comment where recipeid=:rid",nativeQuery = true)
    void deleteByRecipeId(Integer rid);

}
