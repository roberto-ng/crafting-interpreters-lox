import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.system.exitProcess

class Lox {
    companion object {
        var hadError = false
        val keywords = getKeywordMap()

        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        private fun report(line: Int, where: String, message: String) {
            println("[line $line] Error $where: $message")
            hadError = true
        }

        private fun getKeywordMap(): HashMap<String, TokenType> {
            return hashMapOf(
                "and" to TokenType.AND,
                "class" to TokenType.CLASS,
                "else" to TokenType.ELSE,
                "false" to TokenType.FALSE,
                "for" to TokenType.FOR,
                "fun" to TokenType.FUN,
                "if" to TokenType.IF,
                "nil" to TokenType.NIL,
                "or" to TokenType.OR,
                "print" to TokenType.PRINT,
                "return" to TokenType.RETURN,
                "super" to TokenType.SUPER,
                "this" to TokenType.THIS,
                "true" to TokenType.TRUE,
                "var" to TokenType.VAR,
                "while" to TokenType.WHILE,
            )
        }
    }

    fun runFile(path: String) {
        val file = File(path).readText()
        run(file)
    }

    fun runPrompt() {
        val input = InputStreamReader(System.`in`)
        val reader = BufferedReader(input)

        while (true) {
            print("> ")

            val line = reader.readLine() ?: break
            run(line)
        }
    }

    private fun run(source: String) {
        if (hadError) {
            exitProcess(65)
        }

        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        // for now, just print the tokens
        for (token in tokens) {
            println(token.toString())
        }
    }
}