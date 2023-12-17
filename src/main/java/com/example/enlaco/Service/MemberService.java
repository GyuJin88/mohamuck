package com.example.enlaco.Service;

import com.example.enlaco.Constant.Role;
import com.example.enlaco.DTO.MemberDTO;
import com.example.enlaco.DTO.RecipeDTO;
import com.example.enlaco.DTO.StorageDTO;
import com.example.enlaco.Entity.MemberEntity;
import com.example.enlaco.Entity.RecipeEntity;
import com.example.enlaco.Entity.StorageEntity;
import com.example.enlaco.Repository.MemberRepository;
import com.example.enlaco.Repository.RecipeRepository;
import com.example.enlaco.Repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final RecipeRepository recipeRepository;
    private final StorageRepository storageRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper = new ModelMapper();

    //로그인 처리
    @Override
    public UserDetails loadUserByUsername(String memail) throws UsernameNotFoundException {
        //이메일로 회원 조회
        MemberEntity memberEntity = memberRepository.findByMemail(memail);

        if (memberEntity == null) {
            throw new UsernameNotFoundException(memail);
        }

        //보안 인증에서 해당 데이터로 로그인 처리
        return User.builder()
                .username(memberEntity.getMemail())
                .password(memberEntity.getMpwd())
                .roles(memberEntity.getRole().toString())
                .build();
    }

    //로그인 시 회원번호 전달
    public MemberDTO loginId(String memail, String mpwd) throws Exception {
        MemberEntity member = memberRepository.findByMemail(memail);
        MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);

        return memberDTO.builder()
                .mid(member.getMid())
                .memail(member.getMemail())
                .mpwd(member.getMpwd())
                .build();
    }

    //회원가입 처리
    public void saveMember(MemberDTO memberDTO) throws Exception {
        //비밀번호 암호화 처리
        String password = passwordEncoder.encode(memberDTO.getMpwd());
        //MemberEntity에 저장
        MemberEntity memberEntity = MemberEntity.builder()
                .memail(memberDTO.getMemail())
                .mnick(memberDTO.getMnick())
                .mpwd(password)
                .mphone(memberDTO.getMphone())
                .mbirth(memberDTO.getMbirth())
                .role(Role.USER)
                .build();

        validateDuplicateMember(memberEntity);  //저장할 데이터를 중복체크(신규회원 등록시에만)
        memberRepository.save(memberEntity);
    }

    //회원 정보 조회
    public MemberDTO read(Integer mid) throws Exception {
        Optional<MemberEntity> memberEntity = memberRepository.findById(mid);

        MemberDTO memberDTO = modelMapper.map(memberEntity, MemberDTO.class);

        return memberDTO;
    }

    //회원 수정
    public void modifyMember(MemberDTO memberDTO, String memail) throws Exception {
        //int mid = memberRepository.findByMemail(memail).getMid();
        Optional<MemberEntity> data = memberRepository.findById(memberDTO.getMid());
        MemberEntity member = data.orElseThrow();

        MemberEntity update = modelMapper.map(memberDTO, MemberEntity.class);
        update.setMid(memberDTO.getMid());
        //update.setMemail(memberDTO.getMemail());
        //update.setMpwd(memberDTO.getMpwd());
        update.setMnick(memberDTO.getMnick());
        update.setMphone(memberDTO.getMphone());
        update.setMbirth(memberDTO.getMbirth());

        memberRepository.save(update);
    }

    //이메일 중복 체크
    private void validateDuplicateMember(MemberEntity memberEntity) {
        //데이터베이스에서 조회
        MemberEntity findMember = memberRepository.findByMemail(memberEntity.getMemail());

        if (findMember != null) {  //이메일이 존재하면
            throw new IllegalStateException("이미 가입된 회원입니다.");  //오류발생
        }
    }

    //마이페이지조회
    public List<RecipeDTO> list(Integer mid) throws Exception {
        List<RecipeEntity> recipeEntities = recipeRepository.findByMid(mid);

        List<RecipeDTO> recipeDTOS = Arrays.asList(modelMapper.map(recipeEntities, RecipeDTO[].class));

        return recipeDTOS;
    }
    //냉장고 조회
    public List<StorageDTO> storageList(Integer mid) throws Exception {
        List<StorageEntity> storageEntities = storageRepository.findByMid(mid);

        List<StorageDTO> storageDTOS = Arrays.asList(modelMapper.map(storageEntities, StorageDTO[].class));

        return storageDTOS;
    }


    /*public Page<RecipeDTO> myList(int mid, Pageable pageable) throws Exception {
        int curPage = pageable.getPageNumber()-1;
        int pageLimit = 10;

        Pageable newPage = PageRequest.of(curPage, pageLimit);

        Page<RecipeEntity> recipeEntities = memberRepository.myList(mid, newPage);

        Page<RecipeDTO> recipeDTOS = recipeEntities.map(data-> RecipeDTO.builder()
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
                .mid(data.getMemberEntity().getMid())
                .build());

        return recipeDTOS;
    }*/

    //개별조회
    public MemberDTO detail(int mid) throws Exception {
        Optional<MemberEntity> member = memberRepository.findById(mid);

        MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);

        return memberDTO;
    }

    //이메일로 조회해서 mid 값 뱉어주기
    public int findByMemail1(String memail) throws Exception {
        MemberEntity member = memberRepository.findByMemail(memail);
        int mid = member.getMid();

        return mid;
    }
    //storage용 mid값 뱉어주기
    public int findByMemail2(String memail) throws Exception {
        MemberEntity member = memberRepository.findByMemail1(memail);
        int mid = member.getMid();

        return mid;
    }

    //댓글 작성자에 닉네임을 넣기 위해 이메일로 조회해서 닉네임을 뱉어주기
    public String nickname(String memail) throws Exception {
        MemberEntity member = memberRepository.findByMemail1(memail);
        String nickname = member.getMnick();

        return nickname;
    }
}
