package com.CloseConnect.closeconnect.service.member;

import com.CloseConnect.closeconnect.dto.member.LocationDto;
import com.CloseConnect.closeconnect.dto.member.MemberResponseDto;
import com.CloseConnect.closeconnect.entity.member.AuthProvider;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import com.CloseConnect.closeconnect.global.exception.BusinessException;
import com.CloseConnect.closeconnect.global.exception.ExceptionCode;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import com.CloseConnect.closeconnect.security.oatuh2.OAuth2UserInfo;
import com.CloseConnect.closeconnect.security.oatuh2.OAuth2UserInfoFactory;
import com.CloseConnect.closeconnect.security.oatuh2.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        return processOAuth2User(userRequest, oAuth2User);
    }

    // OAuth2 사용자 정보 처리
    protected OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // OAuth2 인증 제공자 확인
        AuthProvider authProvider = AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        // OAuth2 사용자 정보 생성
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider, oAuth2User.getAttributes());

        // 이메일이 없으면 예외 발생
        if (!StringUtils.hasText(userInfo.getEmail())) {
            throw new BusinessException(ExceptionCode.OAUTH2_EMAIL_NOT_FOUND, null);
        }
        // OAuth2 사용자 정보로 회원 조회
        Member member = memberRepository.findByEmail(userInfo.getEmail()).orElse(null);

        // 회원이 존재하지 않으면 회원 등록, 존재하면 회원 정보 업데이트
        if (member != null) {
            if (!member.getAuthProvider().equals(authProvider)) {
                throw new BusinessException(ExceptionCode.ALREADY_SIGNED_UP, member.getEmail());
            }
            member.login();
            updateUser(member, userInfo);
        } else {
            member = registerUser(authProvider, userInfo);
        }

        // 회원 정보를 이용하여 UserPrincipal 객체 생성 및 반환
        return UserPrincipal.create(member, userInfo.getAttributes());
    }

    // 회원 등록
    private Member registerUser(AuthProvider authProvider, OAuth2UserInfo userInfo) {
        Member member = Member.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .oauth2Id(userInfo.getOAuth2Id())
                .authProvider(authProvider)
                .role(Role.USER)
                .activityYn("Y")
                .build();
        member.login();
        return memberRepository.save(member);
    }

    //회원 정보 업데이트
    private void updateUser(Member member, OAuth2UserInfo userInfo) {
        member.update(userInfo);
        memberRepository.save(member);
    }

    public void updateCoordinate(String email, LocationDto locationDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_EXIST_MEMBER, email));
        member.updateCoordinate(locationDto.getLatitude(), locationDto.getLongitude());
        memberRepository.save(member);
    }

    public Page<MemberResponseDto.ResponseDto> getNearbyMemberList(String email, LocationDto locationDto, Pageable pageable) {
        return memberRepository.findNearbyMemberList(locationDto.getLatitude(), locationDto.getLongitude(), locationDto.getRadius(), email, pageable);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto.ResponseDto getMyInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_EXIST_MEMBER, email));
        return MemberResponseDto.ResponseDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .latitude(member.getLocation() != null ? member.getLocation().getY() : null)
                .longitude(member.getLocation() != null ? member.getLocation().getX() : null)
                .build();
    }

    @Transactional(readOnly = true)
    public double getDistanceBetweenMembers(String myEmail, String otherEmail) {
        Double distanceBetweenMembers = memberRepository.findDistanceBetweenMembers(myEmail, otherEmail);
        return distanceBetweenMembers == 0.0 ? 0.1 : distanceBetweenMembers;
    }
}
