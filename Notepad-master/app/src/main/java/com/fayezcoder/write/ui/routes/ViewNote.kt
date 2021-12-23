
package com.fayezcoder.write.ui.routes

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fayezcoder.write.R
import com.fayezcoder.write.android.NotepadViewModel
import com.fayezcoder.write.models.Note
import com.fayezcoder.write.models.NoteContents
import com.fayezcoder.write.models.NoteMetadata
import com.fayezcoder.write.ui.content.ViewNoteContent
import com.fayezcoder.write.ui.menus.NoteViewEditMenu
import com.fayezcoder.write.ui.widgets.*
import kotlinx.coroutines.launch

@Composable fun ViewNote(
  id: Long,
  navController: NavController? = null,
  vm: NotepadViewModel = hiltViewModel(),
  isMultiPane: Boolean = false
) {
  val state = produceState(Note()) {
    launch {
      value = vm.getNote(id)
    }
  }

  if(isMultiPane) {
    ViewNoteContent(state.value)
  } else {
    ViewNote(
      note = state.value,
      navController = navController,
      vm = vm
    )
  }
}

@Composable fun ViewNote(
  note: Note,
  navController: NavController? = null,
  vm: NotepadViewModel? = null
) {
  val id = note.metadata.metadataId

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = { BackButton(navController) },
        title = { AppBarText(note.metadata.title) },
        backgroundColor = colorResource(id = R.color.primary),
        actions = {
          EditButton(id, navController)
          DeleteButton(id, navController, vm)
          NoteViewEditMenu(note.contents.text, vm)
        }
      )
    },
    content = {
      ViewNoteContent(note)
    }
  )
}

@Suppress("FunctionName")
fun NavGraphBuilder.ViewNoteRoute(
  navController: NavController
) = composable(
  route = "ViewNote/{id}",
  arguments = listOf(
    navArgument("id") { NavType.StringType }
  )
) {
  it.arguments?.getString("id")?.let { id ->
    ViewNote(
      id = id.toLong(),
      navController = navController
    )
  }
}

fun NavController.viewNote(id: Long) = navigate("ViewNote/$id")

@Preview @Composable fun ViewNotePreview() = MaterialTheme {
  ViewNote(
    note = Note(
      metadata = NoteMetadata(
        title = "Title"
      ),
      contents = NoteContents(
        text = "This is some text"
      )
    )
  )
}