package io.cole.matthew.detekt.operator.rules

import com.squareup.kotlinpoet.FileSpec
import io.gitlab.arturbosch.detekt.api.Finding
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class PreferArithmeticSymbolSyntaxTest : DetektRuleTestBase("arithmetic") {
    override val subject = PreferArithmeticSymbolSyntax()

    private fun buildCode(func: String, firstNamedArg: String, secondNamedArg: String): FileSpec {
        return super.buildCode("return %num1:L.%func:L(%num2:L)",
            "func" to func,
            "num1" to firstNamedArg,
            "num2" to secondNamedArg
        )
    }

    private fun buildCode(func: String, firstNamedArg: Char, secondNamedArg: Char): FileSpec {
        return super.buildCode("return %num1:L.%func:L(%num2:L)",
            "func" to func,
            "num1" to firstNamedArg,
            "num2" to secondNamedArg
        )
    }

    @Test
    fun `reports if plus is used`() {
        listOf(
            buildCode("plus", 1.toString(), 1.toString()),
            buildCode("plus", "(-1)", 2.toString()),
            buildCode("plus", (-1).toString(), 3.toString()),
            buildCode("plus", 1.toByte().toString(), 4.toString()),
            buildCode("plus", 1.toDouble().toString(), 5.toString()),
            buildCode("plus", 1.toFloat().toString(), 6.toString()),
            buildCode("plus", 1L.toString(), 7.toString()),
            buildCode("plus", 1.toShort().toString(), 8.toString()),
            buildCode("plus", 'c', 9.toChar()),
            buildCode("plus", "BigDecimal.ONE", "BigDecimal.TEN"),
            buildCode("plus", "BigInteger.ONE", "BigInteger.TWO")
        ).flatMap {
            compileAndLint(it.toString())
        }.run {
            this.shouldNotBeEmpty()
            with(this.distinctBy(Finding::issue)) {
                this shouldHaveSize 1
                this.first().issue shouldBe subject.issue
                this.forEach {
                    it.messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with the \"\\+\" operator."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if minus is used`() {
        listOf(
            buildCode("minus", 1.toString(), 1.toString()),
            buildCode("minus", "(-1)", 2.toString()),
            buildCode("minus", (-1).toString(), 3.toString()),
            buildCode("minus", 1.toByte().toString(), 4.toString()),
            buildCode("minus", 1.toDouble().toString(), 5.toString()),
            buildCode("minus", 1.toFloat().toString(), 6.toString()),
            buildCode("minus", 1L.toString(), 7.toString()),
            buildCode("minus", 1.toShort().toString(), 8.toString()),
            buildCode("minus", 'd', 'e'),
            buildCode("minus", "BigDecimal.ONE", "BigDecimal.TEN"),
            buildCode("minus", "BigInteger.ONE", "BigInteger.TWO")
        ).flatMap {
            compileAndLint(it.toString())
        }.run {
            this.shouldNotBeEmpty()
            with(this.distinctBy(Finding::issue)) {
                this shouldHaveSize 1
                this.first().issue shouldBe subject.issue
                this.forEach {
                    it.messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with the \"-\" operator."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if times is used`() {
        listOf(
            buildCode("times", 1.toString(), 1.toString()),
            buildCode("times", "(-1)", 2.toString()),
            buildCode("times", (-1).toString(), 3.toString()),
            buildCode("times", 1.toByte().toString(), 4.toString()),
            buildCode("times", 1.toDouble().toString(), 5.toString()),
            buildCode("times", 1.toFloat().toString(), 6.toString()),
            buildCode("times", 1L.toString(), 7.toString()),
            buildCode("times", 1.toShort().toString(), 8.toString()),
            buildCode("times", 'f', 'g'),
            buildCode("times", "BigDecimal.ONE", "BigDecimal.TEN"),
            buildCode("times", "BigInteger.ONE", "BigInteger.TWO")
        ).flatMap {
            compileAndLint(it.toString())
        }.run {
            this.shouldNotBeEmpty()
            with(this.distinctBy(Finding::issue)) {
                this shouldHaveSize 1
                this.first().issue shouldBe subject.issue
                this.forEach {
                    it.messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with the \"\\*\" operator."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if div is used`() {
        listOf(
            buildCode("div", 1.toString(), 1.toString()),
            buildCode("div", "(-1)", 2.toString()),
            buildCode("div", (-1).toString(), 3.toString()),
            buildCode("div", 1.toByte().toString(), 4.toString()),
            buildCode("div", 1.toDouble().toString(), 5.toString()),
            buildCode("div", 1.toFloat().toString(), 6.toString()),
            buildCode("div", 1L.toString(), 7.toString()),
            buildCode("div", 1.toShort().toString(), 8.toString()),
            buildCode("div", 'h', 'i'),
            buildCode("div", "BigDecimal.ONE", "BigDecimal.TEN"),
            buildCode("div", "BigInteger.ONE", "BigInteger.TWO")
        ).flatMap {
            compileAndLint(it.toString())
        }.run {
            this.shouldNotBeEmpty()
            with(this.distinctBy(Finding::issue)) {
                this shouldHaveSize 1
                this.first().issue shouldBe subject.issue
                this.forEach {
                    it.messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with the \"/\" operator."
                    )
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["rem", "mod"])
    fun `reports if remainder is used`(func: String) {
        listOf(
            buildCode(func, 1.toString(), 1.toString()),
            buildCode(func, "(-1)", 2.toString()),
            buildCode(func, (-1).toString(), 3.toString()),
            buildCode(func, 1.toByte().toString(), 4.toString()),
            buildCode(func, 1.toDouble().toString(), 5.toString()),
            buildCode(func, 1.toFloat().toString(), 6.toString()),
            buildCode(func, 1L.toString(), 7.toString()),
            buildCode(func, 1.toShort().toString(), 8.toString()),
            buildCode(func, 'j', 'k'),
            buildCode(func, "BigDecimal.ONE", "BigDecimal.TEN"),
            buildCode(func, "BigInteger.ONE", "BigInteger.TWO")
        ).flatMap {
            compileAndLint(it.toString())
        }.run {
            this.shouldNotBeEmpty()
            with(this.distinctBy(Finding::issue)) {
                this shouldHaveSize 1
                this.first().issue shouldBe subject.issue
                this.forEach {
                    it.messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with the \"%\" operator."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if rangeTo is used`() {
        listOf(
            buildCode("rangeTo", 1.toString(), 1.toString()),
            buildCode("rangeTo", "(-1)", 2.toString()),
            buildCode("rangeTo", (-1).toString(), 3.toString()),
            buildCode("rangeTo", 1.toByte().toString(), 4.toString()),
            buildCode("rangeTo", 1.toDouble().toString(), 5.toString()),
            buildCode("rangeTo", 1.toFloat().toString(), 6.toString()),
            buildCode("rangeTo", 1L.toString(), 7.toString()),
            buildCode("rangeTo", 1.toShort().toString(), 8.toString()),
            buildCode("rangeTo", 'h', 'i'),
            buildCode("rangeTo", "BigDecimal.ONE", "BigDecimal.TEN"),
            buildCode("rangeTo", "BigInteger.ONE", "BigInteger.TWO")
        ).flatMap {
            compileAndLint(it.toString())
        }.run {
            this.shouldNotBeEmpty()
            with(this.distinctBy(Finding::issue)) {
                this shouldHaveSize 1
                this.first().issue shouldBe subject.issue
                this.forEach {
                    it.messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with the \"\\.\\.\" operator."
                    )
                }
            }
        }
    }

    @Test
    fun `does not report if named arithmetic functions are not used`() {
        compileAndLint(buildCode("coerceIn", "1", "2").toString()).run {
            this.shouldBeEmpty()
        }
    }
}
