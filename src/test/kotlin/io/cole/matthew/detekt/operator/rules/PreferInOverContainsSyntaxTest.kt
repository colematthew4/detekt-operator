package io.cole.matthew.detekt.operator.rules

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test

internal class PreferInOverContainsSyntaxTest : DetektRuleTestBase() {
    override val subject = PreferInOverContainsSyntax()

    private fun buildCode(namedStatement: String, vararg namedArgPairs: Pair<String, *>): FileSpec {
        return FileSpec.builder("", "CollectionContainsTest")
            .addType(TypeSpec.classBuilder("CollectionContainsTest")
                .addFunction(FunSpec.builder("test")
                    .addCode(CodeBlock.builder().addNamed(namedStatement, mapOf(*namedArgPairs)).build())
                    .build())
                .build())
            .build()
    }

    @Test
    fun `reports if contains is used on collection`() {
        val code = buildCode(
            "return %list:L.contains(%item:L)",
            "list" to "listOf(1, 2)",
            "item" to 1
        )

        compileAndLint(code.toString()).apply {
            this shouldHaveSize 1
        }
    }

    @Test
    fun `does not report if in is used on collection`() {
        val code = buildCode(
            "return %item:L in %list:L",
            "list" to "listOf(1, 2)",
            "item" to 1
        )

        compileAndLint(code.toString()).apply {
            this.shouldBeEmpty()
        }
    }

    @Test
    fun `reports if contains is used on range`() {
        val code = buildCode(
            "return (%range:L).contains(%item:L)",
            "range" to "1..2",
            "item" to 1
        )

        compileAndLint(code.toString()).apply {
            this shouldHaveSize 1
        }
    }

    @Test
    fun `does not report if in is used on range`() {
        val code = buildCode(
            "return %item:L in %range:L",
            "range" to "1..2",
            "item" to 1
        )

        compileAndLint(code.toString()).apply {
            this.shouldBeEmpty()
        }
    }

    @Test
    fun `reports if contains is used on string`() {
        val code = buildCode(
            "return %string:L.contains(%item:L)",
            "string" to "\"Hello World\"",
            "item" to "\"ll\""
        )

        compileAndLint(code.toString()).apply {
            this shouldHaveSize 1
        }
    }

    @Test
    fun `does not report if in is used on string`() {
        val code = buildCode(
            "return %item:L in %string:L",
            "string" to "\"Hello World\"",
            "item" to "\"ll\""
        )

        compileAndLint(code.toString()).apply {
            this.shouldBeEmpty()
        }
    }

    @Test
    fun `does not report if contains is used on string with ignoreCase parameter`() {
        val code = buildCode(
            "return %string:L.contains(%item:L, true)",
            "string" to "\"Hello World\"",
            "item" to "\"ll\""
        )

        compileAndLint(code.toString()).apply {
            this.shouldBeEmpty()
        }
    }

    @Test
    fun `reports if contains is used on CharCategory`() {
        val code = buildCode(
            "return %charCategory:L.contains(%item:L)",
            "charCategory" to "CharCategory.CONTROL",
            "item" to "'c'"
        )

        compileAndLint(code.toString()).apply {
            this shouldHaveSize 1
        }
    }

    @Test
    fun `does not report if in is used on CharCategory`() {
        val code = buildCode(
            "return %item:L in %charCategory:L",
            "charCategory" to "CharCategory.CONTROL",
            "item" to "'c'"
        )

        compileAndLint(code.toString()).apply {
            this.shouldBeEmpty()
        }
    }
}
