package io.cole.matthew.detekt.operator.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.util.OperatorNameConventions
import org.jetbrains.kotlin.util.isValidOperator

/**
 * The Kotlin functions `inc` and `dec` can be replaced with the traditional postfix increment and decrement operators -
 * `++` and `--`. This rule detects and reports the usage of `inc()` and `dec()` in the code.
 *
 * <non-compliant>
 *  while (index.inc() < size.dec()) {
 *      println(index, size)
 *  }
 * </non-compliant>
 *
 * <compliant>
 *  while (index++ < size--) {
 *      println(index, size)
 *  }
 * </compliant>
 */
class PreferUnaryPostfixOverFunctionSyntax : PreferOperatorOverNamedFunctionSyntaxBase(
    """
        The unary postfix operators are referenced by their named function translation. 
        This can be replaced by <value1>[(++)(--)].
    """.trimIndent()
) {
    override fun visitCallExpression(expression: KtCallExpression) {
        (expression.getResolvedCall(bindingContext)?.candidateDescriptor as? FunctionDescriptor)?.let { descriptor ->
            if (descriptor.name != OperatorNameConventions.INC && descriptor.name != OperatorNameConventions.DEC) {
                return
            }

            if (descriptor.isValidOperator()) {
                report(CodeSmell(issue, Entity.from(expression), """
                    The ${descriptor.fqNameOrNull()} method can be replaced with its unary postfix operator equivalent.
                """.trimIndent()))
            }
        }

        super.visitCallExpression(expression)
    }
}
