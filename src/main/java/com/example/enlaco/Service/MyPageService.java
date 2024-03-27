package com.example.enlaco.Service;

import com.example.enlaco.DTO.CommentDTO;
import com.example.enlaco.DTO.RecipeDTO;
import com.example.enlaco.Entity.CommentEntity;
import com.example.enlaco.Entity.RecipeEntity;
import com.example.enlaco.Repository.CommentRepository;
import com.example.enlaco.Repository.MemberRepository;
import com.example.enlaco.Repository.RecipeRepository;
import com.example.enlaco.Repository.UsersRepository;
import com.example.enlaco.Util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {

    //파일이 저장될 경로
    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    private final RecipeRepository recipeRepository;
    private final MemberRepository memberRepository;
    private final UsersRepository usersRepository;
    private final CommentRepository commentRepository;

    private final ModelMapper modelMapper = new ModelMapper();
    //파일 저장을 위한 클래스
    private final S3Uploader s3Uploader;


    //레시피 전체조회
    public Page<RecipeDTO> recieListTokenLogin(String email, Pageable pageable) throws Exception {

        int curPage = pageable.getPageNumber()-1;
        int pageLimit = 5;

        Pageable newPage = PageRequest.of(curPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "rid"));

        //Page<RecipeEntity> recipeUserid = recipeRepository.findByRwriterUserid(email, newPage);
        Page<RecipeEntity> recipeUserid = recipeRepository.findByRwriterUserid(email, newPage);

        Page<RecipeDTO> recipeDTOS = recipeUserid.map(data -> RecipeDTO.builder()
                .rid(data.getRid())
                .rimg(data.getRimg())
                .rmenu(data.getRmenu())
                .rcontent(data.getRcontent())
                .rwriter(data.getRwriter())
                .rclass(data.getRclass())
                .rtime(data.getRtime())
                .rselect(data.getRselect())
                .rviewcnt(data.getRviewcnt())
                .rgoodcnt(data.getRgoodcnt())
                .regDate(data.getRegDate())
                .modDate(data.getModDate())
                .userid(data.getUsersEntity().getUserid())
                /*.mid(data.getMemberEntity().getMid())*/
                .build());

        return recipeDTOS;

    }

    public Page<CommentDTO> commentListTokenLogin(String email, Pageable pageable) throws Exception {

        int curPage = pageable.getPageNumber()-1;
        int pageLimit = 5;

        Pageable newPage = PageRequest.of(curPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "cid"));

        Page<CommentEntity> commentUserid = commentRepository.findByCwriterUserid(email, newPage);

        Page<CommentDTO> commentDTOS = commentUserid.map(data -> CommentDTO.builder()
                .cid(data.getCid())
                .regDate(data.getRegDate())
                .cbody(data.getCbody())
                .cwriter(data.getCwriter())
                .recipeid(data.getRecipeEntity().getRid())
                .build());

        return commentDTOS;

    }



}
