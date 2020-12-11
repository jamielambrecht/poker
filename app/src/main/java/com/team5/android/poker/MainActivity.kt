package com.team5.android.poker

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by lazy {
        ViewModelProvider(this).get(GameViewModel::class.java)
    }

    private lateinit var playerCardImageViews : MutableList<ImageView>
    private lateinit var playerCardParams : ConstraintLayout.LayoutParams
    private lateinit var dealButton: Button
    private lateinit var drawButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerCardImageViews = mutableListOf()
        playerCardImageViews.add(findViewById(R.id.card1))
        playerCardImageViews.add(findViewById(R.id.card2))
        playerCardImageViews.add(findViewById(R.id.card3))
        playerCardImageViews.add(findViewById(R.id.card4))
        playerCardImageViews.add(findViewById(R.id.card5))
        dealButton = findViewById(R.id.deal_button)
        drawButton = findViewById(R.id.draw_button)
        drawButton.isEnabled = false

        for (i in 0..4) {
            playerCardImageViews[i].setOnClickListener {
                toggleCardSelection(i)
            }
        }

        dealButton.setOnClickListener {
            gameViewModel.deck.shuffleCards()
            gameViewModel.player.hand.addCards(gameViewModel.deck.dealCards(5))
            gameViewModel.dealer.hand.addCards(gameViewModel.deck.dealCards(5))
            updateCardsViewDrawables()
            drawButton.isEnabled = true
            dealButton.isEnabled = false
        }

        drawButton.setOnClickListener {
            //gameViewModel.deck.addCards(listOf(player.hand.returnCard(inputNumber - 1)))
            val cardsToReturn : MutableList<Card> = mutableListOf()
            var numberOfCardsToDraw : Int = 0
            for (i in 0..4) {
                if (gameViewModel.cardSelected[i]) {
                    cardsToReturn.add(gameViewModel.player.hand.getCardAt(i))
                    numberOfCardsToDraw++
                }
            }
            gameViewModel.deck.addCards(gameViewModel.player.hand.returnCards(cardsToReturn))
            cardsToReturn.clear()
            // add all returned cards from all players to deck before shuffling and dealing!
            // These must be separate steps!
            gameViewModel.deck.shuffleCards()
            gameViewModel.player.hand.addCardsAtBoolIndices(
                    gameViewModel.deck.dealCards(numberOfCardsToDraw),
                    gameViewModel.cardSelected
                    )
            updateCardsViewDrawables()

        }
    }

//    private fun selectDeselectCard(card: ConstraintLayout.LayoutParams, index: Int) {
    private fun toggleCardSelection(index: Int) {
        playerCardParams = playerCardImageViews[index].layoutParams as ConstraintLayout.LayoutParams
        gameViewModel.cardSelected[index] = !gameViewModel.cardSelected[index]
        if (gameViewModel.cardSelected[index]) {
            playerCardParams.verticalBias = 0.8f
        } else {
            playerCardParams.verticalBias = 0.85f
        }
        playerCardImageViews[index].requestLayout()
        Log.i(TAG, gameViewModel.player.hand.getCardAt(index).toString())
    }

    private fun updateCardsViewDrawables() {
        for ((i, card) in gameViewModel.player.hand.getCards().withIndex()) {
            // based on example from stackoverflow:
            val filename = card.getFilename()
            val id = resources.getIdentifier("com.team5.android.poker:drawable/$filename", null, null)
            playerCardImageViews[i].setImageResource(id)
        }
    }

}


//fun main(args: Array<String>) {

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