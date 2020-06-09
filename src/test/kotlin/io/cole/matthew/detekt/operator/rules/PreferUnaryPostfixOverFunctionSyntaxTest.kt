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

internal class PreferUnaryPostfixOverFunctionSyntaxTest : DetektRuleTestBase("unaryPostfix") {
    override val subject = PreferUnaryPostfixOverFunctionSyntax()

    private fun buildCode(func: String, namedArg: Any): FileSpec.Builder {
        return super.buildCode("return %num:L.%func:L()", mapOf("func" to func, "num" to namedArg.toString()))
    }

    @Test
    fun `reports if inc is used on a number`() {
        listOf(
            buildCode("inc", 1),
            buildCode("inc", "(-1)"),
            buildCode("inc", -1),
            buildCode("inc", 1.toByte()),
            buildCode("inc", 1.0),
            buildCode("inc", 1F),
            buildCode("inc", 1L),
            buildCode("inc", 1.toShort()),
            buildCode("inc", "'c'"),
            buildCode("inc", "BigDecimal.ONE").addImports(BigDecimal::class),
            buildCode("inc", "BigInteger.ONE").addImports(BigInteger::class)
        ).map(FileSpec.Builder::build).forAll { ktFileBuilder ->
            compileAndLint(ktFileBuilder.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with its unary postfix operator equivalent\\."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if dec is used on a number`() {
        listOf(
            buildCode("dec", 1),
            buildCode("dec", "(-1)"),
            buildCode("dec", -1),
            buildCode("dec", 1.toByte()),
            buildCode("dec", 1.0),
            buildCode("dec", 1F),
            buildCode("dec", 1L),
            buildCode("dec", 1.toShort()),
            buildCode("dec", "'d'"),
            buildCode("inc", "BigDecimal.ONE").addImports(BigDecimal::class),
            buildCode("inc", "BigInteger.ONE").addImports(BigInteger::class)
        ).map(FileSpec.Builder::build).forAll { ktFileBuilder ->
            compileAndLint(ktFileBuilder.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with its unary postfix operator equivalent\\."
                    )
                }
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
