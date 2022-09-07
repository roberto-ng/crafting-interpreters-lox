class Scanner(val source: String) {
    private var tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): Collection<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        val char = advance()
        when (char) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> {
                if (match('=')) {
                    addToken(TokenType.BANG_EQUAL)
                } else {
                    addToken(TokenType.BANG)
                }
            }
            '=' -> {
                if (match('=')) {
                    addToken(TokenType.EQUAL_EQUAL)
                } else {
                    addToken(TokenType.EQUAL)
                }
            }
            '<' -> {
                if (match('=')) {
                    addToken(TokenType.LESS_EQUAL)
                } else {
                    addToken(TokenType.LESS)
                }
            }
            '>' -> {
                if (match('=')) {
                    addToken(TokenType.GREATER_EQUAL)
                } else {
                    addToken(TokenType.GREATER)
                }
            }
            '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) {
                        advance()
                    }
                } else {
                    addToken(TokenType.SLASH)
                }
            }
            '"' -> string()
            ' ', '\r', '\t' -> {}  // Ignore whitespace.
            '\n' -> {
                line += 1
            }
            else -> {
                if (char.isDigit()) {
                    number()
                } else if (char.isLetter()) {
                    identifier()
                } else {
                    Lox.error(line, "Unexpected character.")
                }
            }
        }
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line += 1
            }

            advance()
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.")
            return
        }

        // The closing ".
        advance()

        // Trim the surrounding quotes.
        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun number() {
        // continue while the next char is a digit
        while (peek().isDigit()) {
            advance()
        }

        // Look for a fractional part.
        if (peek() == '.' && peekNext().isDigit()) {
            // Consume the "."
            advance()

            while (peek().isDigit()) {
                advance()
            }
        }

        val value = source
            .substring(start, current)
            .toDoubleOrNull()

        if (value == null) {
            Lox.error(line, "Invalid number")
            return
        }

        addToken(TokenType.NUMBER, value)
    }

    private fun identifier() {
        while (peek().isLetterOrDigit()) {
            advance()
        }

        val text = source.substring(start, current)
        var type = Lox.keywords.get(text)
        if (type == null) {
            type = TokenType.IDENTIFIER
        }

        addToken(type)
    }

    private fun advance(): Char {
        val char = source[current]
        current += 1

        return char
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current += 1
        return true
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'

        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) {
            return '\u0000'
        }

        return source[current + 1]
    }

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }
}