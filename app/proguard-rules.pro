# =========================================================
# KOTLIN SPECIFIC RULES
# =========================================================

# CRITICAL: Keep Kotlin Metadata.
# Kotlin compiler adds this annotation to classes to store Kotlin-specific
# info (like default parameters and nullability). If R8 strips this,
# reflection-based libraries (like Gson/Retrofit) will fail to read your Kotlin classes.
-keep class kotlin.Metadata { *; }

# Preserve line numbers and source files.
# This is especially important for Kotlin because inline functions and Coroutines
# generate a lot of synthetic bytecode. Without this, your crash logs will be unreadable.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# =========================================================
# NETWORK & OKHTTP RULES
# =========================================================
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# =========================================================
# RETROFIT RULES
# =========================================================
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# =========================================================
# MOSHI & DATA MODEL RULES
# =========================================================
# Keeps your model classes from being shrunk/obfuscated so Moshi can parse them via reflection
-keep class com.sg.moviesindex.data.** { *; }

# Moshi specific rules
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}