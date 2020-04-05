package com.epita.pokebattle.methods

import retrofit2.Callback

abstract class CallBackWithArg<T>(arg: String, nb: Int) : Callback<T> {
    var arg: String? = arg
    var nb: Int? = nb
}