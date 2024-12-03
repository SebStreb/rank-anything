package com.sebstreb.rank_anything.backend.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.sebstreb.rank_anything.backend.configuration.BackendProperties
import com.sebstreb.rank_anything.backend.models.dtos.Credentials
import com.sebstreb.rank_anything.backend.models.dtos.PublicUser
import com.sebstreb.rank_anything.backend.models.entities.User
import com.sebstreb.rank_anything.backend.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val properties: BackendProperties,
) {

    private val jwtVerifier = JWT.require(Algorithm.HMAC256(properties.jwtSecret)).build()

    fun validateEmail(email: String) = email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))
    fun validatePassword(password: String) = password.length >= 8 &&
            password.contains(Regex("[0-9]")) && password.contains(Regex("[A-Z]")) &&
            password.contains(Regex("[a-z]")) && password.contains(Regex("[-+_!@#$%^&*.,?]"))

    fun getUsers(): List<PublicUser> = userRepository.findAll().map { it.toPublicUser() }

    fun getUser(id: Long): PublicUser? = userRepository.findById(id).orElse(null)?.toPublicUser()
    fun getUser(email: String): PublicUser? = userRepository.findByEmail(email)?.toPublicUser()

    fun userExists(id: Long) = userRepository.existsById(id)
    fun userExists(email: String) = userRepository.existsByEmail(email)

    fun registerUser(credentials: Credentials): PublicUser {
        val hashedPassword = passwordEncoder.encode(credentials.password)
        val newUser = User(credentials.email, hashedPassword)
        userRepository.save(newUser)
        return newUser.toPublicUser()
    }

    fun changePassword(id: Long, newPassword: String) {
        val user = userRepository.findById(id).orElse(null) ?: return
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
    }

    fun setAdmin(id: Long) {
        val user = userRepository.findById(id).orElse(null) ?: return
        user.role = "admin"
        userRepository.save(user)
    }

    fun deleteUser(id: Long) = userRepository.deleteById(id)

    fun checkCredentials(credentials: Credentials): Boolean {
        val user = userRepository.findByEmail(credentials.email) ?: return false
        return passwordEncoder.matches(credentials.password, user.password)
    }

    fun checkPassword(id: Long, password: String): Boolean {
        val user = userRepository.findById(id).orElse(null) ?: return false
        return passwordEncoder.matches(password, user.password)
    }

    fun generateToken(email: String): String = JWT.create()
        .withSubject(email)
        .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 days
        .sign(Algorithm.HMAC256(properties.jwtSecret))

    fun verifyToken(token: String) = try {
        jwtVerifier.verify(token).subject
    } catch (e: JWTVerificationException) {
        null
    }

}