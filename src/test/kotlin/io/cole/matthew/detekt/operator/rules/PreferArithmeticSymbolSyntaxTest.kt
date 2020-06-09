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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.math.BigInteger

internal class PreferArithmeticSymbolSyntaxTest : DetektRuleTestBase("arithmetic") {
    override val subject = PreferArithmeticSymbolSyntax()

    private fun buildCode(func: String, firstNamedArg: Any, secondNamedArg: Any): FileSpec.Builder {
        return super.buildCode("return %num1:L.%func:L(%num2:L)", mapOf(
            "func" to func,
            "num1" to firstNamedArg.toString(),
            "num2" to secondNamedArg.toString()
        ))
    }

    @Test
    fun `reports if plus is used`() {
        listOf(
            buildCode("plus", 1, 1),
            buildCode("plus", "(-1)", 2),
            buildCode("plus", -1, 3),
            buildCode("plus", 1.toByte(), 4),
            buildCode("plus", 1.0, 5),
            buildCode("plus", 1F, 6),
            buildCode("plus", 1L, 7),
            buildCode("plus", 1.toShort(), 8),
            buildCode("plus", "'c'", "'9'"),
            buildCode("plus", "BigDecimal.ONE", "BigDecimal.TEN").addImports(BigDecimal::class),
            buildCode("plus", "BigInteger.ONE", "BigInteger.TWO").addImports(BigInteger::class)
        ).map(FileSpec.Builder::build).forAll { ktFileBuilder ->
            compileAndLint(ktFileBuilder.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with the \"\\+\" operator."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if minus is used`() {
        listOf(
            buildCode("minus", 1, 1),
            buildCode("minus", "(-1)", 2),
            buildCode("minus", -1, 3),
            buildCode("minus", 1.toByte(), 4),
            buildCode("minus", 1.0, 5),
            buildCode("minus", 1F, 6),
            buildCode("minus", 1L, 7),
            buildCode("minus", 1.toShort(), 8),
            buildCode("minus", "'d'", "'e'"),
            buildCode("minus", "'d'", 5),
            buildCode("minus", "BigDecimal.ONE", "BigDecimal.TEN").addImports(BigDecimal::class),
            buildCode("minus", "BigInteger.ONE", "BigInteger.TWO").addImports(BigInteger::class)
        ).map(FileSpec.Builder::build).forAll { ktFileBuilder ->
            compileAndLint(ktFileBuilder.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with the \"-\" operator."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if times is used`() {
        listOf(
            buildCode("times", 1, 1),
            buildCode("times", "(-1)", 2),
            buildCode("times", -1, 3),
            buildCode("times", 1.toByte(), 4),
            buildCode("times", 1.0, 5),
            buildCode("times", 1F, 6),
            buildCode("times", 1L, 7),
            buildCode("times", 1.toShort(), 8),
            buildCode("times", "BigDecimal.ONE", "BigDecimal.TEN").addImports(BigDecimal::class),
            buildCode("times", "BigInteger.ONE", "BigInteger.TWO").addImports(BigInteger::class)
        ).map(FileSpec.Builder::build).forAll { ktFileBuilder ->
            compileAndLint(ktFileBuilder.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with the \"\\*\" operator."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if div is used`() {
        listOf(
            buildCode("div", 1, 1),
            buildCode("div", "(-1)", 2),
            buildCode("div", -1, 3),
            buildCode("div", 1.toByte(), 4),
            buildCode("div", 1.0, 5),
            buildCode("div", 1F, 6),
            buildCode("div", 1L, 7),
            buildCode("div", 1.toShort(), 8),
            buildCode("div", "BigDecimal.ONE", "BigDecimal.TEN").addImports(BigDecimal::class),
            buildCode("div", "BigInteger.ONE", "BigInteger.TWO").addImports(BigInteger::class)
        ).map(FileSpec.Builder::build).forAll { ktFileBuilder ->
            compileAndLint(ktFileBuilder.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
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
            buildCode(func, 1, 1),
            buildCode(func, "(-1)", 2),
            buildCode(func, -1, 3),
            buildCode(func, 1.toByte(), 4),
            buildCode(func, 1.0, 5),
            buildCode(func, 1F, 6),
            buildCode(func, 1L, 7),
            buildCode(func, 1.toShort(), 8),
            buildCode(func, "BigDecimal.ONE", "BigDecimal.TEN").addImports(BigDecimal::class),
            buildCode(func, "BigInteger.ONE", "BigInteger.TWO").addImports(BigInteger::class)
        ).map(FileSpec.Builder::build).forAll { ktFileBuilder ->
            compileAndLint(ktFileBuilder.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
                        "The [a-zA-Z.]+ method can be replaced with the \"%\" operator."
                    )
                }
            }
        }
    }

    @Test
    fun `reports if rangeTo is used`() {
        listOf(
            buildCode("rangeTo", 1, 1),
            buildCode("rangeTo", "(-1)", 2),
            buildCode("rangeTo", -1, 3),
            buildCode("rangeTo", 1.toByte(), 4),
            buildCode("rangeTo", 1.0, 5),
            buildCode("rangeTo", 1F, 6),
            buildCode("rangeTo", 1L, 7),
            buildCode("rangeTo", 1.toShort(), 8),
            buildCode("rangeTo", "'h'", "'i'"),
            buildCode("rangeTo", "BigDecimal.ONE", "BigDecimal.TEN").addImports(BigDecimal::class),
            buildCode("rangeTo", "BigInteger.ONE", "BigInteger.TWO").addImports(BigInteger::class)
        ).map(FileSpec.Builder::build).forAll { ktFileBuilder ->
            compileAndLint(ktFileBuilder.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
                        "The kotlin[a-zA-Z.]+ method can be replaced with the \"\\.\\.\" operator."
                    )
                }
            }
        }
    }

    @Test
    fun `does not report if named arithmetic functions are not used`() {
        compileAndLint(buildCode("coerceIn", 1, 2).build().toString()).run {
            this.shouldBeEmpty()
        }
    }
}
