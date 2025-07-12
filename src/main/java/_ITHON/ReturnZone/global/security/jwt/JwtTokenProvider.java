package _ITHON.ReturnZone.global.security.jwt;

import _ITHON.ReturnZone.domain.member.dto.res.LoginResponseDto;
import _ITHON.ReturnZone.domain.member.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";

    // 액세스 토큰 유효 시간 (7일)
    private final long accessTokenValidityInSeconds;

    // 리프레시 토큰 유효 시간 (3일)
    private final long refreshTokenValidityInSeconds;

    private final String secret;
    private Key key;

    private final UserDetailsServiceImpl userDetailsService;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secret, UserDetailsServiceImpl userDetailsService) {
        this.secret = secret;

        this.accessTokenValidityInSeconds = 7 * 24 * 60 * 60 * 1000L;

        this.refreshTokenValidityInSeconds = 3 * 24 * 60 * 60 * 1000L;

        this.userDetailsService = userDetailsService;
    }

    public LoginResponseDto generateToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date accessTime = new Date(now + this.accessTokenValidityInSeconds);
        Date refreshTime = new Date(now + this.refreshTokenValidityInSeconds);

        UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        Member member = userDetails.getMember();

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(accessTime)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(refreshTime)
                .compact();

        return LoginResponseDto.builder()
                .member(member)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpires(this.accessTokenValidityInSeconds - 5000)
                .accessTokenExpiresDate(accessTime)
                .build();
    }

    public Authentication getAuthentication(String token) {

        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public boolean validateToken(String token, String tokenType) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            throw new RuntimeException("유효하지 않은 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("잘못된 형식의 JWT입니다.");
        } catch (ExpiredJwtException e) {
            if ("access".equals(tokenType)) {
                throw new RuntimeException("Access 토큰이 만료되었습니다.");
            } else if ("refresh".equals(tokenType)) {
                throw new RuntimeException("Refresh 토큰이 만료되었습니다.");
            } else {
                throw new RuntimeException("만료된 JWT입니다.");
            }
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("지원하지 않는 JWT입니다.");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT가 비어있거나 잘못되었습니다.");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
}
