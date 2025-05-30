package io.github.dhslrl321.oassample

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import java.time.Instant


@Configuration
class LocalSecurityAuthConfig {
  /**
   * JWT 디코더를 mock으로 대체
   */
  @Bean
  fun jwtDecoder(): JwtDecoder {
    return JwtDecoder { token ->
      Jwt.withTokenValue(token)
        .header("alg", "none")
        .claim("sub", "mock-user")
        .claim("roles", listOf("ROLE_USER"))
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(3600))
        .build()
    }
  }

  @Bean
  fun mockJwtConverter(): JwtAuthenticationConverter {
    return JwtAuthenticationConverter().apply {
      setJwtGrantedAuthoritiesConverter {
        emptyList() // 권한 없음
      }
    }
  }

  @Bean
  fun userDetailsService(): UserDetailsService {
    val user: UserDetails = User.withUsername("user")
      .password("password") // {noop} → 비밀번호 암호화 안 함
      .roles("USER")
      .build()

    return InMemoryUserDetailsManager(user)
  }

  @Bean
  fun passwordEncoder(): PasswordEncoder {
    // 암호에 암호화나 해시를 적용하지 않고 일반 텍스트처럼 처리해주는 인스턴스
    return NoOpPasswordEncoder.getInstance()
  }
}
