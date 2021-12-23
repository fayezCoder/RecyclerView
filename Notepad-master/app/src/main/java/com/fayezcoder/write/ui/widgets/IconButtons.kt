

package com.fayezcoder.write.ui.widgets

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.fayezcoder.write.R
import com.fayezcoder.write.android.NotepadViewModel
import com.fayezcoder.write.ui.routes.editNote
import com.fayezcoder.write.ui.routes.viewNote

@Composable fun BackButton(navController: NavController?) {
  IconButton(
    onClick = { navController?.popBackStack() }
  ) {
    Icon(
      imageVector = Icons.Filled.ArrowBack,
      contentDescription = null,
      tint = Color.White
    )
  }
}

@Composable fun EditButton(
  id: Long,
  navController: NavController?
) {
  IconButton(
    onClick = {
      navController?.apply {
        popBackStack()
        editNote(id)
      }
    }
  ) {
    Icon(
      imageVector = Icons.Filled.Edit,
      contentDescription = stringResource(R.string.action_edit),
      tint = Color.White
    )
  }
}

@Composable fun SaveButton(
  id: Long,
  text: String,
  navController: NavController?,
  vm: NotepadViewModel?
) {
  IconButton(
    onClick = {
      vm?.saveNote(id, text) {
        navController?.apply {
          popBackStack()
          viewNote(it)
        }
      }
    }
  ) {
    Icon(
      imageVector = Icons.Filled.Save,
      contentDescription = stringResource(R.string.action_save),
      tint = Color.White
    )
  }
}

@Composable fun DeleteButton(
  id: Long,
  navController: NavController?,
  vm: NotepadViewModel?
) {
  val dialogIsOpen = remember { mutableStateOf(false) }

  IconButton(onClick = { dialogIsOpen.value = true }) {
    Icon(
      imageVector = Icons.Filled.Delete,
      contentDescription = stringResource(R.string.action_delete),
      tint = Color.White
    )
  }

  if(dialogIsOpen.value) {
    DeleteAlertDialog(
      onConfirm = {
        dialogIsOpen.value = false
        vm?.deleteNote(id) {
          navController?.popBackStack()
        }
      },
      onDismiss = {
        dialogIsOpen.value = false
      }
    )
  }
}

@Composable fun MoreButton(
  showMenu: MutableState<Boolean>
) {
  IconButton(
    onClick = { showMenu.value = true }
  ) {
    Icon(
      imageVector = Icons.Filled.MoreVert,
      contentDescription = null,
      tint = Color.White
    )
  }
}