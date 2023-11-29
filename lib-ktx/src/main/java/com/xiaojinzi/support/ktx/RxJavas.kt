package com.xiaojinzi.support.ktx

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

inline fun <T> Observable<T>.subscribeOnIOThread() = this.subscribeOn(Schedulers.io())
inline fun <T> Flowable<T>.subscribeOnIOThread() = this.subscribeOn(Schedulers.io())
inline fun <T> Single<T>.subscribeOnIOThread() = this.subscribeOn(Schedulers.io())
inline fun Completable.subscribeOnIOThread() = this.subscribeOn(Schedulers.io())
inline fun <T> Maybe<T>.subscribeOnIOThread() = this.subscribeOn(Schedulers.io())

inline fun <T> Observable<T>.subscribeOnMainThread() = this.subscribeOn(AndroidSchedulers.mainThread())
inline fun <T> Flowable<T>.subscribeOnMainThread() = this.subscribeOn(AndroidSchedulers.mainThread())
inline fun <T> Single<T>.subscribeOnMainThread() = this.subscribeOn(AndroidSchedulers.mainThread())
inline fun Completable.subscribeOnMainThread() = this.subscribeOn(AndroidSchedulers.mainThread())
inline fun <T> Maybe<T>.subscribeOnMainThread() = this.subscribeOn(AndroidSchedulers.mainThread())

inline fun <T> Observable<T>.observeOnIOThread() = this.observeOn(Schedulers.io())
inline fun <T> Flowable<T>.observeOnIOThread() = this.observeOn(Schedulers.io())
inline fun <T> Single<T>.observeOnIOThread() = this.observeOn(Schedulers.io())
inline fun Completable.observeOnIOThread() = this.observeOn(Schedulers.io())
inline fun <T> Maybe<T>.observeOnIOThread() = this.observeOn(Schedulers.io())

inline fun <T> Observable<T>.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())
inline fun <T> Flowable<T>.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())
inline fun <T> Single<T>.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())
inline fun Completable.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())
inline fun <T> Maybe<T>.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())

inline fun <T> Observable<T>.delayMilliSeconds(time: Long) = this.delay(time, MILLISECONDS)
inline fun <T> Flowable<T>.delayMilliSeconds(time: Long) = this.delay(time, MILLISECONDS)
inline fun <T> Single<T>.delayMilliSeconds(time: Long) = this.delay(time, MILLISECONDS)
inline fun <T> Completable.delayMilliSeconds(time: Long) = this.delay(time, MILLISECONDS)
inline fun <T> Maybe<T>.delayMilliSeconds(time: Long) = this.delay(time, MILLISECONDS)

inline fun <T> Observable<T>.delaySeconds(time: Long) = this.delay(time, SECONDS)
inline fun <T> Flowable<T>.delaySeconds(time: Long) = this.delay(time, SECONDS)
inline fun <T> Single<T>.delaySeconds(time: Long) = this.delay(time, SECONDS)
inline fun <T> Completable.delaySeconds(time: Long) = this.delay(time, SECONDS)
inline fun <T> Maybe<T>.delaySeconds(time: Long) = this.delay(time, SECONDS)

inline fun <T> Observable<T>.timeoutSeconds(time: Long) = this.timeout(time, SECONDS)
inline fun <T> Flowable<T>.timeoutSeconds(time: Long) = this.timeout(time, SECONDS)
inline fun <T> Single<T>.timeoutSeconds(time: Long) = this.timeout(time, SECONDS)
inline fun <T> Completable.timeoutSeconds(time: Long) = this.timeout(time, SECONDS)
inline fun <T> Maybe<T>.timeoutSeconds(time: Long) = this.timeout(time, SECONDS)

inline fun <T> Observable<T>.timeoutMilliSeconds(time: Long) = this.timeout(time, MILLISECONDS)
inline fun <T> Flowable<T>.timeoutMilliSeconds(time: Long) = this.timeout(time, MILLISECONDS)
inline fun <T> Single<T>.timeoutMilliSeconds(time: Long) = this.timeout(time, MILLISECONDS)
inline fun <T> Completable.timeoutMilliSeconds(time: Long) = this.timeout(time, MILLISECONDS)
inline fun <T> Maybe<T>.timeoutMilliSeconds(time: Long) = this.timeout(time, MILLISECONDS)

fun <T : Any> Observable<T>.subscribeIgnoreError(
  onError: (Throwable) -> Unit = {},
  onSuccess: (T) -> Unit = {}
): Disposable = subscribe(onSuccess, onError)

fun <T : Any> Flowable<T>.subscribeIgnoreError(
  onError: (Throwable) -> Unit = {},
  onSuccess: (T) -> Unit = {}
): Disposable = subscribe(onSuccess, onError)

fun <T : Any> Single<T>.subscribeIgnoreError(
  onError: (Throwable) -> Unit = {},
  onSuccess: (T) -> Unit = {}
): Disposable = subscribe(onSuccess, onError)

fun Completable.subscribeIgnoreError(
  onError: (Throwable) -> Unit = {},
  onSuccess: () -> Unit = {}
): Disposable = subscribe(onSuccess, onError)

fun <T : Any> Maybe<T>.subscribeIgnoreError(
  onError: (Throwable) -> Unit = {},
  onSuccess: (T) -> Unit = {}
): Disposable = subscribe(onSuccess, onError)

/*An Subject 订阅 target Subject*/
fun <T: Any> Subject<T>.toSubscribe(target: Observable<T>): Disposable = target.subscribe {
  this.onNext(it)
}

//为了让 RxJava 的订阅返回的 Disposable 不要检查返回值, 接触一片黄色的警告
private fun ignoreCheck() {}
fun Disposable.ignoreCheckResult(): Unit = ignoreCheck()