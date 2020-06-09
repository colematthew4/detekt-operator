package io.cole.matthew.detekt.operator.rules

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class PreferInOverContainsSyntaxTest : DetektRuleTestBase("contains") {
    override val subject = PreferInOverContainsSyntax()

    @Test
    fun `reports if contains is used on collection`() {
        val code = buildCode("return %list:L.contains(%item:L)", mapOf(
            "list" to "listOf(1, 2)",
            "item" to 1
        )).build()

        compileAndLint(code.toString()).run {
            this shouldHaveSize 1
            with(this.first()) {
                this.issue shouldBe subject.issue
                this.messageOrDescription() shouldBe
                        "The kotlin.collections.List.contains method can be replaced with the \"in\" operator."
            }
        }
    }

    @Test
    fun `does not report if in is used on collection`() {
        val code = buildCode("return %item:L in %list:L", mapOf(
            "list" to "listOf(1, 2)",
            "item" to 1
        )).build()

        compileAndLint(code.toString()).run {
            this.shouldBeEmpty()
        }
    }

    @Test
    fun `reports if contains is used on range`() {
        val code = buildCode("return (%range:L).contains(%item:L)", mapOf(
            "range" to "1..2",
            "item" to 1
        )).build()

        compileAndLint(code.toString()).run {
            this shouldHaveSize 1
            with(this.first()) {
                this.issue shouldBe subject.issue
                this.messageOrDescription() shouldBe
                        "The kotlin.ranges.IntRange.contains method can be replaced with the \"in\" operator."
            }
        }
    }

    @Test
    fun `does not report if in is used on range`() {
        val code = buildCode("return %item:L in %range:L", mapOf(
            "range" to "1..2",
            "item" to 1
        )).build()

        compileAndLint(code.toString()).run {
            this.shouldBeEmpty()
        }
    }

    @Test
    fun `reports if contains is used on string`() {
        val code = buildCode("return %string:L.contains(%item:L)", mapOf(
            "string" to "\"Hello World\"",
            "item" to "\"ll\""
        )).build()

        compileAndLint(code.toString()).run {
            this shouldHaveSize 1
            with(this.first()) {
                this.issue shouldBe subject.issue
                this.messageOrDescription() shouldBe
                        "The kotlin.text.contains method can be replaced with the \"in\" operator."
            }
        }
    }

    @Test
    fun `does not report if in is used on string`() {
        val code = buildCode("return %item:L in %string:L", mapOf(
            "string" to "\"Hello World\"",
            "item" to "\"ll\""
        )).build()

        compileAndLint(code.toString()).run {
            this.shouldBeEmpty()
        }
    }

    @Test
    fun `does not report if contains is used on string with ignoreCase parameter`() {
        val code = buildCode("return %string:L.contains(%item:L, true)", mapOf(
            "string" to "\"Hello World\"",
            "item" to "\"ll\""
        )).build()

        compileAndLint(code.toString()).run {
            this.shouldBeEmpty()
        }
    }

    @Test
    fun `reports if contains is used on CharCategory`() {
        val code = buildCode("return %charCategory:L.contains(%item:L)", mapOf(
            "charCategory" to "CharCategory.CONTROL",
            "item" to "'c'"
        )).build()

        compileAndLint(code.toString()).run {
            this shouldHaveSize 1
            with(this.first()) {
                this.issue shouldBe subject.issue
                this.messageOrDescription() shouldBe
                        "The kotlin.text.CharCategory.contains method can be replaced with the \"in\" operator."
            }
        }
    }

    @Test
    fun `does not report if in is used on CharCategory`() {
        val code = buildCode("return %item:L in %charCategory:L", mapOf(
            "charCategory" to "CharCategory.CONTROL",
            "item" to "'c'"
        )).build()

        compileAndLint(code.toString()).run {
            this.shouldBeEmpty()
        }
    }
}
