package com.github.stephenwanjala.tactoe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var buttons: Array<Array<Button>>
    private var playerTurn = true
    private var movesCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttons = Array(3) { row ->
            Array(3) { column ->
                initButton(row, column)
            }
        }

        val resetButton: Button = findViewById(R.id.btn_reset)
        resetButton.setOnClickListener {
            resetGame()
        }
    }

    private fun initButton(row: Int, column: Int): Button {
        val button: Button = findViewById(resources.getIdentifier("btn_$row$column", "id", packageName))
        button.setOnClickListener(this)
        return button
    }

    override fun onClick(view: View) {
        val button = view as Button
        if (button.text.toString() != "") {
            return
        }

        if (playerTurn) {
            button.text = "X"
        } else {
            button.text = "O"
        }

        movesCount++

        if (checkWin()) {
            val winner = if (playerTurn) "Player 1 (X)" else "Player 2 (O)"
            showResultDialog("Congratulations!", "$winner wins!")
            disableButtons()
        } else if (movesCount == 9) {
            showResultDialog("Draw!", "It's a draw!")
        } else {
            playerTurn = !playerTurn
        }
    }

    private fun checkWin(): Boolean {
        val board = Array(3) { row ->
            Array(3) { column ->
                buttons[row][column].text.toString()
            }
        }

        // Check rows
        for (i in 0 until 3) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != "") {
                return true
            }
        }

        // Check columns
        for (i in 0 until 3) {
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != "") {
                return true
            }
        }

        // Check diagonals
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != "") {
            return true
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != "") {
            return true
        }

        return false
    }

    private fun disableButtons() {
        for (row in buttons) {
            for (button in row) {
                button.isEnabled = false
            }
        }
    }

    private fun resetGame() {
        for (row in buttons) {
            for (button in row) {
                button.text = ""
                button.isEnabled = true
            }
        }
        playerTurn = true
        movesCount = 0
    }


    private fun showResultDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("New Game") { _, _ ->
                resetGame()
            }
            .setCancelable(false)
        builder.create().show()
    }
}


