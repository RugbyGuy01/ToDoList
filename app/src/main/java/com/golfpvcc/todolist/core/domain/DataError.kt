package com.golfpvcc.todolist.core.domain

sealed interface DataError : Error {
    enum class Local : DataError {
        NOT_FOUND,
        UNKNOWN
    }
}