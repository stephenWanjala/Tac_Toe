package com.github.stephenwanjala.tactoe

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider

class MainActivity : AppCompatActivity(), View.OnClickListener, MenuProvider {
    private lateinit var buttons: Array<Array<Button>>
    private var playerTurn = true
    private var movesCount = 0
    private var isAgainstComputer = true


    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)

        toolbar.addMenuProvider(this, this)
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
        val button: Button =
            findViewById(resources.getIdentifier("btn_$row$column", "id", packageName))
        button.setOnClickListener(this)
        return button
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_two_players -> {
                isAgainstComputer = false
                resetGame()
                true
            }

            R.id.menu_against_computer -> {
                isAgainstComputer = true
                resetGame()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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
            val winner = if (playerTurn) {
                if (isAgainstComputer) "Player" else "Player X"
            } else {
                "Player O"
            }
            showResultDialog("Congratulations!", "$winner wins!")
            disableButtons()
        } else if (movesCount == 9) {
            showResultDialog("Draw!", "It's a draw!")
        } else {
            playerTurn = !playerTurn
            if (isAgainstComputer && !playerTurn) {
                makeComputerMove()
            }
        }
    }

    private fun makeComputerMove() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (row in buttons.indices) {
            for (column in buttons[row].indices) {
                if (buttons[row][column].text.toString() == "") {
                    emptyCells.add(Pair(row, column))
                }
            }
        }

        if (emptyCells.isNotEmpty()) {
            val randomIndex = (0 until emptyCells.size).random()
            val (row, column) = emptyCells[randomIndex]
            buttons[row][column].text = "O"
            movesCount++

            if (checkWin()) {
                showResultDialog("Sorry!", "Computer wins!")
                disableButtons()
            } else if (movesCount == 9) {
                showResultDialog("Draw!", "It's a draw!")
            }

            playerTurn = !playerTurn
        }
    }

    private fun checkWin(): Boolean {
        // Check rows
        for (row in buttons) {
            if (row[0].text == row[1].text && row[0].text == row[2].text && row[0].text != "") {
                return true
            }
        }

        // Check columns
        for (column in buttons[0].indices) {
            if (buttons[0][column].text == buttons[1][column].text && buttons[0][column].text == buttons[2][column].text && buttons[0][column].text != "") {
                return true
            }
        }

        // Check diagonals
        if (buttons[0][0].text == buttons[1][1].text && buttons[0][0].text == buttons[2][2].text && buttons[0][0].text != "") {
            return true
        }
        if (buttons[0][2].text == buttons[1][1].text && buttons[0][2].text == buttons[2][0].text && buttons[0][2].text != "") {
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

        if (isAgainstComputer && !playerTurn) {
            makeComputerMove()
        }
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_against_computer -> {
                isAgainstComputer = true
                updateToolbarTitle()
                showToast("Against Computer Mode Selected")
                true
            }

            R.id.menu_two_players -> {
                isAgainstComputer = false
                updateToolbarTitle()
                showToast("Two Players Mode Selected")
                true
            }

            else -> false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun updateToolbarTitle() {
        val gameModeText = if (isAgainstComputer) {
            getString(R.string.against_computer)
        } else {
            getString(R.string.two_players)
        }
        val gameModeMenuItem = toolbar.menu.findItem(R.id.game_mode)
        gameModeMenuItem.title = gameModeText
    }
}
