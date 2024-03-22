package com.example.enlaco.Repository;

import com.example.enlaco.Entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, String> {
    Optional<UsersEntity> findByEmailAndOauthType(String email, String oauthType);

    Optional<UsersEntity> findByEmail(String email);

    Optional<UsersEntity> findOptionalByUserid(Integer userid);

    UsersEntity findByUserid(Integer userid);


    UsersEntity findByEmailIgnoreCase(String email);
    //Optional<UsersEntity> findByUserid(Integer userid);
}
