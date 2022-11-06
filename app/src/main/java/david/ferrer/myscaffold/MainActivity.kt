package david.ferrer.myscaffold

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import david.ferrer.myscaffold.ui.theme.MyScaffoldTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var dialogState by rememberSaveable{ mutableStateOf(false) }

            var firstName by remember{ mutableStateOf("") }
            var lastName by remember{ mutableStateOf("") }
            var email by remember{ mutableStateOf("") }
            var ciudad by remember{ mutableStateOf("") }

            val personas by remember{ mutableStateOf(mutableListOf<Persona>()) }
            personas.add(Persona("Dave", "Fluid", "davefluid@gmail.com", "San Francisco, CA", R.drawable.ic_launcher_background))
            personas.add(Persona("James", "Richardson", "jamesrichardon@gmail.com", "San Francisco, CA", R.drawable.ic_launcher_background))
            personas.add(Persona("Madeline", "Kennedy", "madelinekennedy@gmail.com","Fremont, CA", R.drawable.ic_launcher_background))
            personas.add(Persona("Anna", "Coleman", "annacoleman@gmail.com", "San Francisco, CA", R.drawable.ic_launcher_background))

            MyScaffoldTheme {
                MyDialog(
                    dialogState,
                    {dialogState = !dialogState},
                    firstName,
                    {firstName = it},
                    lastName,
                    {lastName = it},
                    email,
                    {email = it},
                    ciudad,
                    {ciudad = it},
                    {personas.add(Persona(firstName, lastName, email, ciudad, R.drawable.ic_launcher_background))}
                )

                MyScaffold(personas, dialogState) { dialogState = it}
            }
        }
    }
}

@Composable
fun MyScaffold(personas: List<Persona>,mostrar: Boolean, onDialogChange: (Boolean) -> Unit){
    val scaffoldState = rememberScaffoldState()
    val coroutine  = rememberCoroutineScope()

    Scaffold(
        topBar = { MyTopAppBar() },
        scaffoldState = scaffoldState,
        bottomBar = { MyBottomNavigation{ coroutine.launch { scaffoldState.snackbarHostState.showSnackbar("Has pulsado $it") } } },
        floatingActionButton = {MyFAB(mostrar) { onDialogChange(!mostrar) } },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = false,
        drawerContent = { MyDrawer(personas[0]) },
        content = { MyContent(personas) }
    )
}

@Composable
fun MyTopAppBar(){
    TopAppBar(
        modifier = Modifier.height(120.dp),
        backgroundColor = Color.Blue,
        contentColor = Color.White,
        elevation = 4.dp,
        content = { MyTopAppBarContent() }
    )
}

@Composable
fun MyTopAppBarContent(){
    Column(
        modifier = Modifier.fillMaxWidth()
    ){

        // Variable para guardar qué apartado elige el usuario (la info, el check-in o photos)
        var selectedIndex by remember { mutableStateOf(0) }

        // PRIMERA FILA DEL TOPAPPBAR

        Row(horizontalArrangement = Arrangement.Start){
            IconButton(
                onClick = {},
                modifier = Modifier.padding(end = 290.dp)
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
            }

            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Default.Home, contentDescription = "chat")
            }

        }

        // SEGUNDA FILA DEL TOPAPPBAR

        Row {
            Text(
                text = "Adam Smith",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 70.dp)
            )
        }

        // TERCERA FILA DEL TOPAPPBAR
        
        TabRow(
            selectedTabIndex = selectedIndex,
            indicator = {},
            backgroundColor = Color.Blue,
            contentColor = Color.White
        ) {

            // Tenía pensado hacer clases con esto pero era mucho lío y creo que así queda mejor
            // Le pasas el índice y el texto que quieres que tenga y listo
            MyTab(0, "INFO", selectedIndex) { selectedIndex = it }
            MyTab(1, "CHECK-IN", selectedIndex) { selectedIndex = it }
            MyTab(2, "PHOTOS", selectedIndex) { selectedIndex = it }
        }

        Spacer(Modifier.padding(top = 2.dp))
    }

}

@Composable
fun MyTab(index: Int,texto: String, selectedIndex: Int, onClick: (Int) -> Unit){
    Tab(
        selected = selectedIndex == index,
        text = {Text(texto, fontWeight = FontWeight.Bold)},
        onClick = {onClick(index)},
        selectedContentColor = Color.White,
        unselectedContentColor = Color.Gray
    )
}

