# Project-specific ProGuard rules

# We optimize, so we obfuscate ; to be able to read
# debug stack trace, we need this mapping
-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile

# We save sources using their class, and instanciate
# them using reflection : we need their class to stay
# the same
-keep public class * extends v.blade.sources.Source
-keepclassmembers public class * extends v.blade.sources.Source
{
    public static final int NAME_RESOURCE;
    public static final int DESCRIPTION_RESOURCE;
    public static final int IMAGE_RESOURCE;
}
-keep class v.blade.sources.spotify.Spotify$SpotifyTokenResponse {*;}
-keep class v.blade.sources.spotify.SpotifyService$* {*;}
-keep class v.blade.sources.deezer.Deezer$DeezerTokenResponse {*;}
-keep class v.blade.sources.deezer.Deezer$DeezerErrorObject {*;}

# The spotify librespot player needs an 'output class'
# for it's audio output ; we need to keep that
-keep class v.blade.sources.spotify.SpotifyPlayer$BladeSinkOutput

# librespot needs reflexion to work internally
# Here we keep the classes it needs
-keep class xyz.gianlu.librespot.mercury.MercuryRequests$GenericJson
{
    <init>(com.google.gson.JsonObject); # method <init> i.e. constructor
}
-keep class xyz.gianlu.librespot.json.GenericJson
{
    <init>(com.google.gson.JsonObject); # method <init> i.e. constructor
}
-keep class xyz.gianlu.librespot.mercury.MercuryRequests$ResolvedContextWrapper
{
    <init>(com.google.gson.JsonObject); # method <init> i.e. constructor
}
-keep class xyz.gianlu.librespot.json.ResolvedContextWrapper
{
    <init>(com.google.gson.JsonObject); # method <init> i.e. constructor
}
-keep class com.spotify.** {*;}
-keep class xyz.gianlu.librespot.audio.decoders.** {*;}

# We load settings fragment using reflexion
# We need to keep the SettingsActivity class for that
-keep class v.blade.ui.SettingsActivity$AboutFragment
-keep class v.blade.ui.SettingsActivity$SourcesFragment

# For lyrics, we use Genius API, as a retrofit service : reflexion
-keep class v.blade.ui.GeniusService$* {*;}

# OkHttp version < 5.0 warns for this missing ; this is
# a bug (https://github.com/square/okhttp/issues/6258)
# We can safely ignore (those classes are not needed on Android)
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
