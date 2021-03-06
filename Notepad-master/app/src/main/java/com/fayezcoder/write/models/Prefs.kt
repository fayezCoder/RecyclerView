
package com.fayezcoder.write.models

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import de.schnettler.datastore.manager.PreferenceRequest

object Prefs {
  object Theme: PreferenceRequest<String>(
    key = stringPreferencesKey("theme"),
    defaultValue = "light-sans"
  )

  object FontSize: PreferenceRequest<String>(
    key = stringPreferencesKey("font_size"),
    defaultValue = "normal"
  )

  object SortBy: PreferenceRequest<String>(
    key = stringPreferencesKey("sort_by"),
    defaultValue = "date"
  )

  object ExportFilename: PreferenceRequest<String>(
    key = stringPreferencesKey("export_filename"),
    defaultValue = "text-only"
  )

  object ShowDialogs: PreferenceRequest<Boolean>(
    key = booleanPreferencesKey("show_dialogs"),
    defaultValue = false
  )

  object ShowDate: PreferenceRequest<Boolean>(
    key = booleanPreferencesKey("show_date"),
    defaultValue = false
  )

  object DirectEdit: PreferenceRequest<Boolean>(
    key = booleanPreferencesKey("direct_edit"),
    defaultValue = false
  )

  object Markdown: PreferenceRequest<Boolean>(
    key = booleanPreferencesKey("markdown"),
    defaultValue = false
  )
}