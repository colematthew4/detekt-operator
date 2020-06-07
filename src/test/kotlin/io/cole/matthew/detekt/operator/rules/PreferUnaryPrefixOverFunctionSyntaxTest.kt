package io.cole.matthew.detekt.operator.rules

import com.squareup.kotlinpoet.FileSpec
import io.gitlab.arturbosch.detekt.api.Finding
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test

internal class PreferUnaryPrefixOverFunctionSyntaxTest : DetektRuleTestBase("unaryPrefix") {
    override val subject = PreferUnaryPrefixOverFunctionSyntax()

    private fun buildCode(func: String, namedArg: String): FileSpec {
        return super.buildCode("return %num:L.%func:L()", "func" to func, "num" to namedArg)
    }

    @Test
    fun `reports if unaryPlus is used on a number`() {
        listOf(
            buildCode("unaryPlus", 1.toString()),
            buildCode("unaryPlus", "(-1)"),
            buildCode("unaryPlus", (-1).toString()),
            buildCode("unaryPlus", 1.toByte().toString()),
            buildCode("unaryPlus", 1.toDouble().toString()),
            buildCode("unaryPlus", 1.toFloat().toString()),
            buildCode("unaryPlus", 1L.toString()),
            buildCode("unaryPlus", 1.toShort().toString())
        ).flatMap {
            compileAndLint(it.toString())
        }.run {
            this.shouldNotBeEmpty()
            with(this.distinctBy(Finding::issue)) {
                this shouldHaveSize 1
                this.first().issue shouldBe subject.issue
                this.forEach { it.messageOrDescription() shouldMatch Regex(
                    "The kotlin[a-zA-Z.]+ method can be replaced with its unary prefix operator equivalent\\."
                ) }
            }
        }
    }

    @Test
    fun `reports if unaryMinus is used on a number`() {
        listOf(
            buildCode("unaryMinus", 1.toString()),
            buildCode("unaryMinus", "(-1)"),
            buildCode("unaryMinus", (-1).toString()),
            buildCode("unaryMinus", 1.toByte().toString()),
            buildCode("unaryMinus", 1.toDouble().toString()),
            buildCode("unaryMinus", 1.toFloat().toString()),
            buildCode("unaryMinus", 1L.toString()),
            buildCode("unaryMinus", 1.toShort().toString()),
            buildCode("unaryMinus", "BigDecimal.ONE"),
            buildCode("unaryMinus", "BigInteger.ONE")
        ).flatMap {
            compileAndLint(it.toString())
        }.run {
            this.shouldNotBeEmpty()
            with(this.distinctBy(Finding::issue)) {
                this shouldHaveSize 1
                this.first().issue shouldBe subject.issue
                this.forEach {
                    it.messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with its unary prefix operator equivalent\\."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if not is used on a boolean`() {
        compileAndLint(buildCode("not", false.toString()).toString()).run {
            this shouldHaveSize 1
            with(this.first()) {
                this.issue shouldBe subject.issue
                this.messageOrDescription() shouldBe
                        "The kotlin.Boolean.not method can be replaced with its unary prefix operator equivalent."
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
