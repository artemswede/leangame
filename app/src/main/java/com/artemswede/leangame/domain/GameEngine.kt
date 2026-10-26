package com.artemswede.leangame.domain

enum class Phase { INTRO, RUNNING, RESULTS, EDITING, FINAL }
data class ProductResult(val name: String, val produced: Int, val ordered: Int, val delivered: Int)
data class RoundResult(val earnings: Int, val products: List<ProductResult>)
data class GameState(val phase: Phase = Phase.INTRO, val round: Int = 1, val rounds: Int = 6, val money: Int = 5000, val changesLeft: Int = 2, val result: RoundResult? = null)

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
    fun continueToEdit() { require(state.phase == Phase.RESULTS); state = state.copy(phase = Phase.EDITING, changesLeft = 2) }
    fun useChange(): Boolean { if (state.phase != Phase.EDITING || state.changesLeft == 0) return false; state = state.copy(changesLeft = state.changesLeft - 1); return true }
    fun nextRound() { require(state.phase == Phase.EDITING); state = if (state.round >= state.rounds) state.copy(phase = Phase.FINAL) else state.copy(round = state.round + 1, phase = Phase.RUNNING, result = null) }
}
