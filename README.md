# Movies Index

> An Android application that showcases movie information from [The Movie Database](https://www.themoviedb.org) (TMDb) and enables downloading torrent files and magnet links from [YTS](https://yts.mx/).

---

## Features

- **Torrent & Magnet Links**:
  - Download torrent files across multiple resolutions (720p, 1080p, 2160p 4K, 3D).
  - Open magnet links directly in installed BitTorrent clients (e.g., Flud, LibreTorrent).
  - Copy magnet URIs directly to the clipboard.
- **Dark & Light Mode**:
  - Full Day/Night Material theme support with rich deep purple accents.
  - Dedicated one-tap theme toggle button in the toolbar with persistent user preferences.
- **Comprehensive Movie Information**:
  - Real-time rating indicator, release status, runtime, original language, synopsis, and genres.
  - High-resolution poster and backdrop artwork.
  - Native YouTube trailer integration.
  - **Where to Watch**: Streaming, rental, and purchase providers powered by TMDb watch providers.
  - **More Like This**: Smart movie recommendations presented alongside cast and reviews.
- **Cast & Crew Filmography**:
  - Tap any cast member to open their complete biography, birth details, department, and filmography list.
- **Library (Favorites & Watchlist)**:
  - Save movies to local Favorites with interactive animations.
  - Add upcoming or bookmarked movies to your personal Watchlist.
  - Dual-tabbed Library view backed by Room SQLite database.
- **Browsing & Discover**:
  - Filter movies by categories: Popular, Now Playing, and Top Rated with region selection (Global, India, US, UK).
  - Filter movies by Genre.
  - Debounced real-time movie search suggestions.
- **Sharing**:
  - Share movie titles, ratings, synopses, and TMDb links directly to other apps.

---

## Architecture & Technologies

The application follows the **MVVM (Model-View-ViewModel)** architectural pattern with clean separation of concerns and reactive data streams.

- **[Kotlin](https://kotlinlang.org/)**: 100% Kotlin codebase.
- **[Hilt](https://dagger.dev/hilt/)**: Standardized dependency injection across activities, fragments, services, and repositories.
- **[Room](https://developer.android.com/topic/libraries/architecture/room)**: Offline SQLite persistence for Favorites and Watchlist with type converters and schema migrations.
- **[Retrofit 2](https://github.com/square/retrofit) & [OkHttp 3](https://github.com/square/okhttp)**: Type-safe REST client for TMDb and YTS APIs with network logging.
- **[Moshi](https://github.com/square/moshi)**: Modern JSON parsing and serialization.
- **[RxJava 2](https://github.com/ReactiveX/RxJava/tree/2.x)**: Asynchronous reactive streams and query debouncing.
- **[Glide](https://github.com/bumptech/glide)**: Fast and efficient image loading with cross-fade animations and error fallbacks.
- **[Android Architecture Components](https://developer.android.com/topic/libraries/architecture)**: `ViewModel`, `LiveData`, `DataBinding`, and `ViewBinding`.
- **[Material Design Components](https://github.com/material-components/material-components-android)**: Day/Night themes, bottom navigation, cards, chips, and dialogs.
- **[Spotless](https://github.com/diffplug/spotless)**: Automated code style formatting via `ktlint`.
- **Unit Testing**: Over 120 unit tests with **JUnit 5** and **MockK** testing ViewModels, Repositories, DAOs, TypeConverters, Services, and Utilities.

---

## How to Build and Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/saarthak08/Movies-Index.git
   cd Movies-Index
   ```

2. **Set up API Keys**:
   Inside the root directory, create a `keys` folder with `key.properties`:
   ```properties
   storePassword=android
   keyPassword=androiddebugkey
   keyAlias=android
   storeFile=keystore
   apiKey="YOUR_OWN_TMDB_API_KEY"
   ```
   *(Obtain a free API key from [The Movie Database](https://www.themoviedb.org/documentation/api)).*

3. **Build and Test**:
   ```bash
   # Run code style formatting check
   ./gradlew spotlessCheck

   # Run all unit tests
   ./gradlew testDevDebugUnitTest

   # Assemble debug APK
   ./gradlew assembleDevDebug
   ```

---

## APK Link

[Download from Google Play Store](https://play.google.com/store/apps/details?id=com.sg.moviesindex)
