package io.cole.matthew.detekt.operator.rules

import com.squareup.kotlinpoet.FileSpec
import io.gitlab.arturbosch.detekt.api.Finding
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

internal class PreferUnaryPrefixOverFunctionSyntaxTest : DetektRuleTestBase("unaryPrefix") {
    override val subject = PreferUnaryPrefixOverFunctionSyntax()

    private fun buildCode(func: String, namedArg: Any): FileSpec.Builder {
        return super.buildCode("return %num:L.%func:L()", mapOf("func" to func, "num" to namedArg.toString()))
    }

    @Test
    fun `reports if unaryPlus is used on a number`() {
        listOf(
            buildCode("unaryPlus", 1),
            buildCode("unaryPlus", "(-1)"),
            buildCode("unaryPlus", -1),
            buildCode("unaryPlus", 1.toByte()),
            buildCode("unaryPlus", 1.0),
            buildCode("unaryPlus", 1F),
            buildCode("unaryPlus", 1L),
            buildCode("unaryPlus", 1.toShort())
        ).map(FileSpec.Builder::build).forAll { ktFileBuilder ->
            compileAndLint(ktFileBuilder.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with its unary prefix operator equivalent\\."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if unaryMinus is used on a number`() {
        listOf(
            buildCode("unaryMinus", 1),
            buildCode("unaryMinus", "(-1)"),
            buildCode("unaryMinus", (-1)),
            buildCode("unaryMinus", 1.toByte()),
            buildCode("unaryMinus", 1.0),
            buildCode("unaryMinus", 1F),
            buildCode("unaryMinus", 1L),
            buildCode("unaryMinus", 1.toShort()),
            buildCode("unaryMinus", "BigDecimal.ONE").addImports(BigDecimal::class),
            buildCode("unaryMinus", "BigInteger.ONE").addImports(BigInteger::class)
        ).map(FileSpec.Builder::build).forAll { ktFileBuilder ->
            compileAndLint(ktFileBuilder.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with its unary prefix operator equivalent\\."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if not is used on a boolean`() {
        compileAndLint(buildCode("not", false).build().toString()).run {
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
        compileAndLint(buildCode("toString", 1).build().toString()).run {
            this.shouldBeEmpty()
        }
    }
}
