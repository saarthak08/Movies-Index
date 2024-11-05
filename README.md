## Movies Index

> An android application which shows the list & information of movies
> from [The Movie Database](https://www.themoviedb.org) (TMDb) & allows to download their torrent
> files from [YIFY](https://yts.mx/).

### APK Link:

https://play.google.com/store/apps/details?id=com.sg.moviesindex

### Features:

- Download torrent files of movies in different resolutions from YIFY.
- Search any movie & get its information: Original Title, Original Language, Genre, Rating, Release
  Date, Runtime, Status, Synposis, Casts & Reviews.
- Get a list of Most Popular Movies of the current time on TMDb.
- Get a list of Top Rated Movies on TMDb.
- Get a list of Upcoming Movies & Now Playing Movies (Movies Currently Playing in Cinemas).
- Discover movies according to different genres.
- Mark a movie as favourite & save it for future preferences.

### Libraries Used:

- [Glide](https://github.com/bumptech/glide)
- [Retrofit](https://github.com/square/retrofit)
- [OkHttp](https://github.com/square/okhttp)
- [Material Dialogs](https://github.com/afollestad/material-dialogs)
- [Room](https://developer.android.com/topic/libraries/architecture/room)
- [SparkButton](https://github.com/varunest/SparkButton)
- [LoadingAndroidButton](https://github.com/leandroBorgesFerreira/LoadingButtonAndroid)
- [CircularProgressIndicator](https://github.com/antonKozyriatskyi/CircularProgressIndicator)
- [MVVM, Live Data & Data Binding (Android Architectural Components)](https://developer.android.com/topic/libraries/architecture)
- [RxJava](https://github.com/ReactiveX/RxJava)

### Note:

In order to build the project, create a folder named `keys` in the root directory of the project.
Then, add a file `key.properties` with following content:
```
storePassword=android
keyPassword=androiddebugkey
keyAlias=android
storeFile=keystore
apiKey=\"YOUR_OWN_API_KEY\"
```
Replace the value of `apiKey` with your own API key value in order to make the app work correctly.
If you want to create a release build of the app, replace the values of `storePassword, keyPassword, keyAlias, storeFile` with your own keystore values.
