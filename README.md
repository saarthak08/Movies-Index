# Movies Index

> An android application which shows the list & information of movies from [The Movie Database](https://www.themoviedb.org) (TMDb) & allows downloading their torrent files from [YTS](https://yts.mx/).

## Features

- **Torrent Downloads**: Download torrent files of movies in different resolutions from YTS.
- **Movie Information**: Search any movie & get its information: Original Title, Original Language, Genre, Rating, Release Date, Runtime, Status, Synopsis, Cast & Reviews.
- **Movie Lists**:
  - Most Popular Movies
  - Top Rated Movies
  - Upcoming Movies
  - Now Playing Movies
- **Discover**: Discover movies according to different genres.
- **Favourites**: Mark a movie as a favourite & save it for future preferences.

## Architecture & Technologies

The application is built using the **MVVM (Model-View-ViewModel)** architectural pattern to provide a clean separation of concerns and a testable codebase. 

Key technologies and libraries used:
- **[Kotlin](https://kotlinlang.org/)**: The application is written entirely in Kotlin.
- **[Hilt](https://dagger.dev/hilt/)**: Used for Dependency Injection, making it easier to provide dependencies across the app.
- **[RxJava 2](https://github.com/ReactiveX/RxJava/tree/2.x)**: For reactive programming and handling asynchronous operations seamlessly.
- **[Retrofit](https://github.com/square/retrofit) & [OkHttp](https://github.com/square/okhttp)**: Used for making robust and type-safe HTTP network requests to the TMDb and YTS APIs.
- **[Moshi](https://github.com/square/moshi)**: A modern JSON library for Android and Kotlin, used to parse JSON responses from APIs.
- **[Room](https://developer.android.com/topic/libraries/architecture/room)**: Provides a local SQLite database abstraction for saving favourite movies offline.
- **[Glide](https://github.com/bumptech/glide)**: For efficient image loading and caching.
- **[Android Architecture Components](https://developer.android.com/topic/libraries/architecture)**: LiveData and Data Binding to ensure the UI state is robustly managed and reactive.

## How to Build and Run

To build the project on your local machine, follow these steps:

1. Clone the repository.
2. Open the project in **Android Studio**.
3. In the root directory of the project, create a folder named `keys`.
4. Inside the `keys` folder, create a file named `key.properties` with the following content:

   ```properties
   storePassword=android
   keyPassword=androiddebugkey
   keyAlias=android
   storeFile=keystore
   apiKey="YOUR_OWN_API_KEY"
   ```

5. Replace `"YOUR_OWN_API_KEY"` with your valid TMDb API key. You can get one by registering at [The Movie Database (TMDb)](https://www.themoviedb.org/documentation/api).
6. Sync the project with Gradle files.
7. Click the **Run** button in Android Studio or use the command `./gradlew assembleDebug` to build the APK.

*Note: If you want to create a release build of the app, replace the values of `storePassword`, `keyPassword`, `keyAlias`, and `storeFile` with your own keystore credentials.*

## APK Link

[Download from Google Play Store](https://play.google.com/store/apps/details?id=com.sg.moviesindex)
