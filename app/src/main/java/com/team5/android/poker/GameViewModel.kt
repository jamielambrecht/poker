package com.team5.android.poker

import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    val deck = Deck()
    val player = Player("Player")
    val dealer = Player("Dealer")
    //var winner: Player?
    val cardSelected = BooleanArray(5)
}