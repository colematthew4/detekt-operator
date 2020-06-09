package io.cole.matthew.detekt.operator.rules

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import io.gitlab.arturbosch.detekt.api.Finding
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.azstring
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class PreferParensOverInvokeSyntaxTest : DetektRuleTestBase("invoke") {
    override val subject = PreferParensOverInvokeSyntax()

    @Test
    fun `reports if invoke is used on function variable`() {
        with(FunSpec.builder("funcToInvoke").build()) {
            mapOf(this to emptyList<String>()) + (1..22).map { argCount ->
                List(argCount) { "arg$it" }
            }.map { args ->
                FunSpec.builder("funcToInvoke")
                    .addParameters(args.map { ParameterSpec.builder(it, String::class).build() })
                    .build() to args
            }.toMap()
        }.map { (func, args) ->
            val statement = args.foldIndexed("::funcToInvoke.invoke(") { index, stmt, arg ->
                if (index == 0) "$stmt%$arg:L" else "$stmt, %$arg:L"
            } + ")"

            buildCode(statement, args.map { it to "\"${Random.azstring(5)}\"" }.toMap())
                .addFunction(func)
                .build()
        }.forAll { ktFile ->
            compileAndLint(ktFile.toString()).also {
                it.shouldNotBeEmpty()
                with(it.distinctBy(Finding::issue)) {
                    this shouldHaveSize 1
                    this.first().issue shouldBe subject.issue
                    this.first().messageOrDescription() shouldMatch Regex(
                        "The kotlin.reflect.KFunction[0-9]{1,2}.invoke method can be called with just \"\\(\\)\"."
                    )
                }
            }
        }
    }

    @Test
    fun `does not report if invoke is not used`() {
        compileAndLint(buildCode("toString", emptyMap<String, Any>()).build().toString()).run {
            this.shouldBeEmpty()
        }
    }
}
