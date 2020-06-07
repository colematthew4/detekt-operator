package io.cole.matthew.detekt.operator.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.util.OperatorNameConventions

class PreferInOverContainsSyntax : PreferOperatorOverNamedFunctionSyntaxBase(
    """
        The "contains" operator is referenced by its named function translation. 
        This can be replaced by <value2> in <value1>.
    """.trimIndent()
) {
    override fun visitCallExpression(expression: KtCallExpression) {
        (expression.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor)?.let { descriptor ->
            /*
             * String.contains will fail the descriptor.IsValidOperator check because it has 2 parameters (ignoreCase
             * has a default value of false). Therefore checking if the number of parameters passed to String.contains
             * will tell us if the function is eligible to be replaced by its operator expression.
             */
            if (descriptor.name != OperatorNameConventions.CONTAINS || expression.lastChild.children.size > 1) {
                return
            }

            if (descriptor.isOperator) {
                report(CodeSmell(issue, Entity.from(expression), """
                    The ${descriptor.fqNameOrNull()} method can be replaced with the "in" operator.
                """.trimIndent()))
            }
        }

        super.visitCallExpression(expression)
    }
}
