package com.CloseConnect.closeconnect.repository.member;

import com.CloseConnect.closeconnect.dto.member.MemberResponseDto;
import com.CloseConnect.closeconnect.entity.member.AuthProvider;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("mysql")
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void clean() {
        memberRepository.deleteAll();
    }

    @Test
    public void findByEmailTest() throws Exception {
        //given
        String email = "test@test.com";
        Member member = new Member("testName", email, "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        memberRepository.save(member);

        //when
        Optional<Member> foundMember = memberRepository.findByEmail(email);

        //then
        assertThat(foundMember.isPresent()).isTrue();
        assertThat(foundMember.get().getName()).isEqualTo(member.getName());
        assertThat(foundMember.get().getOauth2Id()).isEqualTo(member.getOauth2Id());
    }

    @Test
    public void findNearbyMemberListTest() throws Exception {
        //given
        double myLatitude = 37.52602;
        double myLongitude = 126.9359;
        double radius = 1;
        String email = "test@test.com";
        Pageable pageable = PageRequest.of(0, 10);

        Member member = new Member("testName", email, "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        member.updateCoordinate(myLatitude, myLongitude);
        memberRepository.save(member);

        Member member2 = new Member("testName2", "test2@test.com", "testOAuth2Id2", AuthProvider.KAKAO, Role.USER, "Y");
        member2.updateCoordinate(37.5260179, 126.9359210);
        memberRepository.save(member2);

        Member member3 = new Member("testName3", "test3@test.com", "testOAuth2Id3", AuthProvider.NAVER, Role.USER, "Y");
        member3.updateCoordinate(37.52608, 126.9356);
        memberRepository.save(member3);

        Member member4 = new Member("testName4", "test4@test.com", "testOAuth2Id4", AuthProvider.NAVER, Role.USER, "Y");
        member4.updateCoordinate(37.51770, 127.0077);
        memberRepository.save(member4);

        //when
        Page<MemberResponseDto.ResponseDto> nearbyMemberList = memberRepository.findNearbyMemberList(myLatitude, myLongitude, radius, email, pageable);

        //then
        assertThat(nearbyMemberList.getTotalElements()).isNotZero();
        assertThat(nearbyMemberList.getContent().size()).isEqualTo(2);
    }

    @Test
    public void findDistanceBetweenMembersTest() throws Exception {
        //given
        double myLatitude = 37.52951;
        double myLongitude = 126.8446;
        double otherLatitude = 37.529525;
        double otherLongitude = 126.8455;

        String myEmail = "test1@test.com";
        String otherEmail = "test2@test.com";

        Member member = new Member("testName", myEmail, "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        member.updateCoordinate(myLatitude, myLongitude);
        memberRepository.save(member);

        Member member2 = new Member("testName2", otherEmail, "testOAuth2Id2", AuthProvider.KAKAO, Role.USER, "Y");
        member2.updateCoordinate(otherLatitude, otherLongitude);
        memberRepository.save(member2);

        //when
        Double distanceBetweenMembers = memberRepository.findDistanceBetweenMembers(myEmail, otherEmail);

        //then
        if(distanceBetweenMembers == 0.0) distanceBetweenMembers = 0.1;
        System.out.println("distanceBetweenMembers = " + distanceBetweenMembers);
    }
}











