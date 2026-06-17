# Add project specific ProGuard rules here.

# ── App-specific keeps ────────────────────────────────────────────────────────
-keep class com.julian.iwakganas.model.** { *; }
-keep class com.julian.iwakganas.controller.** { *; }

# ── AndroidX / AppCompat ──────────────────────────────────────────────────────
-keep class androidx.appcompat.** { *; }
-keep interface androidx.appcompat.** { *; }
-dontwarn androidx.appcompat.**

# ── ConstraintLayout ─────────────────────────────────────────────────────────
-keep class androidx.constraintlayout.** { *; }
-dontwarn androidx.constraintlayout.**

# ── Android core components (Activities, Services, etc.) ─────────────────────
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# ── View constructors (required for XML inflation) ───────────────────────────
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ── Serializable classes ─────────────────────────────────────────────────────
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ── Enum support ─────────────────────────────────────────────────────────────
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ── Suppress common warnings ─────────────────────────────────────────────────
-dontwarn java.lang.invoke.**
-dontwarn **$$Lambda$*
