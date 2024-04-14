package com.CloseConnect.closeconnect.repository.member;

import com.CloseConnect.closeconnect.dto.member.MemberResponseDto;
import com.CloseConnect.closeconnect.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    @Query("SELECT new com.CloseConnect.closeconnect.dto.member.MemberResponseDto$ResponseDto(m.id, m.name, m.email, m.isLoggedIn, m.latitude, m.longitude) " +
            "from Member m " +
            "where m.email <> :email and " +
            "function('ST_DISTANCE_SPHERE', " +
            "function('POINT', m.longitude, m.latitude)," +
            "function('POINT', :myLongitude, :myLatitude)) <= :radius * 1000")
    Page<MemberResponseDto.ResponseDto> findNearbyMemberList(@Param("myLatitude") double myLatitude,
                                                             @Param("myLongitude") double myLongitude,
                                                             @Param("radius") double radius,
                                                             @Param("email") String email,
                                                             Pageable pageable);
}
