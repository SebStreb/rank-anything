package com.sebstreb.rank_anything.backend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class BcryptConfiguration {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

}