

package com.fayezcoder.write.ui.routes

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fayezcoder.write.R
import com.fayezcoder.write.android.NotepadViewModel
import com.fayezcoder.write.models.NoteMetadata
import com.fayezcoder.write.ui.content.NoteListContent
import com.fayezcoder.write.ui.menus.NoteListMenu
import com.fayezcoder.write.ui.widgets.AboutDialog
import com.fayezcoder.write.ui.widgets.AppBarText
import kotlinx.coroutines.launch

@Composable fun NoteList(
  navController: NavController,
  vm: NotepadViewModel = hiltViewModel()
) {
  val state = produceState(listOf<NoteMetadata>()) {
    launch {
      value = vm.getNoteMetadata()
    }
  }

  NoteList(
    notes = state.value,
    navController = navController,
    vm = vm
  )
}

@Composable fun NoteList(
  notes: List<NoteMetadata>,
  navController: NavController? = null,
  vm: NotepadViewModel? = null
) {
  val showAboutDialog = remember { mutableStateOf(false) }
  AboutDialog(showAboutDialog, vm)

  Scaffold(
    topBar = {
      TopAppBar(
        title = { AppBarText(stringResource(id = R.string.app_name)) },
        backgroundColor = colorResource(id = R.color.primary),
        actions = {
          NoteListMenu(
            navController = navController,
            vm = vm,
            showAboutDialog = showAboutDialog
          )
        }
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { navController?.newNote() },
        backgroundColor = colorResource(id = R.color.primary),
        content = {
          Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = Color.White
          )
        }
      )
    },
    content = {
      NoteListContent(
        notes = notes,
        navController = navController
      )
    }
  )
}

@Suppress("FunctionName")
fun NavGraphBuilder.NoteListRoute(
  navController: NavController
) = composable(route = "NoteList") {
  NoteList(
    navController = navController
  )
}

@Preview @Composable fun NoteListPreview() = MaterialTheme {
  NoteList(
    notes = listOf(
      NoteMetadata(title = "Test Note 1"),
      NoteMetadata(title = "Test Note 2")
    )
  )
}

@Preview @Composable fun NoteListEmptyPreview() = MaterialTheme {
  NoteList(
    notes = emptyList()
  )
}