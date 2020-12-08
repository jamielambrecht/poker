package com.team5.android.poker

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by lazy {
        ViewModelProvider(this).get(GameViewModel::class.java)
    }

    private lateinit var playerCard1 : ImageView
    private lateinit var playerCard2 : ImageView
    private lateinit var playerCard3 : ImageView
    private lateinit var playerCard4 : ImageView
    private lateinit var playerCard5 : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerCard1 = findViewById(R.id.card1)
        playerCard2 = findViewById(R.id.card2)
        playerCard3 = findViewById(R.id.card3)
        playerCard4 = findViewById(R.id.card4)
        playerCard5 = findViewById(R.id.card5)

        var playerCardImageArray : MutableList<ImageView> = mutableListOf<ImageView>()
        playerCardImageArray.add(playerCard1)
        playerCardImageArray.add(playerCard2)
        playerCardImageArray.add(playerCard3)
        playerCardImageArray.add(playerCard4)
        playerCardImageArray.add(playerCard5)


    }



    fun updateCardsUI(playerCardImageArray: MutableList<ImageView>) {
        for (i in 0..4) {
            val stringGenerated = gameViewModel.player.hand.getCardAt(i).getFilename()
            val id = resources.getIdentifier("yourpackagename:drawable/$stringGenerated", null, null)
            playerCardImageArray[i].setImageResource(id)
        }
    }

}


//fun main(args: Array<String>) {
//    val deck = Deck()
//    val player = Player("Player")
//    val dealer = Player("Dealer")
//    var winner: Player?
//    //deck.display() //DEBUG
//    var gameOn: Boolean = true
//    while (gameOn) {
//        deck.shuffleCards()
//        player.hand.addCards(deck.dealCards(5))
//        dealer.hand.addCards(deck.dealCards(5))
//        player.displayHand()
//        player.hand.setHandRanks()
//
//        println("Enter card index (1-5) to return to dealer (0 when finished)")
//        var inputNumber: Number = -1
//        var cardsReturned: Int = 0
//        while (inputNumber != 0 && player.hand.isNotEmpty()) {
//            inputNumber = Integer.valueOf(readLine())
//            if (inputNumber < player.hand.size() + 1 && inputNumber > 0) {
//                deck.addCards(listOf(player.hand.returnCard(inputNumber - 1)))
//                ++cardsReturned
//            }
//            player.displayHand()
//        }
//        if (cardsReturned > 0) {
//            player.hand.addCards(deck.dealCards(cardsReturned))
//        }
//        player.displayHand()
//        player.hand.setHandRanks()
//        dealer.hand.setHandRanks()
//
//        if (player.hand.getPrimaryHandRank() == dealer.hand.getPrimaryHandRank()) {
//            if (player.hand.getSecondaryHandRank() == dealer.hand.getSecondaryHandRank()) {
//                winner = if (player.hand.getTieBreaker() == dealer.hand.getTieBreaker()) {
//                    null
//                } else {
//                    if (player.hand.getTieBreaker() > dealer.hand.getTieBreaker()) {
//                        player
//                    } else {
//                        dealer
//                    }
//                }
//            } else {
//                winner = if (player.hand.getSecondaryHandRank() > dealer.hand.getSecondaryHandRank()) {
//                    player
//                } else {
//                    dealer
//                }
//            }
//        } else {
//            winner = if (player.hand.getPrimaryHandRank() > dealer.hand.getPrimaryHandRank()) {
//                player
//            } else {
//                dealer
//            }
//        }
//
//        if (winner == null) {
//            println("Tie game!")
//        } else {
//            println("The winner is " + winner.name)
//        }
//        deck.addCards(player.hand.returnAllCards())
//        deck.addCards(dealer.hand.returnAllCards())
//        println("Continue playing? Y/n")
//        if (readLine()!!.toLowerCase() == "n") {
//            gameOn = false
//        }
//    }
//}