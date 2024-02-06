package com.manandhiman.abc

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.manandhiman.abc.ui.theme.ABCTheme


class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ABCTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          UI(viewModel{ MViewModel(application) })
        }
      }
    }
  }

  @Composable
  fun UI(viewModel: MViewModel) {

    var inputName by rememberSaveable {
      mutableStateOf("")
    }

    Column(
      Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {

      Button(onClick = { shareList(viewModel.students) }) {
        Text(text = "Share List")
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(0.7f),
          value = inputName,
          onValueChange = { inputName = it },
          label = { Text(text = "Enter Student Name") },
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions = KeyboardActions(
            onSend = { if(inputName.isNotBlank()) viewModel.addStudent(inputName) }
//            onNext = { focusRequester.requestFocus() }
          )
        )

        Button(onClick = {
          if(inputName.isNotBlank()) viewModel.addStudent(inputName)
        }
        ) {
          Text(text = "Add")
        }

      }

      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(
        Modifier
          .fillMaxSize()
          .padding(8.dp)) {
        items(viewModel.students.size) {

          val number = it + 1
          val name = viewModel.students[it].name

          Row(
            Modifier
              .fillMaxWidth()
              .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {

            Text(text = "$number. $name", fontSize = 24.sp, softWrap = true, modifier = Modifier.fillMaxWidth(0.75f))

            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete Item",
              modifier = Modifier.clickable { viewModel.deleteFromDB(viewModel.students[it]) }
            )
          }
          Divider()
        }

      }
    }

    
  }

  private fun shareList(students: List<Student>) {
    val listAsText = generateString(students)

    try {
      val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
      val clip = ClipData.newPlainText("Students", listAsText)
      clipboard.setPrimaryClip(clip)

    } catch (_: Exception) {

    }
    finally {
      Toast.makeText(this,"Data Copied",Toast.LENGTH_LONG).show()
    }

    try{
      val intent = Intent(Intent.ACTION_SEND)
      intent.type = "text/plain"
      intent.putExtra(Intent.EXTRA_TEXT, listAsText);
      startActivity(intent)
    }

    catch(e: Exception){
      Toast.makeText(this,"Some Error Occurred. Please Try Again",Toast.LENGTH_LONG).show()
    }
  }

  private fun generateString(students: List<Student>): String {
    val strBuilder = StringBuilder("")

    for(i in students.indices) {

      val number = i + 1
      val name = students[i].name

      strBuilder.append("$number. $name\n")
    }

    return strBuilder.toString()
  }

  @Preview(showBackground = true)
  @Composable
  fun Prev() {
    UI(viewModel{ MViewModel(application) })
  }
}
