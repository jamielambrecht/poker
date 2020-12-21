package com.team5.android.poker

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_STATE = "GameState"
private const val KEY_PLAYER = "Player"
private const val KEY_DEALER = "Dealer"
private const val KEY_DECK = "Deck"

class MainActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by lazy {
        ViewModelProvider(this).get(GameViewModel::class.java)
    }

    private lateinit var playerCardImageViews : MutableList<ImageView>
    private lateinit var dealerCardImageViews : MutableList<ImageView>
    private lateinit var playerCardParams : ConstraintLayout.LayoutParams
    private lateinit var dealButton: Button
    private lateinit var drawButton: Button
    private lateinit var playerHandText: TextView
    private lateinit var dealerHandText: TextView
    private lateinit var dealerMoveText: TextView
    private lateinit var winnerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerCardImageViews = mutableListOf()
        playerCardImageViews.add(findViewById(R.id.card1))
        playerCardImageViews.add(findViewById(R.id.card2))
        playerCardImageViews.add(findViewById(R.id.card3))
        playerCardImageViews.add(findViewById(R.id.card4))
        playerCardImageViews.add(findViewById(R.id.card5))

        dealerCardImageViews = mutableListOf()
        dealerCardImageViews.add(findViewById(R.id.d_card1))
        dealerCardImageViews.add(findViewById(R.id.d_card2))
        dealerCardImageViews.add(findViewById(R.id.d_card3))
        dealerCardImageViews.add(findViewById(R.id.d_card4))
        dealerCardImageViews.add(findViewById(R.id.d_card5))
        dealerHandText = findViewById(R.id.dealer_hand_text)
        dealerMoveText = findViewById(R.id.dealer_move)
        winnerTextView = findViewById(R.id.winner_text)

        dealButton = findViewById(R.id.deal_button)
        drawButton = findViewById(R.id.draw_button)
        playerHandText = findViewById(R.id.player_hand_text)

        // We tried to use saveInstanceState but we had trouble structuring our implementation of
        // Parcelable for custom Object classes, though we did learn a bit about how it works.

//        gameViewModel.deck = savedInstanceState?.getParcelable(KEY_DECK) ?: gameViewModel.deck
//        gameViewModel.player = savedInstanceState?.getParcelable(KEY_DECK) ?: gameViewModel.player
//        gameViewModel.dealer = savedInstanceState?.getParcelable(KEY_DEALER) ?: gameViewModel.dealer

