package com.example.enlaco.StorageTest;

import com.example.enlaco.DTO.StorageDTO;
import com.example.enlaco.Entity.StorageEntity;
import com.example.enlaco.Entity.UsersEntity;
import com.example.enlaco.Repository.StorageRepository;
import com.example.enlaco.Repository.UsersRepository;
import com.example.enlaco.Service.StorageService;
import com.example.enlaco.Util.S3Uploader;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
public class StorageServiceTest {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private StorageRepository storageRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private S3Uploader s3Uploader;

    @Autowired
    private StorageService storageService;

    @Test
    public void insertTokenTest(Integer userid, StorageDTO storageDTO, MultipartFile imgFile) throws Exception {
        UsersEntity user = usersRepository.findByUserid(userid);
        //.orElseThrow(() -> new RuntimeException("Member not found with id: " + userid));


        //storageDTO.setUserid(user.getUserid());
        storageDTO.setMid(-1); //필요없는 폼로그인은 -1로 저장
        storageDTO.setRid(0);
        storageDTO.setSimg(null);


        StorageEntity storage = modelMapper.map(storageDTO, StorageEntity.class);

        storage.setUsersEntity(user);
        storageRepository.save(storage);
    }


}
