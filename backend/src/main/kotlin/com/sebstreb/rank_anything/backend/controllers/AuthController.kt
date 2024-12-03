package com.sebstreb.rank_anything.backend.controllers

import com.sebstreb.rank_anything.backend.models.dtos.Credentials
import com.sebstreb.rank_anything.backend.models.dtos.PublicUser
import com.sebstreb.rank_anything.backend.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
class AuthController(private val userService: UserService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    fun register(@RequestBody credentials: Credentials): PublicUser {
        if (!userService.validateEmail(credentials.email)) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (!userService.validatePassword(credentials.password)) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (userService.userExists(credentials.email)) throw ResponseStatusException(HttpStatus.CONFLICT)
        return userService.registerUser(credentials)
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    fun login(@RequestBody credentials: Credentials): String {
        if (!userService.checkCredentials(credentials)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        return userService.generateToken(credentials.email)
    }

}