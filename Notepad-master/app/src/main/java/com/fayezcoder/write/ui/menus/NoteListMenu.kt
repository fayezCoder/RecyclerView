

package com.fayezcoder.write.ui.menus

import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.fayezcoder.write.R
import com.fayezcoder.write.android.NotepadViewModel
import com.fayezcoder.write.ui.routes.appSettings
import com.fayezcoder.write.ui.widgets.MoreButton

@Composable fun NoteListMenu(
  navController: NavController?,
  vm: NotepadViewModel?,
  showAboutDialog: MutableState<Boolean>,
  showSettingsDialog: MutableState<Boolean>? = null,
) {
  val showMenu = remember { mutableStateOf(false) }

  Box {
    MoreButton(showMenu)
    DropdownMenu(
      expanded = showMenu.value,
      onDismissRequest = { showMenu.value = false }
    ) {
      showSettingsDialog?.let {
        SettingsDialogMenuItem(showMenu, showSettingsDialog)
      } ?: SettingsMenuItem(showMenu, navController)

      ImportMenuItem(showMenu, vm)
      AboutMenuItem(showMenu, showAboutDialog)
    }
  }
}

@Composable fun SettingsMenuItem(
  showMenu: MutableState<Boolean>,
  navController: NavController?
) {
  DropdownMenuItem(
    onClick = {
      showMenu.value = false
      navController?.appSettings()
    }
  ) {
    Text(text = stringResource(R.string.action_settings))
  }
}

@Composable fun SettingsDialogMenuItem(
  showMenu: MutableState<Boolean>,
  showSettingsDialog: MutableState<Boolean>?
) {
  DropdownMenuItem(
    onClick = {
      showMenu.value = false
      showSettingsDialog?.value = true
    }
  ) {
    Text(text = stringResource(R.string.action_settings))
  }
}

@Composable fun ImportMenuItem(
  showMenu: MutableState<Boolean>,
  vm: NotepadViewModel?
) {
  DropdownMenuItem(
    onClick = {
      showMenu.value = false
      vm?.importNotes()
    }
  ) {
    Text(text = stringResource(R.string.import_notes))
  }
}

@Composable fun AboutMenuItem(
  showMenu: MutableState<Boolean>,
  showAboutDialog: MutableState<Boolean>
) {
  DropdownMenuItem(
    onClick = {
      showMenu.value = false
      showAboutDialog.value = true
    }
  ) {
    Text(text = stringResource(R.string.dialog_about_title))
  }
}
