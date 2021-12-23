

package com.fayezcoder.write.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.util.Base64
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.datastore.preferences.preferencesDataStore
import com.fayezcoder.write.BuildConfig
import java.util.*

fun Context.showToast(
  @StringRes text: Int
) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

val Context.dataStore by preferencesDataStore("settings")

val buildYear: Int get() {
  val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Denver")).apply {
    timeInMillis = BuildConfig.TIMESTAMP
  }

  return calendar.get(Calendar.YEAR)
}

val Context.isPlayStoreInstalled get() = try {
  packageManager.getPackageInfo("com.android.vending", 0)
  true
} catch(e: PackageManager.NameNotFoundException) {
  false
}

val Context.releaseType: ReleaseType
  @Suppress("Deprecation", "PackageManagerGetSignatures")
  get() {
    val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
    for(enum in ReleaseType.values()) {
      try {
        val enumSignature = Signature(Base64.decode(enum.signature, Base64.DEFAULT))
        for(signature in info.signatures) {
          if(signature == enumSignature) return enum
        }
      } catch (ignored: Exception) {}
    }

    return ReleaseType.Unknown
  }
