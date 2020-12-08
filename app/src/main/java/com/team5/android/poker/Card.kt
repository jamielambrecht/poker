package com.team5.android.poker

enum class Suit {
    Clubs, Diamonds, Hearts, Spades
}

enum class Face {
    Jack, Queen, King, Ace
}

enum class Filenames {
    ace_of_spaces, etc, fioasfis, fuiaud;

    companion object {
        fun getByValue(value: Int) = Numbers.values().firstOrNull { it.ordinal == value }
    }
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

    fun getFilename() : String? {
        var filenameNumber : Int = this.suit.ordinal*13 + (this.rank - 1)
        return Filenames.getByValue(filenameNumber)!!.name
    }
}
