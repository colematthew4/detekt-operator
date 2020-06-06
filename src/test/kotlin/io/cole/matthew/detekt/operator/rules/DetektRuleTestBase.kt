package io.cole.matthew.detekt.operator.rules

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DetektRuleTestBase {
    private val envWrapper = KtTestCompiler.createEnvironment()
    protected abstract val subject: PreferOperatorOverNamedFunctionSyntax

    @AfterAll
    fun setup() {
        envWrapper.dispose()
    }

    fun compileAndLint(code: String) = subject.compileAndLintWithContext(envWrapper.env, code)
}
