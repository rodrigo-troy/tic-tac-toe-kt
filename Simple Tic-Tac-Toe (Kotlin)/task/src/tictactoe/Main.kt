package tictactoe

data class Cell(val ch: Char) {
    override fun toString() = ch.toString()

    companion object {
        val EMPTY: Cell = Cell('_')
        val O: Cell = Cell('O')
        val X: Cell = Cell('X')
    }
}

data class GameState(
    val isXWins: Boolean,
    val isOWins: Boolean,
    val isDraw: Boolean,
    val isImpossible: Boolean
)


interface Grid {
    val cells: MutableList<MutableList<Cell>>
}

interface Displayable {
    fun display(grid: Grid)
}

class RandomGrid : Grid {
    override val cells = List(3) { List(3) { Cell.EMPTY }.toMutableList() }.toMutableList()

    fun populateRandomly() {
        val charList = mutableListOf('X', 'O')

        val emptyCells = cells.withIndex().flatMap { (i, row) ->
            row.withIndex().filter { (_, cell) -> cell.ch == '_' }.map { (j, _) -> i to j }
        }.toMutableList()

        while (emptyCells.isNotEmpty()) {
            val (i, j) = emptyCells.random()
            emptyCells.remove(i to j)
            val randomChar = charList[0]
            cells[i][j] = Cell(randomChar)
            charList.add(charList.removeAt(0))
        }
    }
}

class UserDefinedGrid(input: String) : Grid {
    override val cells = input.chunked(3).map { it.map { Cell(it) }.toMutableList() }.toMutableList()
}

class EmptyGrid : Grid {
    override val cells = List(3) { List(3) { Cell.EMPTY }.toMutableList() }.toMutableList()
}

class ConsoleDisplay : Displayable {
    override fun display(grid: Grid) {
        println("---------")
        grid.cells.forEach(::displayRow)
        println("---------")
    }

    private fun displayRow(row: List<Cell>) {
        println("| ${row.joinToString(" ")} |")
    }
}

open class TicTacToe(private val grid: Grid, private val display: Displayable) {
    private var gameState: GameState = GameState(isXWins = false, isOWins = false, isDraw = false, isImpossible = false)

    open fun start() {
        display.display(grid)
        updateGameState(grid)
        printGameState()
    }

    open fun getGameState(): GameState {
        return this.gameState.copy()
    }

    fun updateGameState(grid: Grid) {
        val isXWins = hasThreeInARow(grid, Cell.X)
        val isOWins = hasThreeInARow(grid, Cell.O)
        val isDraw = checkGameDraw(isXWins, isOWins)
        val isImpossible = checkGameImpossible(isXWins, isOWins)
        this.gameState = GameState(isXWins, isOWins, isDraw, isImpossible)
    }

    fun printGameState() {
        when {
            gameState.isImpossible -> println("Impossible")
            gameState.isXWins -> println("X wins")
            gameState.isOWins -> println("O wins")
            gameState.isDraw -> println("Draw")
            else -> println("Game not finished")
        }
    }

    private fun checkGameImpossible(isXWins: Boolean, isOWins: Boolean): Boolean {
        val flattenCells = grid.cells.flatten()
        val countDifference = Math.abs(flattenCells.count { it == Cell.X } - flattenCells.count { it == Cell.O })
        return isXWins && isOWins || countDifference >= 2
    }

    private fun checkGameDraw(isXWins: Boolean, isOWins: Boolean): Boolean {
        val flattenCells = grid.cells.flatten()
        return !isXWins && !isOWins && flattenCells.none { it == Cell.EMPTY }
    }

    private fun hasThreeInARow(grid: Grid, cell: Cell): Boolean {
        val combinations = listOf(
            listOf(grid.cells[0][0], grid.cells[0][1], grid.cells[0][2]),
            listOf(grid.cells[1][0], grid.cells[1][1], grid.cells[1][2]),
            listOf(grid.cells[2][0], grid.cells[2][1], grid.cells[2][2]),
            listOf(grid.cells[0][0], grid.cells[1][0], grid.cells[2][0]),
            listOf(grid.cells[0][1], grid.cells[1][1], grid.cells[2][1]),
            listOf(grid.cells[0][2], grid.cells[1][2], grid.cells[2][2]),
            listOf(grid.cells[0][0], grid.cells[1][1], grid.cells[2][2]),
            listOf(grid.cells[0][2], grid.cells[1][1], grid.cells[2][0])
        )

        return combinations.any { combination -> combination.all { it == cell } }
    }
}

class InteractiveTicTacToe(private val grid: Grid, private val display: Displayable) : TicTacToe(grid, display) {
    override fun start() {
        display.display(grid)
        play()
    }

    fun play() {
        var currentCell = Cell.X

        while (true) {
            val (i, j) = readCoordinates()

            if (grid.cells[i][j] != Cell.EMPTY) {
                println("This cell is occupied! Choose another one!")
                continue
            }

            grid.cells[i][j] = currentCell

            display.display(grid)

            this.updateGameState(grid)

            val currentState = getGameState()

            if (currentState.isXWins || currentState.isOWins || currentState.isDraw) {
                break
            }

            currentCell = if (currentCell == Cell.X) Cell.O else Cell.X
        }

        printGameState()
    }

    private fun readCoordinates(): Pair<Int, Int> {
        while (true) {
            val (i, j) = readln().split(" ").map { it.toIntOrNull() }

            if (i == null || j == null) {
                println("You should enter numbers!")
                continue
            }

            if (i !in 1..3 || j !in 1..3) {
                println("Coordinates should be from 1 to 3!")
                continue
            }

            return i - 1 to j - 1
        }
    }
}

fun main() {
    val grid: Grid = EmptyGrid()
    val display: Displayable = ConsoleDisplay()
    val game = InteractiveTicTacToe(grid, display)
    game.start()
}
