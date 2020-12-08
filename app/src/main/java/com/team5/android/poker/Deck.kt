package com.team5.android.poker

class Deck() {

    private val cards: MutableList<Card> = mutableListOf<Card>()

    init {
        for (suit in Suit.values()) {
            for (rank in 1..13) {
                cards.add(Card(suit, rank))
            }
        }
    }

    fun addCards(cards: List<Card>) {
        this.cards += cards
    }

    fun shuffleCards() {
        cards.shuffle()
    }

    fun dealCards(quantity : Int) : List<Card> {
        return List(quantity) {this.cards.removeFirst()}
    }

    fun display() {
        for (card in cards) {
            println(card)
        }
    }

}