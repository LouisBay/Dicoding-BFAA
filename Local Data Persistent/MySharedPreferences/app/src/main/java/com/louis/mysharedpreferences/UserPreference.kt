package com.louis.mysharedpreferences

import android.content.Context

internal class UserPreference(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value: UserModel) {
        preferences.edit().apply {
            putString(NAME, value.name)
            putString(EMAIL, value.email)
            putInt(AGE, value.age)
            putString(PHONE_NUMBER, value.phoneNumber)
            putBoolean(LOVE_MU, value.isLove)
        }.also {
            it.apply()
        }
    }

    fun getUser(): UserModel {
        UserModel().apply {
            with(preferences) {
                name = getString(NAME, "")
                email = getString(EMAIL, "")
                age = getInt(AGE, 0)
                phoneNumber = getString(PHONE_NUMBER, "")
                isLove = getBoolean(LOVE_MU, false)
            }
        }.also {
            return it
        }
    }

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val AGE = "age"
        private const val PHONE_NUMBER = "phone"
        private const val LOVE_MU = "islove"
    }
}