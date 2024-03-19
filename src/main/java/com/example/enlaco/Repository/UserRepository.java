package com.example.enlaco.Repository;

import com.example.enlaco.Entity.MemberEntity;
import com.example.enlaco.Entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmailAndOauthType(String email, String oauthType);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUserid(Integer userid);
}
