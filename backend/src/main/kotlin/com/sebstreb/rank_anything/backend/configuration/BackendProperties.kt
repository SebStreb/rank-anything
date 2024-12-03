package com.sebstreb.rank_anything.backend.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "rank-anything.backend")
data class BackendProperties(var jwtSecret: String = "")