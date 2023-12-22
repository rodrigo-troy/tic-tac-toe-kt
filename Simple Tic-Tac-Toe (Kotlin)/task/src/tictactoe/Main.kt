package tictactoe

data class Cell(val ch: Char) {
    override fun toString() = ch.toString()

    fun isEmpty() = ch == '_'
    fun isNotEmpty() = !isEmpty()
    fun isX() = ch == 'X'
    fun isO() = ch == 'O'
    fun isNotX() = !isX()
    fun isNotO() = !isO()
    fun isNot(ch: Char) = this.ch != ch

    companion object {
        val EMPTY: Cell = Cell('_')
        val O: Cell = Cell('O')
        val X: Cell = Cell('X')
    }
}

interface Grid {
    val cells: List<List<Cell>>
}

interface Displayable {
    fun display(grid: Grid)
}

class RandomGrid : Grid {
    override var cells: List<MutableList<Cell>> = listOf(
        mutableListOf(Cell('_'), Cell('_'), Cell('_')),
        mutableListOf(Cell('_'), Cell('_'), Cell('_')),
        mutableListOf(Cell('_'), Cell('_'), Cell('_'))
    )

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
    override val cells = input.chunked(3).map { it.map { Cell(it) } }
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

class TicTacToe(private val grid: Grid, private val display: Displayable) {
    fun start() {
        display.display(grid)
        analyzeGameState(grid)
    }

    private fun analyzeGameState(grid: Grid) {
        val isXWins = hasThreeInARow(grid, Cell.X)
        val isOWins = hasThreeInARow(grid, Cell.O)
        val isDraw = checkGameDraw(isXWins, isOWins)
        val isImpossible = checkGameImpossible(isXWins, isOWins)

        printGameState(isImpossible, isDraw, isXWins, isOWins)
    }

    private fun printGameState(isImpossible: Boolean, isDraw: Boolean, isXWins: Boolean, isOWins: Boolean) {
        when {
            isImpossible -> println("Impossible")
            isDraw -> println("Draw")
            isXWins -> println("X wins")
            isOWins -> println("O wins")
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

fun main() {
    val grid: Grid = UserDefinedGrid(readln())
    val display: Displayable = ConsoleDisplay()
    val game = TicTacToe(grid, display)
    game.start()
}
