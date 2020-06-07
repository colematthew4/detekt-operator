package io.cole.matthew.detekt.operator.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.util.OperatorNameConventions
import org.jetbrains.kotlin.util.isValidOperator

class PreferBracketAccessorOverFunctionSyntax : PreferOperatorOverNamedFunctionSyntaxBase(
    """
        An "index accessor" operator is referenced by its named function translation. 
        This can be replaced by <value1>[<value2>].
    """.trimIndent()
) {
    override fun visitCallExpression(expression: KtCallExpression) {
        (expression.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor)?.let { descriptor ->
            if (descriptor.name != OperatorNameConventions.GET && descriptor.name != OperatorNameConventions.SET) {
                return
            }

            if (descriptor.isValidOperator()) {
                report(CodeSmell(issue, Entity.from(expression), """
                    The ${descriptor.fqNameOrNull()} method can be replaced with the "[]" operator.
                """.trimIndent()))
            }
        }

        super.visitCallExpression(expression)
    }
}
