package com.artemswede.leangame
import com.artemswede.leangame.domain.*
import org.junit.Assert.*
import org.junit.Test
class GameEngineTest {
 @Test fun coreLoopKeepsChangeLimitAndFinishes() { val e=GameEngine(1); e.start(); e.finishRound(); assertEquals(Phase.RESULTS,e.state.phase); e.continueToEdit(); e.beginEditing(); assertTrue(e.useChange()); assertTrue(e.useChange()); assertFalse(e.useChange()); e.nextRound(); assertEquals(Phase.FINAL,e.state.phase) }
 @Test fun movingStationCostsMoneyAndOneChangeOnlyAfterConfirmation() { val e=GameEngine(); e.start(); e.finishRound(); e.continueToEdit(); e.beginEditing(); e.selectStation("Assembly"); e.requestMove(); e.confirmMove(); assertEquals(12638, e.state.money); assertEquals(1, e.state.changesLeft); assertEquals(Phase.EDITING, e.state.phase) }
 @Test fun firstRoundUsesVerifiedEarningsFixture() { val e=GameEngine(); e.start(); e.finishRound(); assertEquals(8200,e.state.result!!.earnings) }
}
