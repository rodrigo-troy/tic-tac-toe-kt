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
}

interface Grid {
    val cells: List<MutableList<Cell>>
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
    override var cells: List<MutableList<Cell>> = listOf(
        mutableListOf(Cell('_'), Cell('_'), Cell('_')),
        mutableListOf(Cell('_'), Cell('_'), Cell('_')),
        mutableListOf(Cell('_'), Cell('_'), Cell('_'))
    )

    init {
        val charList = input.toList().map { Cell(it) }
        cells = listOf(
            charList.subList(0, 3).toMutableList(),
            charList.subList(3, 6).toMutableList(),
            charList.subList(6, 9).toMutableList()
        )
    }
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
    }
}

fun main() {
    val grid: Grid = UserDefinedGrid(readln())
    val display: Displayable = ConsoleDisplay()
    val game = TicTacToe(grid, display)
    game.start()
}
