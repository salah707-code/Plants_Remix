package com.example.plantencyclopedia.security

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

class SecurityManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("al_yenboot_security", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SALT = "pin_salt"
        private const val KEY_SECURITY_ENABLED = "security_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_AUTO_LOCK_TIME = "auto_lock_time" // seconds
    }

    fun isSecurityEnabled(): Boolean {
        return prefs.getBoolean(KEY_SECURITY_ENABLED, false) && prefs.getString(KEY_PIN_HASH, null) != null
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun setPin(pin: String) {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        val saltString = Base64.getEncoder().encodeToString(salt)
        val hash = hashPin(pin, salt)

        prefs.edit()
            .putString(KEY_PIN_SALT, saltString)
            .putString(KEY_PIN_HASH, hash)
            .putBoolean(KEY_SECURITY_ENABLED, true)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        val saltString = prefs.getString(KEY_PIN_SALT, null) ?: return false
        val savedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val salt = Base64.getDecoder().decode(saltString)
        val currentHash = hashPin(pin, salt)
        return currentHash == savedHash
    }

    fun disableSecurity() {
        prefs.edit()
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_SALT)
            .putBoolean(KEY_SECURITY_ENABLED, false)
            .apply()
    }

    private fun hashPin(pin: String, salt: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val digest = md.digest(pin.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(digest)
    }
}
