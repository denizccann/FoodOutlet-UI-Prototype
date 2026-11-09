package com.example.foodoutlet

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.random.Random

val ElegantFont = FontFamily.Serif


data class StoreLocation(val name: String, val address: String, val lat: Double, val lng: Double)
val infinityStores = listOf(
    StoreLocation("Infinity Mart - Yonge St", "852 Yonge St, Toronto", 43.671, -79.387),
    StoreLocation("Infinity Mart - Queen East", "1930 Queen St East, Toronto", 43.669, -79.301),
    StoreLocation("Infinity Mart - Bloor West", "1986 Bloor St West, Toronto", 43.652, -79.467),
    StoreLocation("Infinity Mart - Dundas St", "200 Dundas St E, Toronto", 43.655, -79.370),
    StoreLocation("Infinity Mart - Fort York", "170 Fort York Blvd, Toronto", 43.639, -79.398),
    StoreLocation("Infinity Mart - Bay St", "801 Bay St, Toronto", 43.662, -79.385),
    StoreLocation("Infinity Mart - Niagara", "4861 Victoria Ave, Niagara Falls", 43.090, -79.073)
)

class FoodOutletViewModel : ViewModel() {
    private val _cart = MutableStateFlow<List<GroceryBox>>(emptyList())
    val cart: StateFlow<List<GroceryBox>> = _cart.asStateFlow()

    private val _isVegetarianMode = MutableStateFlow(false)
    val isVegetarianMode: StateFlow<Boolean> = _isVegetarianMode.asStateFlow()

    private val _selectedStore = MutableStateFlow(infinityStores[0])
    val selectedStore: StateFlow<StoreLocation> = _selectedStore.asStateFlow()

    fun addToCart(box: GroceryBox) { _cart.value = _cart.value + box }
    fun removeFromCart(box: GroceryBox) { _cart.value = _cart.value - box }
    fun clearCart() { _cart.value = emptyList() }
    fun toggleVegetarianMode(enabled: Boolean) { _isVegetarianMode.value = enabled }
    fun selectStore(store: StoreLocation) { _selectedStore.value = store }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        setContent { FoodOutletApp() }
    }
}

