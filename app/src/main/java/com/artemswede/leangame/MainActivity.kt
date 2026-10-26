package com.artemswede.leangame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.artemswede.leangame.domain.*

class MainActivity : ComponentActivity() { override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { LeanApp() } } }
@Composable fun LeanApp() {
    val engine = remember { GameEngine() }; var state by remember { mutableStateOf(engine.state) }
    fun sync() { state = engine.state }
    MaterialTheme { Box(Modifier.fillMaxSize().background(Color(0xff70a634)), Alignment.Center) {
        Column(Modifier.fillMaxWidth(.82f).background(Color(0xff826b3d)).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Round ${state.round}/${state.rounds}     Money ${state.money}", color = Color.White)
            Spacer(Modifier.height(28.dp)); Text("Lean Bicycle Factory", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(Modifier.height(16.dp))
            when (state.phase) {
                Phase.INTRO -> { Text("Welcome to the Lean Bicycle Factory. Optimize this factory and make it a lean machine.", color=Color.White); Button(onClick={engine.start();sync()}) { Text("Start game") } }
                Phase.RUNNING -> { Text("Production is running. Observe the factory flow.", color=Color.White); Button(onClick={engine.finishRound();sync()}) { Text("Finish production run") } }
                Phase.RESULTS -> { Text("Production run finished\nEarnings this round: ${state.result!!.earnings}", color=Color.White); state.result!!.products.forEach { Text("${it.name}: ${it.produced}/${it.ordered}/${it.delivered}", color=Color.White) }; Button(onClick={engine.continueToEdit();sync()}) { Text("Continue") } }
                Phase.EDITING -> { Text("Make Changes — ${state.changesLeft} Changes left", color=Color.White); Row { Button(onClick={engine.useChange();sync()}, enabled=state.changesLeft>0) { Text("Move station") }; Spacer(Modifier.width(8.dp)); Button(onClick={engine.nextRound();sync()}) { Text(if(state.round==state.rounds) "Finish game" else "Done changing") } } }
                Phase.FINAL -> { Text("Game Over!\nTotal score: ${state.money}", color=Color.White); Button(onClick={engine.start();sync()}) { Text("Back to main menu") } }
            }
        }
    } }
}