@Composable
fun MyBottomNavigation(onClickIcon: (String) -> Unit){
    val lista  = mutableListOf<Pair<String, ImageVector>>()
    lista.add(Pair("Refresh", Icons.Default.Refresh))
    lista.add(Pair("Favorite", Icons.Default.Favorite))
    lista.add(Pair("Place", Icons.Default.Place))
    lista.add(Pair("Person", Icons.Default.Person))
    BottomNavigation(
        backgroundColor = Color(0xFFF44336),
        contentColor = Color.White
    ) {
        lista.forEach { item ->
            BottomNavigationItem(
                selected = false,
                onClick = { onClickIcon(item.first) },
                icon = {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first
                    )
                },
                label = { Text(item.first) }
            )
        }
    }
}

@Composable
fun MyFAB(mostrar: Boolean, showChange: (Boolean) -> Unit){
    FloatingActionButton(
        onClick = { showChange(!mostrar) },
        backgroundColor = Color.Blue,
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "add")
    }
}

@Composable
fun MyDrawer(persona: Persona){

    val listaOpciones = listOf("Education", "Experience", "Skills")

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White)
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 100.dp, top = 35.dp)
            ){
                Image(
                    painterResource(id = persona.foto),
                    contentDescription = "foto",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Text("${persona.nombre} ${persona.apellido}")
                Text(persona.direccion)
            }
        }

        Spacer(modifier = Modifier.padding(70.dp))

        Column(
            modifier = Modifier.background(Color.White)
        ) {
            listaOpciones.forEach { opcion ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.White)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(120.dp)
                    ){
                        Text(
                            text = opcion,
                            color = Color.LightGray,
                            modifier = Modifier
                                .padding(start = 20.dp)
                                .width(280.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "desplegable"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyContent(personas: List<Persona>){
    Column(
        Modifier.padding(start = 2.dp)
    ){
        personas.forEach{ persona ->
            MyCard(persona)
            Spacer(modifier = Modifier.padding(bottom = 2.dp))
        }
    }
}

@Composable
fun MyCard(persona: Persona){
    Card(
        elevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ){
        Row(
            Modifier.padding(start = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Spacer(modifier = Modifier.padding(5.dp))
            Image(
                painterResource(id = persona.foto),
                contentDescription = "foto",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Column(
                Modifier
                    .padding(start = 10.dp)
                    .width(120.dp)){
                Text("${persona.nombre} ${persona.apellido}")
                Row{
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = "Place",
                        Modifier
                            .size(20.dp)
                            .padding(top = 1.dp)
                    )
                    Text(
                        persona.direccion,
                        fontSize = 12.sp,
                    )
                }
            }
            Row{
                Button(
                    onClick = { },
                    modifier = Modifier
                        .padding(start = 140.dp)
                        .clip(CircleShape)
                        .size(40.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
                ) {
                    Text("+", fontSize = 15.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MyDialog(
    mostrar: Boolean,
    showChange: () -> Unit,
    firstName: String,
    firstNameChange: (String) -> Unit,
    lastName: String,
    lastNameChange: (String) -> Unit,
    email: String,
    emailChange: (String) -> Unit,
    ciudad: String,
    ciudadChange: (String) -> Unit,
    addPeople:() -> Unit
){
    if(mostrar) {
        Dialog(
            onDismissRequest = { showChange() },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Direcciones", fontSize = 45.sp)
                Spacer(Modifier.padding(top = 20.dp))
                MyDialogTextField("First name", firstName, onValueChange = firstNameChange)
                Spacer(Modifier.padding(top = 10.dp))
                MyDialogTextField("Last name", lastName, onValueChange = lastNameChange)
                Spacer(Modifier.padding(top = 10.dp))
                MyDialogTextField("Email", email, onValueChange = emailChange)
                Spacer(Modifier.padding(top = 10.dp))
                MyDialogTextField("City", ciudad, onValueChange = ciudadChange)
                Spacer(Modifier.padding(top = 10.dp))
                Button(
                    onClick = {
                        if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()) {
                            showChange()
                            addPeople()
                            firstNameChange("")
                            lastNameChange("")
                            emailChange("")
                            ciudadChange("")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF44336))
                ) {
                    Text("SEND INVITATION", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MyDialogTextField(texto: String, valor: String, onValueChange: (String) -> Unit){
    Column{
        TextField(
            value = valor,
            onValueChange = onValueChange,
            placeholder = {
                Text(texto)
            }
        )
    }
}