@Composable
fun FoodOutletApp() {
    val navController = rememberNavController()
    val viewModel: FoodOutletViewModel = viewModel()

    val dynamicColorScheme = lightColorScheme(
        primary = Color(0xFF4F46E5),
        tertiary = Color(0xFF312E81),
        secondary = Color(0xFF10B981),
        background = Color(0xFFF8FAFC),
        surface = Color.White,
        onBackground = Color(0xFF1E293B)
    )

    MaterialTheme(colorScheme = dynamicColorScheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NavHost(navController = navController, startDestination = "welcome") {
                composable("welcome") { WelcomeScreen(navController) }
                composable("home") { HomeScreen(navController, viewModel) }
                composable("detail/{boxId}") { backStackEntry ->
                    val boxId = backStackEntry.arguments?.getString("boxId")
                    val isVeg = viewModel.isVegetarianMode.collectAsState().value
                    val sourceList = if (isVeg) vegBoxes else sampleBoxes
                    val selectedBox = sourceList.find { it.id == boxId }
                    selectedBox?.let { BoxDetailScreen(navController, it, viewModel) }
                }
                composable("checkout") { CheckoutScreen(navController, viewModel) }
            }
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF312E81), Color(0xFF4F46E5), Color(0xFF0EA5E9)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.15f), modifier = Modifier.size(100.dp)) {
                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.padding(24.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Food Outlet", fontFamily = ElegantFont, fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Premium Curated Grocery Boxes", fontFamily = ElegantFont, fontSize = 16.sp, color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(40.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Developed by Deniz Can", fontFamily = ElegantFont, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("George Brown College | COMP3074", fontFamily = ElegantFont, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(color = Color(0xFFFDE047).copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                        Text("Official Retail Partner: Infinity Mart", fontFamily = ElegantFont, fontSize = 13.sp, color = Color(0xFFFDE047), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("LET'S ORDER", fontFamily = ElegantFont, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF312E81))
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF312E81))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: FoodOutletViewModel) {
    val cart by viewModel.cart.collectAsState()
    val isVeg by viewModel.isVegetarianMode.collectAsState()
    val currentBoxes = if (isVeg) vegBoxes else sampleBoxes

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Food Outlet", fontFamily = ElegantFont, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, color = MaterialTheme.colorScheme.tertiary)
                        Text("Partnered with Infinity Mart", fontFamily = ElegantFont, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    BadgedBox(
                        badge = { if (cart.isNotEmpty()) Badge(containerColor = Color(0xFFEF4444)) { Text(cart.size.toString(), color = Color.White) } },
                        modifier = Modifier.padding(end = 16.dp, start = 8.dp)
                    ) {
                        IconButton(onClick = { navController.navigate("checkout") }, modifier = Modifier.background(Color.White, CircleShape).shadow(3.dp, CircleShape)) {
                            Icon(Icons.Rounded.ShoppingCart, contentDescription = "Cart", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), modifier = Modifier.size(44.dp)) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(10.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Curated Quality & Value", fontFamily = ElegantFont, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
                                Text("Smart Weekly Grocery Savings", fontFamily = ElegantFont, fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Welcome to Food Outlet! Built for COMP3074 by Deniz Can in collaboration with Infinity Mart. Choose between our Normal Curated Boxes or switch to Vegetarian Mode anytime for healthy plant-based savings.",
                            fontFamily = ElegantFont,
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.toggleVegetarianMode(false) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isVeg) MaterialTheme.colorScheme.primary else Color.White),
                        border = if (isVeg) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Text("🛒 Normal Boxes", fontFamily = ElegantFont, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (!isVeg) Color.White else MaterialTheme.colorScheme.primary)
                    }
                    Button(
                        onClick = { viewModel.toggleVegetarianMode(true) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isVeg) Color(0xFF059669) else Color.White),
                        border = if (!isVeg) BorderStroke(1.dp, Color(0xFF059669)) else null
                    ) {
                        Text("🌱 Vegetarian", fontFamily = ElegantFont, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (isVeg) Color.White else Color(0xFF059669))
                    }
                }
            }

            item {
                Text(text = if (isVeg) "🌱 Plant-Based Weekly Picks" else "🛒 Curated Weekly Boxes", fontFamily = ElegantFont, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }
            items(currentBoxes) { box -> BoxCardItem(box = box) { navController.navigate("detail/${box.id}") } }
        }
    }
}

