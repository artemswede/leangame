package com.artemswede.leangame.domain

enum class Phase { INTRO, RUNNING, RESULTS, CHANGE_BRIEFING, EDITING, STATION_MENU, MOVE_CONFIRMATION, PAUSED, FINAL }
data class ProductResult(val name: String, val produced: Int, val ordered: Int, val delivered: Int)
data class RoundResult(val earnings: Int, val products: List<ProductResult>)
data class GameState(val phase: Phase = Phase.INTRO, val round: Int = 1, val rounds: Int = 6, val money: Int = 5000, val changesLeft: Int = 2, val selectedStation: String? = null, val result: RoundResult? = null)

/** Deterministic reconstruction model; confirmed balance defaults live here. */
class GameEngine(private val rounds: Int = 6) {
    private val fixtures = listOf(8200, 11200, 11100, 11500, 12000, 13200)
    var state = GameState(rounds = rounds); private set
    fun start() { state = state.copy(phase = Phase.RUNNING) }
    fun finishRound() {
        val earnings = fixtures.getOrElse(state.round - 1) { 9000 }
        val result = RoundResult(earnings, listOf(ProductResult("Men's bikes", 40, 31, 31), ProductResult("Women's bikes", 20, 15, 13), ProductResult("Children's bikes", 20, 15, 13)))
        state = state.copy(phase = Phase.RESULTS, money = state.money + earnings, result = result)
    }
    fun continueToEdit() { require(state.phase == Phase.RESULTS); state = state.copy(phase = Phase.CHANGE_BRIEFING, changesLeft = 2) }
    fun beginEditing() { require(state.phase == Phase.CHANGE_BRIEFING); state = state.copy(phase = Phase.EDITING) }
    fun selectStation(name: String) { if (state.phase == Phase.EDITING) state = state.copy(phase = Phase.STATION_MENU, selectedStation = name) }
    fun cancelMenu() { if (state.phase == Phase.STATION_MENU) state = state.copy(phase = Phase.EDITING, selectedStation = null) }
    fun requestMove() { if (state.phase == Phase.STATION_MENU) state = state.copy(phase = Phase.MOVE_CONFIRMATION) }
    fun confirmMove(cost: Int = 562) { if (state.phase == Phase.MOVE_CONFIRMATION && state.money >= cost && state.changesLeft > 0) state = state.copy(money = state.money - cost, changesLeft = state.changesLeft - 1, phase = Phase.EDITING, selectedStation = null) }
    fun pause() { if (state.phase == Phase.RUNNING) state = state.copy(phase = Phase.PAUSED) }
    fun resume() { if (state.phase == Phase.PAUSED) state = state.copy(phase = Phase.RUNNING) }
    fun useChange(): Boolean { if (state.phase != Phase.EDITING || state.changesLeft == 0) return false; state = state.copy(changesLeft = state.changesLeft - 1); return true }
    fun nextRound() { require(state.phase == Phase.EDITING); state = if (state.round >= state.rounds) state.copy(phase = Phase.FINAL) else state.copy(round = state.round + 1, phase = Phase.RUNNING, result = null) }
}
