

package com.fayezcoder.write.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.fayezcoder.write.data.DataMigrator
import com.fayezcoder.write.ui.NotepadComposeApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint class NotepadActivity: ComponentActivity() {
  @Inject lateinit var migrator: DataMigrator

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycleScope.launch {
      migrator.migrate()
      setContent {
        NotepadComposeApp()
      }
    }
  }
}