@Composable
fun BoxCardItem(box: GroceryBox, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x44000000)),
        shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                AsyncImage(model = box.imageUrl, contentDescription = box.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))

                Row(modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopStart), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    box.tags.forEach { tag ->
                        Surface(color = Color.White.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp)) {
                            Text(text = tag.uppercase(), fontFamily = ElegantFont, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = box.name, fontFamily = ElegantFont, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = box.description, fontFamily = ElegantFont, fontSize = 15.sp, color = Color.Gray, lineHeight = 22.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text(text = "Retail: $${"%.2f".format(box.retailValue)}", fontFamily = ElegantFont, fontSize = 15.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough)
                        Text(text = "$${"%.2f".format(box.price)}", fontFamily = ElegantFont, fontSize = 30.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                    Surface(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Icon(Icons.Default.TrendingDown, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "SAVE ${box.savingsPercentage}%", fontFamily = ElegantFont, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxDetailScreen(navController: NavController, box: GroceryBox, viewModel: FoodOutletViewModel) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Box Details", fontFamily = ElegantFont, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, scrolledContainerColor = MaterialTheme.colorScheme.surface))
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 16.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Total Price", fontFamily = ElegantFont, fontSize = 14.sp, color = Color.Gray)
                        Text("$${"%.2f".format(box.price)}", fontFamily = ElegantFont, fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(
                        onClick = { viewModel.addToCart(box); Toast.makeText(context, "${box.name} added to cart!", Toast.LENGTH_SHORT).show(); navController.navigate("checkout") },
                        modifier = Modifier.height(56.dp).width(200.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Rounded.ShoppingCart, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ADD TO CART", fontFamily = ElegantFont, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().animateContentSize(), contentPadding = PaddingValues(bottom = 100.dp)) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    AsyncImage(model = box.imageUrl, contentDescription = box.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(box.name, fontFamily = ElegantFont, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center) }
                }
            }
            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Overview", fontFamily = ElegantFont, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(box.description, fontFamily = ElegantFont, fontSize = 16.sp, color = Color.DarkGray, lineHeight = 24.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("What's Inside", fontFamily = ElegantFont, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            box.contents.forEachIndexed { index, item ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 10.dp)) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(item, fontFamily = ElegantFont, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium)
                                }
                                if (index < box.contents.size - 1) HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 1.dp, modifier = Modifier.padding(start = 34.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)), border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("🔥 Incredible Deal: You save $${"%.2f".format(box.retailValue - box.price)} by buying this curated weekly box from Infinity Mart!", fontFamily = ElegantFont, color = MaterialTheme.colorScheme.secondary, fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController, viewModel: FoodOutletViewModel) {
    val cart by viewModel.cart.collectAsState()
    val total = cart.sumOf { it.price }
    val selectedStore by viewModel.selectedStore.collectAsState()
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val dynamicCode = remember(firstName, selectedStore) {
        val namePart = if (firstName.isNotBlank()) firstName.uppercase() else "GUEST"
        "$namePart-${Random.nextInt(100, 999)}"
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout & Pickup", fontFamily = ElegantFont, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 24.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pay at Location:", fontFamily = ElegantFont, fontSize = 18.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Text("$${"%.2f".format(total)}", fontFamily = ElegantFont, fontSize = 26.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(
                        onClick = {
                            if (firstName.isBlank()) {
                                Toast.makeText(context, "Please enter your Name!", Toast.LENGTH_SHORT).show()
                            } else {
                                showDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), enabled = cart.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("RESERVE NOW", fontFamily = ElegantFont, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {

            item {
                Text("Your Details", fontFamily = ElegantFont, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name", fontFamily = ElegantFont) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name (Optional)", fontFamily = ElegantFont) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                    }
                }
            }

            item {
                Text("Order Summary", fontFamily = ElegantFont, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (cart.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("Your cart is empty.", fontFamily = ElegantFont, color = Color.Gray)
                            }
                        } else {
                            cart.forEachIndexed { index, item ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Surface(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(40.dp)) { Box(contentAlignment = Alignment.Center) { Text("1x", fontFamily = ElegantFont, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) } }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(item.name, fontFamily = ElegantFont, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("$${"%.2f".format(item.price)}", fontFamily = ElegantFont, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(onClick = { viewModel.removeFromCart(item) }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                                if (index < cart.size - 1) HorizontalDivider(color = MaterialTheme.colorScheme.background)
                            }
                        }
                    }
                }
            }

            item {
                Text("Select Infinity Mart Store", fontFamily = ElegantFont, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp)) {
                        Text(selectedStore.name, fontFamily = ElegantFont, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        infinityStores.forEach { store ->
                            DropdownMenuItem(text = { Text(store.name, fontFamily = ElegantFont) }, onClick = { viewModel.selectStore(store); expanded = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))) {
                            AndroidView(
                                factory = { ctx -> MapView(ctx).apply { setMultiTouchControls(true); controller.setZoom(15.0); val point = GeoPoint(selectedStore.lat, selectedStore.lng); controller.setCenter(point); val marker = Marker(this).apply { position = point; setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); title = selectedStore.name }; overlays.add(marker) } },
                                update = { mapView -> val point = GeoPoint(selectedStore.lat, selectedStore.lng); mapView.controller.setCenter(point); mapView.overlays.clear(); val marker = Marker(mapView).apply { position = point; setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); title = selectedStore.name }; mapView.overlays.add(marker); mapView.invalidate() },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), modifier = Modifier.size(44.dp)) { Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(10.dp)) }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column { Text(selectedStore.name, fontFamily = ElegantFont, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onBackground); Text(selectedStore.address, fontFamily = ElegantFont, fontSize = 15.sp, color = Color.Gray) }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("🎉 Order Reserved!", fontFamily = ElegantFont, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                text = {
                    Column {
                        Text("Thank you, $firstName! Your box is reserved at **${selectedStore.name}**.", fontFamily = ElegantFont, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("YOUR UNIQUE PICKUP CODE", fontFamily = ElegantFont, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(dynamicCode, fontFamily = ElegantFont, fontWeight = FontWeight.Black, fontSize = 28.sp, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Please take a screenshot of this code. Show it at the store counter to pay and collect your box.", fontFamily = ElegantFont, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false; viewModel.clearCart(); navController.navigate("home") { popUpTo("welcome") } },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Got It! Back to Home", fontFamily = ElegantFont, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}