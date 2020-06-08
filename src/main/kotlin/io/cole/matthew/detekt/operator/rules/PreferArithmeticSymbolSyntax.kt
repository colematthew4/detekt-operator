package io.cole.matthew.detekt.operator.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.util.OperatorNameConventions
import org.jetbrains.kotlin.util.isValidOperator

class PreferArithmeticSymbolSyntax : PreferOperatorOverNamedFunctionSyntaxBase(
    """
        An arithmetic operator is referenced by its named function translation. 
        This can be replaced by <value2> [+-*/%(..)] <value1>.
    """.trimIndent()
) {
    private val _operatorSymbols = mapOf(
        OperatorNameConventions.PLUS to "+",
        OperatorNameConventions.MINUS to "-",
        OperatorNameConventions.TIMES to "*",
        OperatorNameConventions.DIV to "/",
        OperatorNameConventions.REM to "%",
        OperatorNameConventions.MOD to "%",
        OperatorNameConventions.RANGE_TO to ".."
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        (expression.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor)?.let { descriptor ->
            if (descriptor.name !in OperatorNameConventions.BINARY_OPERATION_NAMES) {
                return
            }

            if (descriptor.isValidOperator()) {
                report(CodeSmell(issue, Entity.from(expression), "The ${descriptor.fqNameOrNull()} method can " +
                        "be replaced with the \"${_operatorSymbols[descriptor.name]}\" operator."))
            }
        }

        super.visitCallExpression(expression)
    }
}
