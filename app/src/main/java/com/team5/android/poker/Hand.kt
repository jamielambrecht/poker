package com.team5.android.poker

enum class Numbers {
    zero, one, two, three, four
}

enum class HandRanks {
    HighestCard, Pair, TwoPair, ThreeOfAKind,
    Straight, Flush, FullHouse, FourOfAKind,
    StraightFlush, RoyalFlush;

    override fun toString() : String {
        val strings = listOf<String>(
                "High Card", "Pair", "Two Pair", "Three of A Kind", "Straight",
                "Flush", "Full House", "Four Of A Kind", "Straight Flush", "Royal Flush"
        )
        return strings[this.ordinal]
    }
}

class Hand(owner: String?) {
    private val cards: MutableList<Card> = mutableListOf<Card>()
    private var handRank = HandRanks.HighestCard
    private var tieBreakers = IntArray(3)
    private val owner = owner

    fun addCards(cards: List<Card>) {
        this.cards += cards
    }

    fun addCardsAtBoolIndices(cards: List<Card>, boolIndices: BooleanArray) {
        val indices = mutableListOf<Int>()
        for ((i, bool) in boolIndices.withIndex()) {
            if (bool) {
                indices.add(i)
            }
        }
        for (card in cards) {
            this.cards.add(indices.removeFirst(), card)
        }
    }

    fun returnCards(cards : MutableList<Card>) : MutableList<Card> {
        val toDeck = mutableListOf<Card>()
        for (card in cards) {
            this.cards.remove(card)
            toDeck.add(card)
        }
        return toDeck
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
    fun setHandRanks() : HandRanks {
        val cards = this.cards.toMutableList()
        cards.sortBy { it.rank }
        var howManyByRank = IntArray(14)
        var rankAtMatchLevel = IntArray(4)
        var firstPairRank: Int = 0
        var pairs: Int = 0
        handRank = HandRanks.HighestCard
        val singleCardsRanks = mutableListOf<Int>()

        // Count number of each card rank to find pairs etc.
        for (rank in 1..13) {
            for (card in cards) {
                howManyByRank[rank] += if (rank == card.rank) 1 else 0
                // Count Pairs
            }
            if (howManyByRank[rank] == 1) {
                // Save rank of single cards to determine highest card outside of pairs
                singleCardsRanks.add(rank)
            }
            if (howManyByRank[rank] == 2) {
                rankAtMatchLevel[HandRanks.Pair.ordinal] = rank
                if (pairs == 0) {firstPairRank = rank}
                ++pairs
            }
        }

        rankAtMatchLevel[0] = singleCardsRanks.maxOf { it }

        handRank = HandRanks.values()[pairs]
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
                handRank = HandRanks.Flush
            }
        }
        if (handRank == HandRanks.ThreeOfAKind && pairs > 0) {
            handRank = HandRanks.FullHouse
        }
        if (howManyByRank.maxOf {it.toInt()} == 4) { handRank = HandRanks.FourOfAKind }

        if (handRank == HandRanks.StraightFlush && cards.first().rank == 9) {
            handRank = HandRanks.RoyalFlush
        }

        //  Set tie-breakers
        if (handRank == HandRanks.HighestCard)  {
            tieBreakers[0] = rankAtMatchLevel[0]
            singleCardsRanks.remove( singleCardsRanks.maxOf {it} )
            tieBreakers[1] = singleCardsRanks.maxOf {it}
            singleCardsRanks.remove( singleCardsRanks.maxOf {it} )
            tieBreakers[2] = singleCardsRanks.maxOf {it}}

        if (handRank == HandRanks.Pair)         {
            tieBreakers[0] = rankAtMatchLevel[1]
            tieBreakers[1] = rankAtMatchLevel[0]
            singleCardsRanks.remove( singleCardsRanks.maxOf {it} )
            tieBreakers[2] = singleCardsRanks.maxOf {it}}

        if (handRank == HandRanks.TwoPair)      {
            tieBreakers[0] = rankAtMatchLevel[1]
            tieBreakers[1] = firstPairRank
            tieBreakers[2] = rankAtMatchLevel[0] }

        if (handRank == HandRanks.ThreeOfAKind) {
            tieBreakers[0] = rankAtMatchLevel[2]
            tieBreakers[1] = rankAtMatchLevel[0]
            singleCardsRanks.remove( singleCardsRanks.maxOf {it} )
            tieBreakers[2] = singleCardsRanks.maxOf {it}
        }

        if (handRank == HandRanks.Straight)     {
            tieBreakers[0] = rankAtMatchLevel[0]
            tieBreakers[1] = cards[4].suit.ordinal
            tieBreakers[2] = cards[3].suit.ordinal
        }

        if (handRank == HandRanks.Flush)        {
            tieBreakers[0] = rankAtMatchLevel[0]
            tieBreakers[1] = cards.first().suit.ordinal
            singleCardsRanks.remove( singleCardsRanks.maxOf {it} )
            tieBreakers[2] = singleCardsRanks.maxOf {it}
        }

        if (handRank == HandRanks.FullHouse)    {
            tieBreakers[0] = rankAtMatchLevel[2]
            tieBreakers[1] = rankAtMatchLevel[1]
            tieBreakers[2] = cards[4].suit.ordinal
        }

        if (handRank == HandRanks.FourOfAKind)    {
            tieBreakers[0] = rankAtMatchLevel[3]
            tieBreakers[1] = rankAtMatchLevel[0]
            tieBreakers[2] = cards[4].suit.ordinal
        }

        if (handRank == HandRanks.StraightFlush) {
            tieBreakers[0] = rankAtMatchLevel[0]
            tieBreakers[1] = cards.first().suit.ordinal
            tieBreakers[2] = 0
        }
        if (handRank == HandRanks.RoyalFlush)   {
            tieBreakers[0] = cards.first().suit.ordinal
            tieBreakers[1] = 0
            tieBreakers[2] = 0
        }

        return handRank
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

    fun getHandRankAtLevel(level : Int): Int {
        when (level) {
            0 -> return handRank.ordinal
            1 -> return tieBreakers[0]
            2 -> return tieBreakers[1]
        }
        return -1
    }

    fun getCards(): MutableList<Card> {
        return cards
    }
}