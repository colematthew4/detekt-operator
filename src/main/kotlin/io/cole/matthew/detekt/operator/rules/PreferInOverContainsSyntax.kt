package io.cole.matthew.detekt.operator.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.util.OperatorNameConventions

class PreferInOverContainsSyntax : PreferOperatorOverNamedFunctionSyntax(
    """
        The "contains" operator is referenced by its named function translation. 
        This can be replaced by <value2> in <value1>.
    """.trimIndent()
) {
    override fun visitCallExpression(expression: KtCallExpression) {
        if (bindingContext == BindingContext.EMPTY) {
            return
        }

        (expression.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor)?.let { descriptor ->
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
