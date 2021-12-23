

package com.fayezcoder.write.ui.content

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fayezcoder.write.R
import com.fayezcoder.write.ui.routes.EditNotePreview
import com.fayezcoder.write.utils.UnitDisposableEffect

@Composable fun EditNoteContent(
  textState: MutableState<TextFieldValue>?
) {
  val focusRequester = remember { FocusRequester() }
  BasicTextField(
    value = textState?.value ?: TextFieldValue(),
    onValueChange = { textState?.value = it },
    textStyle = TextStyle(
      fontSize = 16.sp
    ),
    modifier = Modifier
      .padding(
        horizontal = 16.dp,
        vertical = 12.dp
      )
      .fillMaxWidth()
      .fillMaxHeight()
      .focusRequester(focusRequester)
  )

  if(textState?.value?.text.isNullOrEmpty()) {
    BasicText(
      text = stringResource(id = R.string.edit_text),
      style = TextStyle(
        fontSize = 16.sp,
        color = Color.LightGray
      ),
      modifier = Modifier
        .padding(
          horizontal = 16.dp,
          vertical = 12.dp
        )
    )
  }

  UnitDisposableEffect {
    focusRequester.requestFocus()
  }
}

@Preview @Composable fun EditNoteContentPreview() = EditNotePreview()