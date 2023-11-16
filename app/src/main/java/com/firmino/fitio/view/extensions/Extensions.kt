package com.firmino.fitio.view.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import com.firmino.fitio.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.capitalizeAndFormat(minSize: Int = 0) = if (this.length > minSize) this.trim().lowercase()
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    .replace("_", " ") else this

fun getImageByName(name: String): Int {
    return when (name) {
        "abdominals" -> R.drawable.ic_muscle_abdominals
        "abductors" -> R.drawable.ic_muscle_abductors
        "adductors" -> R.drawable.ic_muscle_adductors
        "biceps" -> R.drawable.ic_muscle_biceps
        "calves" -> R.drawable.ic_muscle_calves
        "chest" -> R.drawable.ic_muscle_chest
        "forearms" -> R.drawable.ic_muscle_forearms
        "glutes" -> R.drawable.ic_muscle_glutes
        "hamstrings" -> R.drawable.ic_muscle_hamstrings
        "lats" -> R.drawable.ic_muscle_lats
        "lower_back" -> R.drawable.ic_muscle_lower_back
        "middle_back" -> R.drawable.ic_muscle_middle_back
        "neck" -> R.drawable.ic_muscle_neck
        "quadriceps" -> R.drawable.ic_muscle_quadriceps
        "shoulders" -> R.drawable.ic_muscle_shoulders
        "traps" -> R.drawable.ic_muscle_traps
        "triceps" -> R.drawable.ic_muscle_triceps
        else -> R.drawable.ic_fit
    }
}

val allMusclesList = listOf(
    "",
    "abdominals",
    "abductors",
    "adductors",
    "biceps",
    "calves",
    "chest",
    "forearms",
    "glutes",
    "hamstrings",
    "lats",
    "lower_back",
    "middle_back",
    "neck",
    "quadriceps",
    "shoulders",
    "traps",
    "triceps"
)
val allDifficultyList = listOf("", "beginner", "intermediate", "expert")
val allTypesList =
    listOf("", "cardio", "olympic_weightlifting", "plyometrics", "powerlifting", "strength", "stretching", "strongman")

val allRestImages = listOf(
    R.drawable.bg_rest1,
    R.drawable.bg_rest2,
    R.drawable.bg_rest3,
    R.drawable.bg_rest4,
    R.drawable.bg_rest5,
    R.drawable.bg_rest6,
    R.drawable.bg_rest7,
)

fun Long.dateMillisToText(): String {
    val dateFormatter = SimpleDateFormat("kk:mm:ss", Locale.getDefault())
    return dateFormatter.format(Date(this)).toString()

}

fun Long.ticksMillisToText(): String {
    val second: Long = this / 1000 % 60
    val minute: Long = this / (1000 * 60) % 60
    val hour: Long = this / (1000 * 60 * 60) % 24

    return String.format("%02d:%02d:%02d", hour, minute, second)
}

data class AppVersion(
    val versionName: String,
    val versionNumber: Long,
)

fun getAppVersion(context: Context): AppVersion? {
    return try {
        val packageManager = context.packageManager
        val packageName = context.packageName
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
        AppVersion(
            versionName = packageInfo.versionName,
            versionNumber = PackageInfoCompat.getLongVersionCode(packageInfo),
        )
    } catch (e: Exception) {
        null
    }
}
