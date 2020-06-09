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
 * This rule detects and reports instances in the code where the `invoke()` method is used to invoke functions stored as
 * variables.
 *
 * <non-compliant>
 *  val func = ::error
 *  func.invoke("error")
 * </non-compliant>
 *
 * <compliant>
 *  val func = ::error
 *  func("error")
 * </compliant>
 */
class PreferParensOverInvokeSyntax : PreferOperatorOverNamedFunctionSyntaxBase(
    """
        The function "invoke" operator is referenced by its named function translation. 
        This can be replaced by <func>([...args]).
    """.trimIndent()
) {
    override fun visitCallExpression(expression: KtCallExpression) {
        (expression.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor)?.let { descriptor ->
            if (descriptor.name != OperatorNameConventions.INVOKE) {
                return
            }

            if (descriptor.isValidOperator()) {
                report(CodeSmell(issue, Entity.from(expression), """
                    The ${descriptor.fqNameOrNull()} method can be called with just "()".
                """.trimIndent()))
            }
        }

        super.visitCallExpression(expression)
    }
}
