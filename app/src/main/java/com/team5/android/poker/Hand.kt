package com.team5.android.poker

enum class Numbers {
    zero, one, two, three, four;

    companion object {
        fun getByValue(value: Int) = values().firstOrNull { it.ordinal == value }
    }
}

enum class HandRanks {
    HighestCard, Pair, TwoPair, ThreeOfAKind,
    Straight, Flush, FullHouse, FourOfAKind,
    StraightFlush, RoyalFlush;

    companion object {
        fun getByValue(value: Int) = values().firstOrNull { it.ordinal == value }
    }
}

class Hand(owner: String?) {
    private val cards: MutableList<Card> = mutableListOf<Card>()
    private var handRank = HandRanks.HighestCard
    private var tieBreakers = IntArray(2)
    private val owner = owner

    fun addCards(cards: List<Card>) {
        this.cards += cards
    }

    fun returnCards(indices : IntArray) : List<Card> {
        var cards: MutableList<Card> = mutableListOf<Card>()
        for (i in indices) {
            cards.add(this.cards.removeAt(i))
        }
        return cards.toList()
    }

    fun returnCard(index: Int): Card {
        return cards.removeAt(index)
    }

    fun returnAllCards() : List<Card> {
        return List(cards.size) {this.cards.removeFirst()}
    }

    fun getCardAt(index: Int) : Card {
        return cards[index]
    }

    fun display() {
        for (card in cards) {
            print("$card   ")
        }
        println()
    }

    /*	10 Hand Ranks by index: 0. Highest Card, 1. Pair, 2. Two Pair, 3. Three-of-a-kind,
    4. Straight, 5. Flush, 6. Full House, 7. Four-of-A-Kind,
    8. Straight Flush, 9. Royal Flush
    NOTE: Straights start at Ace (??)
    */
    fun setHandRanks() {
        cards.sortBy { it.rank }
        var howManyByRank = IntArray(14)
        var rankAtMatchLevel = IntArray(4)
        var firstPairRank: Int = 0
        var pairs: Int = 0
        handRank = HandRanks.HighestCard

        // Set highest card
        rankAtMatchLevel[0] = cards.maxOf { it.rank }
        println("Highest card: " + Card.rankToString(rankAtMatchLevel[0]))

        // Count number of each card rank to find pairs etc.
        print("$owner has ")
        for (rank in 1..13) {
            for (card in cards) {
                howManyByRank[rank] += if (rank == card.rank) 1 else 0
                // Count Pairs
            }
            if (howManyByRank[rank] == 2) {
                rankAtMatchLevel[HandRanks.Pair.ordinal] = rank
                if (pairs == 0) {firstPairRank = rank}
                ++pairs
            }
            if (howManyByRank[rank] > 0) {
                print( Numbers.getByValue(howManyByRank[rank])!!.name + " " + Card.rankToString(rank) + ", " )
            }
        }
        println()

        handRank = HandRanks.getByValue(pairs)!!
        if (howManyByRank.maxOf {it.toInt()} == 3) { handRank = HandRanks.ThreeOfAKind }

        var straight : Boolean = true
        for (i in 1..4) {
            if (cards.elementAt(i).rank - cards.elementAt(i-1).rank != 1 ) {
                //  Special case: A2345 ie 2345A
                if (i == 4) {
                    if (cards.elementAt(i).rank == 13 && cards.elementAt(i-1).rank == 4) {
                        rankAtMatchLevel[0] = 4 // Treat 5 as highest card so that A2345 does not tie with 10JQKA
                        break
                    }
                }
                straight = false
            }
        }
        if (straight) { handRank = HandRanks.Straight }

        if (cards.all { it.suit == cards.first().suit }) {
            if (handRank == HandRanks.Straight) {
                handRank = HandRanks.StraightFlush
            } else {
                handRank == HandRanks.Flush
            }
        }

        if (handRank == HandRanks.StraightFlush && cards.first().rank == 9) {
            handRank = HandRanks.RoyalFlush
        }

        if (handRank == HandRanks.ThreeOfAKind && pairs > 0) {
            handRank = HandRanks.FullHouse
        }
        if (howManyByRank.maxOf {it.toInt()} == 4) { handRank = HandRanks.FourOfAKind }

        if (handRank == HandRanks.HighestCard)  { tieBreakers[0] = rankAtMatchLevel[0] }
        if (handRank == HandRanks.Pair)         { tieBreakers[0] = rankAtMatchLevel[1]; tieBreakers[1] = rankAtMatchLevel[0] }
        if (handRank == HandRanks.TwoPair)      { tieBreakers[0] = rankAtMatchLevel[1]; tieBreakers[1] = firstPairRank }
        if (handRank == HandRanks.ThreeOfAKind) { tieBreakers[0] = rankAtMatchLevel[0] }
        if (handRank == HandRanks.Straight)     { tieBreakers[0] = rankAtMatchLevel[0] }
        if (handRank == HandRanks.Flush)        { tieBreakers[0] = rankAtMatchLevel[0] }
        if (handRank == HandRanks.FullHouse)    { tieBreakers[0] = rankAtMatchLevel[2]; tieBreakers[1] = rankAtMatchLevel[1] }
        if (handRank == HandRanks.FourOfAKind)    { tieBreakers[0] = rankAtMatchLevel[0] }
        if (handRank == HandRanks.StraightFlush) { tieBreakers[0] = rankAtMatchLevel[0] }
        if (handRank == HandRanks.RoyalFlush)   { tieBreakers[0] = rankAtMatchLevel[0] }

        println(owner + "'s current hand is a " + handRank.name)
    }

    fun isNotEmpty(): Boolean {
        return cards.size > 0
    }

    fun size(): Int {
        return cards.size
    }

    fun sortCards() {
        cards.sortBy { it.rank }
    }

    fun getPrimaryHandRank(): Int {
        return handRank.ordinal
    }

    fun getSecondaryHandRank(): Int {
        return tieBreakers[0]
    }

    fun getTieBreaker(): Int {
        return tieBreakers[1]
    }
}