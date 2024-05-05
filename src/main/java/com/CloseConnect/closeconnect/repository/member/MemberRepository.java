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

    @Query("SELECT new com.CloseConnect.closeconnect.dto.member.MemberResponseDto$ResponseDto(m.id, m.name, m.email, m.isLoggedIn, function('ST_X', m.location) as latitude, function('ST_Y', m.location) as longitude) " +
            "from Member m " +
            "where m.email <> :email and " +
            "function('ST_DISTANCE_SPHERE', " +
            "function('POINT', function('ST_Y', m.location), function('ST_X', m.location))," +
            "function('POINT', :myLongitude, :myLatitude)) <= :radius * 1000")
    Page<MemberResponseDto.ResponseDto> findNearbyMemberList(@Param("myLatitude") double myLatitude,
                                                             @Param("myLongitude") double myLongitude,
                                                             @Param("radius") double radius,
                                                             @Param("email") String email,
                                                             Pageable pageable);

@Query(value = "SELECT ROUND(" +
        "ST_DISTANCE_SPHERE(" +
        "POINT(st_y(m1.location), st_x(m1.location)), " +
        "POINT(st_y(m2.location), st_x(m2.location))) / 1000, 1)" +
        "FROM Member m1, Member m2 " +
        "WHERE m1.email = :myEmail AND m2.email = :otherEmail", nativeQuery = true)
    Double findDistanceBetweenMembers(@Param("myEmail") String myEmail,
                                      @Param("otherEmail")String otherEmail);
}
