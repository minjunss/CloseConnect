package com.CloseConnect.closeconnect.security.oatuh2.jwt;

import com.CloseConnect.closeconnect.dto.member.MemberResponseDto;
import com.CloseConnect.closeconnect.entity.token.Token;
import com.CloseConnect.closeconnect.global.exception.BusinessException;
import com.CloseConnect.closeconnect.global.exception.ExceptionCode;
import com.CloseConnect.closeconnect.repository.token.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private Long accessTokenExpirationMinutes;

    @Getter
    private final Key key;
    private final TokenRepository tokenRepository;

    public JwtTokenProvider(TokenRepository tokenRepository, @Value("${jwt.key.secret}") String secretKey) {
        this.tokenRepository = tokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //Authentication 가지고 AccessToken 생성하는 메서드
    public MemberResponseDto.TokenInfo generateToken(Authentication authentication) {
        return generateToken(authentication.getName(), authentication.getAuthorities());
    }

    //name, authorities 가지고 AccessToken 생성하는 메서드
    private MemberResponseDto.TokenInfo generateToken(String name, Collection<? extends GrantedAuthority> inputAuthorities) {

        //권한 가져오기
        String authorities = inputAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + accessTokenExpirationMinutes * 60 * 1000);
        //Generate AccessToken
        String accessToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .claim("type", TYPE_ACCESS)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("accessToken = " + accessToken);

        //토큰 db 저장
        Token token = Token.builder()
                .email(name)
                .token(accessToken)
                .expirationTime(expirationDate)
                .build();
        tokenRepository.save(token);

        return MemberResponseDto.TokenInfo.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpirationTime(accessTokenExpirationMinutes)
                .build();
    }

    //JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        //클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        //UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        Token foundToken = tokenRepository.findByToken(token);
        if (foundToken != null && !foundToken.isBlacklisted()) {
            try {
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
                return true;
            } catch (SecurityException | MalformedJwtException e) {
                throw new BusinessException(ExceptionCode.INVALID_JWT_TOKEN, token);
            } catch (ExpiredJwtException e) {
                log.info(e.getMessage());
                throw new BusinessException(ExceptionCode.EXPIRED_JWT_TOKEN, token);
            } catch (UnsupportedJwtException e) {
                log.info(e.getMessage());
                throw new BusinessException(ExceptionCode.UNSUPPORTED_JWT_TOKEN, token);
            } catch (IllegalArgumentException e) {
                log.info(e.getMessage());
                throw new BusinessException(ExceptionCode.EMPTY_JWT_CLAIMS, token);
            }
        } else {
            throw new BusinessException(ExceptionCode.BLACKLIST_TOKEN, token);
        }
    }

    public boolean validateTokenWithServletRequest(HttpServletRequest request, String token) {
        Token foundToken = tokenRepository.findByToken(token);
        if (foundToken != null && !foundToken.isBlacklisted()) {
            try {
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
                return true;
            } catch (SecurityException | MalformedJwtException | IllegalArgumentException | UnsupportedJwtException |
                     ExpiredJwtException e) {
                request.setAttribute("exception", e);
            }
        } else {
            request.setAttribute("exception", new BusinessException(ExceptionCode.BLACKLIST_TOKEN, token));
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    //HTTP 요청에서 JWT 토큰을 추출하는 메서드
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
