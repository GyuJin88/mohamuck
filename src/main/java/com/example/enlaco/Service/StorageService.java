package com.example.enlaco.Service;

import com.example.enlaco.DTO.StorageDTO;
import com.example.enlaco.Entity.MemberEntity;
import com.example.enlaco.Entity.StorageEntity;
import com.example.enlaco.Repository.MemberRepository;
import com.example.enlaco.Repository.StorageRepository;
import com.example.enlaco.Util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StorageService {
    //파일이 저장될 경로
    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    private final StorageRepository storageRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    //파일 저장을 위한 클래스
    private final S3Uploader s3Uploader;

    //삭제
    public void remove(int sid, MultipartFile imgFile) throws Exception {
        StorageEntity read = storageRepository.findById(sid).orElseThrow();
        if (imgFile != null) {
            s3Uploader.deleteFile(read.getSimg());
        }
        storageRepository.deleteById(sid);
    }

    //개별조회
    public StorageDTO detail(Integer sid) throws Exception {
        StorageEntity storage = storageRepository.findById(sid).orElseThrow();

        StorageDTO storageDTO = modelMapper.map(storage, StorageDTO.class);

        return storageDTO;
    }

    //전체조회
    public List<StorageDTO> list(int mid, int userid) throws Exception {
        List<StorageEntity> storageEntitiesMid = storageRepository.findById(mid, userid);


        List<StorageDTO> storageDTOS = Arrays.asList(
                modelMapper.map(storageEntitiesMid, StorageDTO[].class));

        return storageDTOS;
    }

    //삽입
    public void insert(int id, StorageDTO storageDTO, MultipartFile imgFile) throws Exception {
        Optional<MemberEntity> data = memberRepository.findById(id);
        MemberEntity member = data.orElseThrow();

        if (imgFile!=null) {
            String originalFileName = imgFile.getOriginalFilename();
            String newFIleName = null;
            if (originalFileName != null) {
                newFIleName = s3Uploader.upload(imgFile, imgUploadLocation);
            }
            storageDTO.setSimg(newFIleName);
        } else {
            storageDTO.setSimg(null);
        }

        StorageEntity storage = modelMapper.map(storageDTO, StorageEntity.class);
        storage.setMemberEntity(member);

        storageRepository.save(storage);
    }
    public void aiInsert(int id, StorageDTO storageDTO, String name) throws Exception {
        Optional<MemberEntity> data = memberRepository.findById(id);
        MemberEntity member = data.orElseThrow();
        storageDTO.setSimg(name);
        StorageEntity storage = modelMapper.map(storageDTO, StorageEntity.class);
        storage.setMemberEntity(member);
        storageRepository.save(storage);
    }

    //수정
    public void modify(StorageDTO storageDTO, String memail,  MultipartFile imgFile) throws Exception {
        int sid = storageDTO.getSid();
        int mid = memberRepository.findByMemail(memail).getMid();

        Optional<StorageEntity> read = storageRepository.findById(sid);
        StorageEntity storage = read.orElseThrow();

        Optional<MemberEntity> data = memberRepository.findById(mid);
        MemberEntity member = data.orElseThrow();

        StorageEntity storageEntity = storageRepository.findById(storageDTO.getSid()).orElseThrow();
        String deleteFile = storageEntity.getSimg();

        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = null;

        if (originalFileName.length() != 0) {
            if (deleteFile != null) {
                s3Uploader.deleteFile(storageEntity.getSimg());
            }

            newFileName = s3Uploader.upload(imgFile, imgUploadLocation);
            storageDTO.setSimg(newFileName);
            //storage.setSimg(newFileName);
        }

        storageDTO.setSid(storage.getSid());  //밑에서 Entity로 한번에 저장*/

        StorageEntity update = modelMapper.map(storageDTO, StorageEntity.class);

        update.setSid(storageDTO.getSid());
        /*update.setSbuydate(storage.getSbuydate());  //구매날짜*/
        update.setSyutong(storageDTO.getSyutong());
        update.setSimg(storageDTO.getSimg());
        update.setSingre(storageDTO.getSingre());
        /*update.setSkeep(storage.getSkeep());  //보관방법*/
        /*update.setSquan(storage.getSquan());  //수량*/
        update.setMemberEntity(member);

        storageRepository.save(update);
    }

    //디데이 구하기
    public long calculateDDay(int sid) {
        // 저장소 데이터 조회
        StorageEntity storage = storageRepository.findById(sid).orElseThrow(() -> new RuntimeException("해당 ID의 저장소를 찾을 수 없습니다."));

        // StorageEntity에서 syutong 날짜 문자열 가져오기
        String syutongDateString = storage.getSyutong(); // getSyutong() 메서드를 통해 날짜 문자열 가져오기

        // 가져온 문자열을 LocalDate로 파싱
        LocalDate syutongDate = LocalDate.parse(syutongDateString);

        // 현재 날짜
        LocalDate currentDate = LocalDate.now();

        // 두 날짜 간의 차이 계산
        return ChronoUnit.DAYS.between(currentDate, syutongDate);
    }


}
