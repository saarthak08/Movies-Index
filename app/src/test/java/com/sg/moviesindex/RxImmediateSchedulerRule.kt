package com.sg.moviesindex

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class RxImmediateSchedulerRule :
  BeforeEachCallback,
  AfterEachCallback {
  override fun beforeEach(context: ExtensionContext) {
    RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }

    RxAndroidPlugins.reset()
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
  }

  override fun afterEach(context: ExtensionContext) {
    RxJavaPlugins.reset()
    RxAndroidPlugins.reset()
  }
}
