# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-renamesourcefileattribute SourceFile
-keepattributes  Signature,SourceFile,LineNumberTable
-keep public class * extends android.app.Application


-keep class bio** { *;}
-keep class kr.co.smartbank.app.service** { *;}
-keep class kr.co.everspin.eversafe.EversafeHelper { *;}
-keep class kr.co.everspin.eversafe.Eversafe { *;}
-keep class kr.co.everspin.eversafe.keypad.* { *;}
-keep class kellinwood.sigblock** { *; }
-keep class sys.util** { *; }
-keep class org.apache.commons.codec** { *; }
-keep class org.json.simple** { *; }
-keep class org.mozilla** { *; }
-keep class kr.co.coocon.sasapi** { *; }
-keep class kr.co.coocon.org** { *; }
-keep class kr.co.coocon.v8** { native <methods>; <fields>; <methods>; }
-keep class com.nprotect** {*;}
-keep class com.lumensoft** {*;}
-keep class com.signkorea** {*;}
-keep class org.jetbrains** {*;}
-keep class com.inzisoft** {*;}

-dontwarn kr.co.coocon.sasapi.**
-dontwarn kr.co.coocon.org.**
-dontwarn org.mozilla.javascript.** -dontwarn sun.**
-dontwarn kr.co.coocon.v8.**