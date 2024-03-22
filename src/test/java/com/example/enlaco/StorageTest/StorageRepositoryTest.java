package com.example.enlaco.StorageTest;

import com.example.enlaco.Entity.StorageEntity;
import com.example.enlaco.Repository.StorageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class StorageRepositoryTest {
    @Autowired
    private StorageRepository storageRepository;
/*
    @Test
    public void findByMidTest() throws Exception {
        Integer mid;
        List<Object[]> resultList = storageRepository.findSimgSingreAndSYutongByMid(1);
        for (Object[] row : resultList) {
            String simg = (String) row[0];
            String singre = (String) row[1];
            String sYutong = (String) row[2];
            System.out.println("simg: " + simg + ", singre: " + singre + ", sYutong: " + sYutong);
        }

    }

 */

    @Test
    public void findByAllTest() throws Exception {
        System.out.println(storageRepository.findAll().toString());
    }




}
