package com.team5.android.poker

import androidx.lifecycle.ViewModel

enum class States {
    BEGIN, FIRSTHAND, SECONDHAND
}

class GameViewModel : ViewModel() {

    private val theDeck = Deck()
    val player = Player("Player")
    val dealer = Player("Dealer")
    //var winner: Player?
    private val cardsSelected = BooleanArray(5)
    var gameState: States = States.BEGIN

    val cardSelected: BooleanArray
        get() = cardsSelected

    val deck: Deck
        get() = theDeck
}