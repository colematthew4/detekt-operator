package io.cole.matthew.detekt.operator.rules

import com.squareup.kotlinpoet.FileSpec
import io.gitlab.arturbosch.detekt.api.Finding
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test

internal class PreferUnaryPostfixOverFunctionSyntaxTest : DetektRuleTestBase("unaryPostfix") {
    override val subject = PreferUnaryPostfixOverFunctionSyntax()

    private fun buildCode(func: String, namedArg: String): FileSpec {
        return super.buildCode("return %num:L.%func:L()", "func" to func, "num" to namedArg)
    }

    private fun buildCode(func: String, namedArg: Char): FileSpec {
        return super.buildCode("return %num:L.%func:L()", "func" to func, "num" to namedArg)
    }

    @Test
    fun `reports if inc is used on a number`() {
        listOf(
            buildCode("inc", 1.toString()),
            buildCode("inc", "(-1)"),
            buildCode("inc", (-1).toString()),
            buildCode("inc", 1.toByte().toString()),
            buildCode("inc", 1.toDouble().toString()),
            buildCode("inc", 1.toFloat().toString()),
            buildCode("inc", 1L.toString()),
            buildCode("inc", 1.toShort().toString()),
            buildCode("inc", 'c'),
            buildCode("inc", "BigDecimal.ONE"),
            buildCode("inc", "BigInteger.ONE")
        ).flatMap {
            compileAndLint(it.toString())
        }.run {
            this.shouldNotBeEmpty()
            with(this.distinctBy(Finding::issue)) {
                this shouldHaveSize 1
                this.first().issue shouldBe subject.issue
                this.forEach { it.messageOrDescription() shouldMatch Regex(
                    "The kotlin[a-zA-Z.]+ method can be replaced with its unary postfix operator equivalent\\."
                ) }
            }
        }
    }

    @Test
    fun `reports if dec is used on a number`() {
        listOf(
            buildCode("dec", 1.toString()),
            buildCode("dec", "(-1)"),
            buildCode("dec", (-1).toString()),
            buildCode("dec", 1.toByte().toString()),
            buildCode("dec", 1.toDouble().toString()),
            buildCode("dec", 1.toFloat().toString()),
            buildCode("dec", 1L.toString()),
            buildCode("dec", 1.toShort().toString()),
            buildCode("dec", 'd'),
            buildCode("dec", "BigDecimal.ONE"),
            buildCode("dec", "BigInteger.ONE")
        ).flatMap {
            compileAndLint(it.toString())
        }.run {
            this.shouldNotBeEmpty()
            with(this.distinctBy(Finding::issue)) {
                this shouldHaveSize 1
                this.first().issue shouldBe subject.issue
                this.forEach {
                    it.messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with its unary postfix operator equivalent\\."
                    )
                }
            }
        }
    }

    @Test
    fun `does not report if unary functions are not used`() {
        compileAndLint(buildCode("toString", "1").toString()).run {
            this.shouldBeEmpty()
        }
    }
}