//        val currentStateAsInt = savedInstanceState?.getInt(KEY_STATE, 0) ?: 0
//        gameViewModel.gameState = States.values()[currentStateAsInt]

        updateCardsViewDrawables()
        updateDealerCardsViewDrawables()
        updateWinnerText()
        updateButtons()

        for (i in 0..4) {
            playerCardImageViews[i].setOnClickListener {
                toggleCardSelection(i)
            }
        }

        dealButton.setOnClickListener {
            //  Shuffle the Deck
            gameViewModel.deck.shuffleCards()
            //  Deal 5 Cards to each Player's Hand
            gameViewModel.player.hand.addCards(gameViewModel.deck.dealCards(5))
            gameViewModel.dealer.hand.addCards(gameViewModel.deck.dealCards(5))
            //  Set the Game State to the next step
            gameViewModel.gameState = States.FIRSTHAND
            //  Update the UI accordingly
            updateCardsViewDrawables()
            updateDealerCardsViewDrawables()
            updateButtons()
            updateWinnerText()
        }

        drawButton.setOnClickListener {
            val cardsToReturn : MutableList<Card> = mutableListOf()
//            var numberOfCardsToDraw : Int = 0
            for (i in 0..4) {
                if (gameViewModel.cardSelected[i]) {
                    cardsToReturn.add(gameViewModel.player.hand.getCardAt(i))
//                    numberOfCardsToDraw++
                }
            }
            // add all returned cards from all players to deck before shuffling and dealing!
            gameViewModel.deck.addCards(gameViewModel.player.hand.returnCards(cardsToReturn))

            // These must be separate steps!
            gameViewModel.deck.shuffleCards()
            gameViewModel.player.hand.addCardsAtBoolIndices(
                    gameViewModel.deck.dealCards(cardsToReturn.size),
                    gameViewModel.cardSelected
            )

            cardsToReturn.clear()

            gameViewModel.player.hand.setHandRanks()
            gameViewModel.dealer.hand.setHandRanks()

            determineWinner()

            gameViewModel.gameState = States.SECONDHAND
            resetCardSelections()
            updateCardsViewDrawables()
            updateDealerCardsViewDrawables()
            updateWinnerText()
            updateButtons()
            gameViewModel.deck.addCards(gameViewModel.player.hand.returnAllCards())
            gameViewModel.deck.addCards(gameViewModel.dealer.hand.returnAllCards())

        }
    }

    private fun determineWinner() {
        gameViewModel.tieGame = false
        if (gameViewModel.player.hand.getHandRankAtLevel(0) == gameViewModel.dealer.hand.getHandRankAtLevel(0)) {
            if (gameViewModel.player.hand.getHandRankAtLevel(1) == gameViewModel.dealer.hand.getHandRankAtLevel(1)) {
                if (gameViewModel.player.hand.getHandRankAtLevel(2) == gameViewModel.dealer.hand.getHandRankAtLevel(2)) {
                    if (gameViewModel.player.hand.getHandRankAtLevel(3) == gameViewModel.dealer.hand.getHandRankAtLevel(3)) {
                        gameViewModel.tieGame = true
                    } else {
                        if (gameViewModel.player.hand.getHandRankAtLevel(3) > gameViewModel.dealer.hand.getHandRankAtLevel(3)) {
                            gameViewModel.winner = gameViewModel.player
                        } else {
                            gameViewModel.winner = gameViewModel.dealer
                        }
                    }
                } else {
                    if (gameViewModel.player.hand.getHandRankAtLevel(2) > gameViewModel.dealer.hand.getHandRankAtLevel(2)) {
                        gameViewModel.winner = gameViewModel.player
                    } else {
                        gameViewModel.winner = gameViewModel.dealer
                    }
                }
            } else {
                if (gameViewModel.player.hand.getHandRankAtLevel(1) > gameViewModel.dealer.hand.getHandRankAtLevel(1)) {
                    gameViewModel.winner = gameViewModel.player
                } else {
                    gameViewModel.winner = gameViewModel.dealer
                }
            }
        } else {
            if (gameViewModel.player.hand.getHandRankAtLevel(0) > gameViewModel.dealer.hand.getHandRankAtLevel(0)) {
                gameViewModel.winner = gameViewModel.player
            } else {
                gameViewModel.winner = gameViewModel.dealer
            }
        }
    }

    private fun updateButtons() {
        if (gameViewModel.gameState == States.BEGIN) {
            drawButton.isEnabled = false
            dealerHandText.text = resources.getText(R.string.dealer_hand_default_text)
        }
        if (gameViewModel.gameState == States.FIRSTHAND) {
            drawButton.isEnabled = true
            dealButton.isEnabled = false
            dealerHandText.text = resources.getText(R.string.dealer_hand_default_text)
            playerHandText.text = gameViewModel.player.hand.setHandRanks().toString()
        }
        if (gameViewModel.gameState == States.SECONDHAND) {
            drawButton.isEnabled = false
            dealButton.isEnabled = true
            playerHandText.text = gameViewModel.player.hand.setHandRanks().toString()
            dealerHandText.text = gameViewModel.dealer.hand.setHandRanks().toString()
        }

    }

    private fun updateWinnerText() {
        if (gameViewModel.gameState == States.SECONDHAND) {
            if (gameViewModel.tieGame == true) {
                winnerTextView.setText(resources.getText(R.string.tie_game))
            } else {
                winnerTextView.setText(resources.getText(R.string.the_winner_is).toString() + gameViewModel.winner.name)
            }
        } else {
            winnerTextView.setText("")
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_STATE, gameViewModel.gameState.ordinal)
//        outState.putParcelable(KEY_PLAYER, gameViewModel.player)
//        outState.putParcelable(KEY_DEALER, gameViewModel.dealer)
//        outState.putParcelable(KEY_DECK, gameViewModel.deck)
    }

    //    private fun selectDeselectCard(card: ConstraintLayout.LayoutParams, index: Int) {
    private fun toggleCardSelection(index: Int) {
        gameViewModel.cardSelected[index] = !gameViewModel.cardSelected[index]
        updateSelectedCardImageViewVerticalBias(index)
    }

    private fun resetCardSelections() {
        for (index in 0..4) {
            gameViewModel.cardSelected[index] = false
            updateSelectedCardImageViewVerticalBias(index)
        }
    }

    private fun updateSelectedCardImageViewVerticalBias(index: Int) {
        playerCardParams = playerCardImageViews[index].layoutParams as ConstraintLayout.LayoutParams
        if (gameViewModel.cardSelected[index]) {
            playerCardParams.verticalBias = 0.8f
        } else {
            playerCardParams.verticalBias = 0.85f
        }
        playerCardImageViews[index].requestLayout()
    }

    private fun updateCardsViewDrawables() {
        for ((i, card) in gameViewModel.player.hand.getCards().withIndex()) {
            // based on example from stackoverflow:
            val filename = card.getFilename()
            val id = resources.getIdentifier("com.team5.android.poker:drawable/$filename", null, null)
            playerCardImageViews[i].setImageResource(id)
        }

    }

    private fun updateDealerCardsViewDrawables() {

        if (gameViewModel.gameState == States.FIRSTHAND) {
            for ((i, card) in gameViewModel.dealer.hand.getCards().withIndex()) {
                dealerCardImageViews[i].setImageResource(R.drawable.back_of_card)
            }
        } else if (gameViewModel.gameState == States.SECONDHAND) {
            for ((i, card) in gameViewModel.dealer.hand.getCards().withIndex()) {
                // based on example from stackoverflow:
                val filename = card.getFilename()
                val id = resources.getIdentifier("com.team5.android.poker:drawable/$filename", null, null)
                dealerCardImageViews[i].setImageResource(id)
            }
        }
    }

}