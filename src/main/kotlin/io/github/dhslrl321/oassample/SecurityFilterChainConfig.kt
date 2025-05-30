package io.github.dhslrl321.oassample

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityFilterChainConfig(
  private val authenticationConverter: JwtAuthenticationConverter
) {

  @Bean
  @Order(0)
  fun publicSecurity(http: HttpSecurity): SecurityFilterChain {
    http
      .securityMatcher(
        "/index.html",
        "/swagger/**",
        "/openapi/**"
      )
      .authorizeHttpRequests {
        it.anyRequest().permitAll()
      }
      .csrf { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(STATELESS) }

    return http.build()
  }

  @Bean
  @Order(1)
  fun externalSecurity(http: HttpSecurity): SecurityFilterChain {
    http
      .securityMatcher("/external/**")
      .authorizeHttpRequests {
        it.anyRequest().authenticated()
      }
      .oauth2ResourceServer {
        it.jwt { jwtConfigurer ->
          jwtConfigurer.jwtAuthenticationConverter(authenticationConverter)
        }
      }
      .csrf { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(STATELESS) }

    return http.build()
  }

  @Bean
  @Order(2)
  fun internalSecurity(http: HttpSecurity): SecurityFilterChain {
    http
      .securityMatcher("/internal/**")
      .authorizeHttpRequests {
        it.anyRequest().authenticated()
      }
      .httpBasic {}
      .csrf { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(STATELESS) }

    return http.build()
  }


}
