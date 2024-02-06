package com.example.speedrun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speedrun.ui.theme.SpeedrunTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedrunTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    val loggedIn = FirebaseAuth.getInstance().currentUser != null

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("registration") {
            RegistrationScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { signIn(email, password, navController) }) {
            Text("Sign In")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate("registration") }) {
            Text("Register")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (password == confirmPassword) {
                signUp(email, password, navController)
            } else {
                errorMessage = "Passwords do not match"
            }
        }) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(errorMessage, color = Color.Red)
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    TodoList(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoList(navController: NavController) {
    // Список задач
    val tasks = remember { mutableStateListOf<String>() }
    // Текст новой задачи
    var newTaskText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("ToDo List", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Отображение списка задач
        Column {
            tasks.forEachIndexed { index, task ->
                var editingTask by remember { mutableStateOf(false) }
                var editedTask by remember { mutableStateOf(task) }

                if (editingTask) {
                    TextField(
                        value = editedTask,
                        onValueChange = { editedTask = it },
                        label = { Text("Edit Task") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            tasks[index] = editedTask
                            editingTask = false
                        }),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Row {
                        Text(text = task)
                        IconButton(onClick = {
                            tasks.removeAt(index)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
                        }
                        IconButton(onClick = {
                            editingTask = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Task")
                        }
                    }
                }
            }
        }

        // Поле ввода для новой задачи
        OutlinedTextField(
            value = newTaskText,
            onValueChange = { newTaskText = it },
            label = { Text("New Task") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка для добавления новой задачи
        Button(onClick = {
            if (newTaskText.isNotEmpty()) {
                tasks.add(newTaskText)
                newTaskText = ""
            }
        }) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка для выхода из приложения
        Button(onClick = { FirebaseAuth.getInstance().signOut(); navController.navigate("login") }) {
            Text("Sign Out")
        }
    }
}


// Функция для входа в систему
fun signIn(email: String, password: String, navController: NavController) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate("home")
            } else {
                // Ошибка входа
            }
        }
}

// Функция для регистрации
fun signUp(email: String, password: String, navController: NavController) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Отправка письма для верификации
                FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            // Переход на главный экран после успешной регистрации и отправки письма для верификации
                            navController.navigate("home")
                        } else {
                            // Обработка ошибки отправки письма для верификации
                        }
                    }
            } else {
                // Обработка ошибки регистрации
            }
        }
}
