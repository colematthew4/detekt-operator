package io.cole.matthew.detekt.operator.rules

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DetektRuleTestBase(private val operatorToTest: String) {
    private val envWrapper = KtTestCompiler.createEnvironment()
    protected abstract val subject: PreferOperatorOverNamedFunctionSyntaxBase

    @AfterAll
    fun setup() {
        envWrapper.dispose()
    }

    fun compileAndLint(code: String) = subject.compileAndLintWithContext(envWrapper.env, code)

    fun buildCode(namedStatement: String, vararg namedArgPairs: Pair<String, *>): FileSpec {
        return FileSpec.builder("", "${operatorToTest.capitalize()}Test")
            .addType(TypeSpec.classBuilder("${operatorToTest.capitalize()}Test")
                .addFunction(FunSpec.builder("test")
                    .addCode(CodeBlock.builder().addNamed(namedStatement, mapOf(*namedArgPairs)).build())
                    .build())
                .build())
            .build()
    }
}
