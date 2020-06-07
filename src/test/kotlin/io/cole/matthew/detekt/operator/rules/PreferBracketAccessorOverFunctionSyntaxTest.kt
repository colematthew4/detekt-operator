package io.cole.matthew.detekt.operator.rules

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PreferBracketAccessorOverFunctionSyntaxTest : DetektRuleTestBase("indexAccessor") {
    override val subject = PreferBracketAccessorOverFunctionSyntax()

    @Nested
    inner class GetOperator {
        @Test
        fun `reports if get is used on array or collection`() {
            listOf(
                "arrayOf",
                "booleanArrayOf",
                "byteArrayOf",
                "charArrayOf",
                "doubleArrayOf",
                "floatArrayOf",
                "intArrayOf",
                "longArrayOf",
                "shortArrayOf",
                "listOf"
            ).map { func ->
                buildCode("return %list:L.get(%index:L)", "list" to "$func(1, 2)", "index" to 1)
            }.forEach { code ->
                compileAndLint(code.toString()).run {
                    this shouldHaveSize 1
                    with(this.first()) {
                        this.issue shouldBe subject.issue
                        this.messageOrDescription() shouldMatch Regex(
                            "The kotlin[a-zA-Z.]+ method can be replaced with the \"\\[]\" operator\\."
                        )
                    }
                }
            }
        }

        @Test
        fun `reports if get is used on map`() {
            val code = buildCode(
                "return %list:L.get(%index:L)",
                "list" to "mapOf(1 to 'a', 2 to 'b')",
                "index" to 1
            )

            compileAndLint(code.toString()).run {
                this shouldHaveSize 1
                with(this.first()) {
                    this.issue shouldBe subject.issue
                    this.messageOrDescription() shouldBe
                            "The kotlin.collections.Map.get method can be replaced with the \"[]\" operator."
                }
            }
        }

        @Test
        fun `reports if get is used on string`() {
            val code = buildCode(
                "return %string:L.get(%index:L)",
                "string" to "\"Hello World\"",
                "index" to 1
            )

            compileAndLint(code.toString()).run {
                this shouldHaveSize 1
                with(this.first()) {
                    this.issue shouldBe subject.issue
                    this.messageOrDescription() shouldBe
                            "The kotlin.String.get method can be replaced with the \"[]\" operator."
                }
            }
        }
    }

    @Nested
    inner class SetOperator {
        @Test
        fun `reports if set is used on array or collection`() {
            listOf(
                "arrayOf",
                "booleanArrayOf",
                "byteArrayOf",
                "charArrayOf",
                "doubleArrayOf",
                "floatArrayOf",
                "intArrayOf",
                "longArrayOf",
                "shortArrayOf",
                "mutableListOf"
            ).map { func ->
                buildCode("return %list:L.set(%index:L, %item:L)",
                    "list" to "$func(1, 2)",
                    "index" to 0,
                    "item" to 1
                )
            }.forEach { code ->
                compileAndLint(code.toString()).run {
                    this shouldHaveSize 1
                    with(this.first()) {
                        this.issue shouldBe subject.issue
                        this.messageOrDescription() shouldMatch Regex(
                            "The kotlin[a-zA-Z.]+ method can be replaced with the \"\\[]\" operator\\."
                        )
                    }
                }
            }
        }

        @Test
        fun `reports if set is used on map`() {
            val code = buildCode(
                "return %map:L.set(%key:L, %value:L)",
                "map" to "mutableMapOf(1 to 'a', 2 to 'b')",
                "key" to 3,
                "value" to 'c'
            )

            compileAndLint(code.toString()).run {
                this shouldHaveSize 1
                with(this.first()) {
                    this.issue shouldBe subject.issue
                    this.messageOrDescription() shouldBe
                            "The kotlin.collections.set method can be replaced with the \"[]\" operator."
                }
            }
        }

        @Test
        fun `reports if set is used on string builder`() {
            val code = buildCode(
                "return %string:L.set(%index:L, %item:L)",
                "string" to "StringBuilder()",
                "index" to 0,
                "item" to "\"ll\""
            )

            compileAndLint(code.toString()).run {
                this shouldHaveSize 1
                with(this.first()) {
                    this.issue shouldBe subject.issue
                    this.messageOrDescription() shouldBe
                            "The kotlin.text.set method can be replaced with the \"[]\" operator."
                }
            }
        }
    }

    @Test
    fun `does not report if named index accessor functions are not used`() {
        val code = buildCode(
            "return %string:L.drop(%index:L)",
            "string" to "\"Hello World\"",
            "index" to 1
        )

        compileAndLint(code.toString()).run {
            this.shouldBeEmpty()
        }
    }
}
