package com.team5.android.poker

enum class Suit {
    Clubs, Diamonds, Hearts, Spades
}

enum class Face {
    Jack, Queen, King, Ace
}

enum class Filenames {
    two_of_clubs, three_of_clubs, four_of_clubs, five_of_clubs, six_of_clubs, seven_of_clubs,
    eight_of_clubs, nine_of_clubs, ten_of_clubs, jack_of_clubs, queen_of_clubs, king_of_clubs,
    ace_of_clubs, two_of_diamonds, three_of_diamonds, four_of_diamonds, five_of_diamonds,
    six_of_diamonds, seven_of_diamonds, eight_of_diamonds, nine_of_diamonds, ten_of_diamonds,
    jack_of_diamonds, queen_of_diamonds, king_of_diamonds, ace_of_diamonds, two_of_hearts,
    three_of_hearts, four_of_hearts, five_of_hearts, six_of_hearts, seven_of_hearts,
    eight_of_hearts, nine_of_hearts, ten_of_hearts, jack_of_hearts, queen_of_hearts,
    king_of_hearts, ace_of_hearts, two_of_spades, three_of_spades, four_of_spades, five_of_spades,
    six_of_spades, seven_of_spades, eight_of_spades, nine_of_spades, ten_of_spades, jack_of_spades,
    queen_of_spades, king_of_spades, ace_of_spades;

}

data class Card(val suit: Suit, val rank: Int) {
    override fun toString(): String {
        return rankToString(rank) + " of " + suit.name
    }

    companion object {
        fun rankToString(rank: Int): String {
            var string : String = ""
            if (rank + 1 <= 10) {
                string = (rank + 1).toString()
            } else {
                for (face in Face.values()) {
                    if (rank - 10 == face.ordinal)
                        string = face.name
                }
            }
            return string
        }
    }

    fun getFilename() : String {
        return Filenames.values()[this.suit.ordinal*13 + (this.rank - 1)].name
    }
}
