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

private val grass=Color(0xff70a634); private val floor=Color(0xff79613a); private val dialog=Color(0xff536894)
private val stations=listOf("Planning","Casting","Assembly","Painting","Quality Assurance","Storage","Counter")
class MainActivity:ComponentActivity(){override fun onCreate(b:Bundle?){super.onCreate(b);setContent{LeanApp()}}}
@Composable private fun LeanApp(){
 val engine=remember{GameEngine()}; var state by remember{mutableStateOf(engine.state)}; fun sync(){state=engine.state}
 MaterialTheme{Box(Modifier.fillMaxSize().background(grass).padding(12.dp)){Column(Modifier.fillMaxSize().background(floor).padding(12.dp)){
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Round ${state.round}/${state.rounds}",color=Color.White);Text("Money ${state.money}",color=Color.White);if(state.phase==Phase.RUNNING)TextButton({engine.pause();sync()}){Text("Pause Game")}}
  Column(Modifier.weight(1f).padding(12.dp),verticalArrangement=Arrangement.SpaceEvenly){Text("Lean Bicycle Factory",style=MaterialTheme.typography.headlineMedium,color=Color.White);Text(if(state.phase==Phase.RUNNING)"Production is running — observe the material flow." else "Tap a station to manage the factory.",color=Color.White);stations.chunked(3).forEach{r->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){r.forEach{s->OutlinedButton({engine.selectStation(s);sync()},enabled=state.phase==Phase.EDITING){Text(s)}}}}}
  if(state.phase==Phase.EDITING)Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("${state.changesLeft} Changes left",color=Color.White);Button({engine.nextRound();sync()}){Text(if(state.round==state.rounds)"Finish game" else "Done changing")}}
 };GameDialog(state,{engine.start();sync()},{engine.finishRound();sync()},{engine.continueToEdit();sync()},{engine.beginEditing();sync()},{engine.requestMove();sync()},{engine.confirmMove();sync()},{engine.cancelMenu();sync()},{engine.resume();sync()})}}
}
@Composable private fun GameDialog(s:GameState,start:()->Unit,finish:()->Unit,cont:()->Unit,begin:()->Unit,move:()->Unit,confirm:()->Unit,cancel:()->Unit,resume:()->Unit){when(s.phase){
 Phase.INTRO->Modal("Welcome to the Lean Bicycle Factory","Your job here is to optimize this factory and make it a lean machine. Observe the flow and plan changes for the next round."){Button(start){Text("Start game")}}
 Phase.RUNNING->Modal("Production running","The 180-second production run is simulated deterministically for this reconstruction."){Button(finish){Text("Finish production run")}}
 Phase.RESULTS->Modal("Production run finished","End of round ${s.round}\nEarnings this round: ${s.result!!.earnings}\n\n"+s.result!!.products.joinToString("\n"){"${it.name}: ${it.produced} / ${it.ordered} / ${it.delivered}"}){Button(cont){Text("Continue")}}
 Phase.CHANGE_BRIEFING->Modal("Make Changes","You only have two changes per round, so choose carefully. Click a station to see what changes you can make."){Button(begin){Text("Start changing")}}
 Phase.STATION_MENU->Modal(s.selectedStation?:"Station","Choose an action. A confirmed action spends money and one change."){Row{Button(move){Text("Move")};Spacer(Modifier.width(8.dp));OutlinedButton(cancel){Text("Cancel")}}}
 Phase.MOVE_CONFIRMATION->Modal("Moving a station","Move a station closer to reduce travel. When buffers overlap, the truck is not needed.\n\nMoving will cost $562. Proceed?"){Row{Button(confirm){Text("Yes")};Spacer(Modifier.width(8.dp));OutlinedButton(cancel){Text("No")}}}
 Phase.PAUSED->Modal("Pause Game","Production is paused."){Button(resume){Text("Resume")}}
 Phase.FINAL->Modal("Game Over!","Total score: ${s.money}"){Button(start){Text("Back to main menu")}}
 else->Unit}}
@Composable private fun Modal(title:String,body:String,content:@Composable()->Unit){Box(Modifier.fillMaxSize(),Alignment.Center){Surface(color=dialog,modifier=Modifier.fillMaxWidth(.72f)){Column(Modifier.padding(22.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(title,style=MaterialTheme.typography.titleLarge,color=Color.White);Spacer(Modifier.height(12.dp));Text(body,color=Color.White);Spacer(Modifier.height(18.dp));content()}}}}
