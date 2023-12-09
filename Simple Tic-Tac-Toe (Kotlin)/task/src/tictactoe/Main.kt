package tictactoe

data class Cell(val ch: Char) {
    override fun toString() = ch.toString()
}

interface Grid {
    val cells: List<MutableList<Cell>>
}

interface StringParsable {
    fun fromString(input: String)
}

interface Displayable {
    fun display(grid: Grid)
}

class SimpleGrid : Grid, StringParsable {
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

    override fun fromString(input: String) {
        cells = input.chunked(3).map { row ->
            row.map { ch ->
                Cell(ch)
            }.toMutableList()
        }
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
        val string = readln()

        display.display(grid)
    }
}

fun main() {
    val grid: Grid = SimpleGrid()
    val display: Displayable = ConsoleDisplay()
    val game = TicTacToe(grid, display)
    game.start()
}
