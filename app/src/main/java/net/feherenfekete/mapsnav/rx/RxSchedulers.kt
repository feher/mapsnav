package net.feherenfekete.mapsnav.rx

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RxSchedulers @Inject constructor() {

    fun main() = AndroidSchedulers.mainThread()

    fun io() = Schedulers.io()

    fun computation() = Schedulers.computation()

}
