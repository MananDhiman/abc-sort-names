package com.manandhiman.abc


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.manandhiman.abc.ui.theme.ABCSortNamesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ABCSortNamesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {

                        val db = DatabaseHandler(applicationContext)
                        val viewModel = viewModel{ MViewModel(db) }
                        val navController = rememberNavController()

                        NavHost(navController, startDestination = "main") {
                            composable(route = "main") { UI(viewModel, navController) }
                            composable(route = "manageLists") { ListsScreen(viewModel) }
                        }

                    }
                }
            }
        }
    }

    @Composable
    fun UI(viewModel: MViewModel, navController: NavHostController) {

        var inputName by rememberSaveable { mutableStateOf("") }
        val currentFilter = remember { mutableStateOf(Filter.DEFAULT) }

        Column( Modifier.fillMaxSize() .padding(16.dp) ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { shareList(viewModel.students) }, enabled = viewModel.students.isNotEmpty()) {
                    Text(text = "Share List")
                }

                val dropDownExpanded = remember { mutableStateOf(false) }

                Column ( Modifier.clickable { dropDownExpanded.value = !dropDownExpanded.value } ) {
                    OutlinedButton(onClick = {dropDownExpanded.value = !dropDownExpanded.value}) { Text("Options") }

                    DropdownMenu(
                        expanded = dropDownExpanded.value,
                        onDismissRequest = { dropDownExpanded.value = false }
                    ) {
                        if (currentFilter.value == Filter.DEFAULT ) {
                            DropdownMenuItem(
                                text = { Text("Reverse / Descending Order") },
                                onClick = { currentFilter.value = Filter.REVERSE }
                            )
                        } else if(currentFilter.value != Filter.DEFAULT) {
                            DropdownMenuItem(
                                text = { Text("Default / Ascending Order") },
                                onClick = { currentFilter.value = Filter.DEFAULT }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("By Name Length") },
                            onClick = { currentFilter.value = Filter.NAME_LENGTH }
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                Modifier
                    .fillMaxWidth()
                    .border(width = 3.dp, shape = RoundedCornerShape(20.dp), color = androidx.compose.ui.graphics.Color(Color.GRAY))
                    .padding(12.dp)) {
                // todo update colour set selected or disabled later
                item {
                    Button(onClick = {
                        viewModel.activeList = 0
                    }) {
                        Text(text = "All Entries")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                items(viewModel.abcLists.size) {
                    Button(onClick = {
                        viewModel.activeList = viewModel.abcLists[it].id
                    }) {
                        Text(text = viewModel.abcLists[it].name)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                item {
                    Button(onClick = { navController.navigate("manageLists" )}) {
                        Text(text = "Manage Lists")
                    }
                }
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
                    label = { Text(text = "Enter Name") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = { if(inputName.isNotBlank()) {
                            viewModel.addStudent(inputName)
                            inputName = ""
                        } }
                    )
                )

                Button(onClick = {
                    if(inputName.isNotBlank()) {
                        viewModel.addStudent(inputName)
                        inputName = ""
                    }
                }
                ) { Text(text = "Add") }

            }

            Spacer(modifier = Modifier.height(8.dp))

            val students: List<Student> = when (currentFilter.value) {
                Filter.DEFAULT -> viewModel.students
                Filter.REVERSE -> viewModel.studentsReversed
                Filter.NAME_LENGTH -> viewModel.studentsNameLength
            }

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(8.dp)) {
                items(students.size) {

                    val number = it + 1
                    val name = students[it].name

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
                            modifier = Modifier.clickable { viewModel.deleteStudentFromDB(students[it]) }
                        )
                    }
                    HorizontalDivider()
                }

            }
        }


    }

    @Composable
    fun ListsScreen(viewModel: MViewModel) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val openDialog = remember { mutableStateOf(false) }
            val listName = remember { mutableStateOf("Default List") }

            val activeListId = remember { mutableStateOf(0) }

            if (openDialog.value) {
                ManageListDialog(openDialog, listName, activeListId, viewModel::addOrUpdateList)
            }

            Text("Manage Lists", fontSize = 24.sp)

            Spacer(Modifier.height(24.dp))

            Button(onClick = {
                openDialog.value = true
                activeListId.value = 0
            }) { // todo
                Text(text = "Create new List")
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(8.dp)) {
                items(viewModel.abcLists.size) {

                    val number = it + 1
                    val name = viewModel.abcLists[it].name

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
                            contentDescription = "Delete List",
                            modifier = Modifier.clickable { viewModel.deleteList(viewModel.abcLists[it].id) }
                        )
                    }
                    HorizontalDivider()
                }

            }

        }


    }

    @Composable
    fun ManageListDialog(
        openDialog: MutableState<Boolean>,
        listName: MutableState<String>,
        activeListId: MutableState<Int>,
        addOrUpdateList: (id: Int, listName: String) -> Unit,
        ) {
        Dialog(onDismissRequest = { openDialog.value = false }) {
            Card(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp)) {

                Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    OutlinedTextField(value = listName.value, onValueChange = { listName.value = it }, label = { Text("List Name") })

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(onClick = { openDialog.value = false }) { Text("Cancel") }
                        Button(onClick = {
                            addOrUpdateList(activeListId.value, listName.value)
                            openDialog.value = false
                            activeListId.value = 0
                            listName.value = "Default List"
                        }) { Text("Save") }
                    }

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
            intent.putExtra(Intent.EXTRA_TEXT, listAsText)
            startActivity(intent)
        }

        catch(e: Exception){
            Toast.makeText(this,"Some Error Occurred. Please Try Again",Toast.LENGTH_LONG).show()
            Log.d("TAG EXCEPTION", e.message.toString())
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
    fun GreetingPreview() {
        ABCSortNamesTheme {
            UI(viewModel{ MViewModel(DatabaseHandler(this@MainActivity)) }, NavHostController(this))
        }
    }
}

