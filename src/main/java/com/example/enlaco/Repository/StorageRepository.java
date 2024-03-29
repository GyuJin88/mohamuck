//2023-11-15 정선아 : findByMid 메소드 생성
package com.example.enlaco.Repository;

import com.example.enlaco.DTO.StorageDTO;
import com.example.enlaco.Entity.MemberEntity;
import com.example.enlaco.Entity.StorageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<StorageEntity, Integer> {
    //Mid로 조회해서 목록으로 가져오기
    @Query(value = "SELECT * FROM Storage WHERE mid=:mid or userid=:userid", nativeQuery = true)
    List<StorageEntity> findById(@Param("mid") Integer mid, @Param("userid") Integer userid);


    @Query(value = "SELECT * FROM Storage s JOIN Member m ON s.mid = m.mid", nativeQuery = true)
    List<StorageEntity> findByMid(@Param("mid") Integer mid);

    @Query(value = "SELECT * FROM Storage s JOIN Users u ON s.userid = u.userid WHERE u.email = :email", nativeQuery = true)
    List<StorageEntity> findByEmail(@Param("email") String email);

    @Query(value = "SELECT s.* FROM Storage s JOIN User u ON s.userid = u.userid", nativeQuery = true)
    List<StorageEntity> findByUid(@Param("userid") Integer userid);

    //@Query(value = "SELECT s.* FROM Storage s, Member m WHERE s.mid = m.mid IN (SELECT mid FROM Member m WHERE m.memail =:memail)", nativeQuery = true)
    @Query(value = "SELECT s.* FROM Storage s, Member m WHERE s.mid = m.mid AND m.memail IN (SELECT m.memail FROM Member m WHERE m.memail = :memail)", nativeQuery = true)
    List<StorageEntity> findByMidOnStorage(@Param("memail") String email);

    @Query(value = "SELECT s.* FROM Storage s, Users u WHERE s.userid = u.userid AND u.email IN (SELECT u.email FROM Users u WHERE u.email = :uemail)", nativeQuery = true)
    List<StorageEntity> findByUseridOnStorage(@Param("uemail") String email);

    @Query(value = "SELECT s.* FROM Storage s, Member m WHERE s.sid = s.mid AND s.mid = m.mid AND m.memail IN (SELECT m.memail FROM Member m WHERE m.memail = :memail)", nativeQuery = true)
    StorageEntity findBySidByMid(@Param("memail") String email);

    @Query(value = "SELECT s.* FROM Storage s, Users u WHERE s.sid = s.suserid AND s.userid = u.userid AND u.email IN (SELECT u.email FROM Users u WHERE u.email = :uemail)", nativeQuery = true)
    StorageEntity findBySidByUserid(@Param("uemail") String email);


    @Query(value = "SELECT * FROM StorageEntity WHERE memail=:memail", nativeQuery = true)
    StorageEntity findByMemail1(String memail);

    /*
    //userid로 조회해서 목록에 가져오기
    @Query(value = "SELECT * FROM StorageEntity WHERE userid.id =:id", nativeQuery = true)
    List<StorageEntity> findByUserId(@Param("userid") Integer id);

     */

}


