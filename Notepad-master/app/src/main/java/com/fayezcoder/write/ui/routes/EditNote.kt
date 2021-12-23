

package com.fayezcoder.write.ui.routes

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fayezcoder.write.R
import com.fayezcoder.write.android.NotepadViewModel
import com.fayezcoder.write.models.Note
import com.fayezcoder.write.models.NoteContents
import com.fayezcoder.write.models.NoteMetadata
import com.fayezcoder.write.ui.content.EditNoteContent
import com.fayezcoder.write.ui.menus.NoteViewEditMenu
import com.fayezcoder.write.ui.widgets.*
import kotlinx.coroutines.launch

@Composable fun EditNote(
  id: Long?,
  navController: NavController? = null,
  vm: NotepadViewModel = hiltViewModel(),
  isMultiPane: Boolean = false
) {
  val state = produceState(
    Note(
      metadata = NoteMetadata(
        title = stringResource(id = R.string.action_new)
      )
    )
  ) {
    id?.let {
      launch {
        value = vm.getNote(it)
      }
    }
  }

  val textState = remember {
    mutableStateOf(TextFieldValue())
  }.apply {
    val text = state.value.contents.text
    value = TextFieldValue(
      text = text,
      selection = TextRange(text.length)
    )
  }

  if(isMultiPane) {
    EditNoteContent(textState)
  } else {
    EditNote(
      note = state.value,
      textState = textState,
      navController = navController,
      vm = vm
    )
  }
}

@Composable fun EditNote(
  note: Note,
  textState: MutableState<TextFieldValue>,
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
          SaveButton(id, textState.value.text, navController, vm)
          DeleteButton(id, navController, vm)
          NoteViewEditMenu(textState.value.text, vm)
        }
      )
    },
    content = {
      EditNoteContent(textState)
    }
  )
}

@Suppress("FunctionName")
fun NavGraphBuilder.EditNoteRoute(
  navController: NavController
) = composable(
  route = "EditNote?id={id}",
  arguments = listOf(
    navArgument("id") { nullable = true }
  )
) {
  EditNote(
    id = it.arguments?.getString("id")?.toLong(),
    navController = navController
  )
}

fun NavController.newNote() = navigate("EditNote")
fun NavController.editNote(id: Long) = navigate("EditNote?id=$id")

@Preview @Composable fun EditNotePreview() = MaterialTheme {
  EditNote(
    note = Note(
      metadata = NoteMetadata(
        title = "Title"
      ),
      contents = NoteContents(
        text = "This is some text"
      )
    ),
    textState = remember { mutableStateOf(TextFieldValue()) }
  )
}