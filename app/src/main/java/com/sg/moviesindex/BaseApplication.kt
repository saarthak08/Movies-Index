package com.sg.moviesindex

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Movies Index app.
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation,
 * including a base class for your application that serves as the
 * application-level dependency container.
 */
@HiltAndroidApp
class BaseApplication : Application()
