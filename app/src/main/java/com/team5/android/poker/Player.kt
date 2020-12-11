package com.team5.android.poker

class Player(val name: String) {
    val hand: Hand = Hand(name)
    var selectedCards = BooleanArray(5)

    fun displayHand() {
        hand.sortCards()
        println(name + "'s hand: ")
        hand.display()
    }
}