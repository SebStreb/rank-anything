package com.sebstreb.rank_anything.backend.controllers

import com.sebstreb.rank_anything.backend.models.dtos.ChangePassword
import com.sebstreb.rank_anything.backend.models.dtos.PublicUser
import com.sebstreb.rank_anything.backend.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping("", "/")
    @PreAuthorize("isAuthenticated()")
    fun getUsers(): List<PublicUser> = userService.getUsers()

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun getUser(@PathVariable id: Long): PublicUser? =
        userService.getUser(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @PatchMapping("/{id}/admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    fun setAdmin(@PathVariable id: Long) {
        if (!userService.userExists(id)) throw ResponseStatusException(HttpStatus.NOT_FOUND)
        userService.setAdmin(id)
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    fun changePassword(
        @PathVariable id: Long,
        @RequestBody changePassword: ChangePassword,
        @AuthenticationPrincipal user: PublicUser
    ) {
        if (!userService.userExists(id)) throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (id != user.id && user.role != "admin") throw ResponseStatusException(HttpStatus.FORBIDDEN)
        if (!userService.checkPassword(
                id,
                changePassword.oldPassword
            )
        ) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        if (!userService.validatePassword(changePassword.newPassword)) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        userService.changePassword(id, changePassword.newPassword)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    fun deleteUser(@PathVariable id: Long, @AuthenticationPrincipal user: PublicUser) {
        if (!userService.userExists(id)) throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (id != user.id && user.role != "admin") throw ResponseStatusException(HttpStatus.FORBIDDEN)
        userService.deleteUser(id)
    }

}