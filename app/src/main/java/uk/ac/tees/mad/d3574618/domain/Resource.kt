package uk.ac.tees.mad.d3574618.domain

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String?, data: T? = null) : Resource<T> (data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}