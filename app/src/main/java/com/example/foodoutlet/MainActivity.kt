package com.example.foodoutlet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    FoodOutletApp()
                }
            }
        }
    }
}

@Composable
fun FoodOutletApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("detail/{boxId}") { backStackEntry ->
            val boxId = backStackEntry.arguments?.getString("boxId")
            val selectedBox = sampleBoxes.find { it.id == boxId }
            selectedBox?.let { BoxDetailScreen(navController, it) }
        }
        composable("checkout") { CheckoutScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FOOD OUTLET", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate("checkout") }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("THIS WEEK'S BOXES", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(sampleBoxes) { box ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(box.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Retail: $${box.retailValue}")
                                Text("Price: $${box.price}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Badge(containerColor = Color(0xFF4CAF50)) {
                                Text("SAVE ${box.savingsPercentage}%", color = Color.White, modifier = Modifier.padding(4.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate("detail/${box.id}") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("VIEW CONTENTS")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxDetailScreen(navController: NavController, box: GroceryBox) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(box.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.LightGray), contentAlignment = Alignment.Center) {
                Text("[ Image of ${box.name} ]", color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("What's Inside:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            box.contents.forEach { item ->
                Text("- $item", modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Retail Value: $${box.retailValue}", color = Color.Gray)
            Text("Your Price: $${box.price}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Savings: ${box.savingsPercentage}%", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { navController.navigate("checkout") },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("ADD TO CART")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CHECKOUT") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("ORDER SUMMARY", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("1x Family Value Box")
                Text("$32.00")
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text("PICKUP LOCATION", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth().height(120.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Map")
                        Text("[ Map Placeholder ]")
                        Text("Selected: 200 Dundas St E, Toronto", fontSize = 12.sp)
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text("PAYMENT", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = "**** **** **** 4242",
                onValueChange = {},
                label = { Text("Card Number") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("CONFIRM & PAY $32.00")
            }
        }
    }
}