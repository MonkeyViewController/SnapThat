# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/isaacsiegel/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class !android.support.v7.internal.view.menu.**,android.support.v7.** {*;}
-keep class com.parse.* { *; }
-dontwarn com.parse.**

-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

-dontwarn com.squareup.okhttp.**
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**