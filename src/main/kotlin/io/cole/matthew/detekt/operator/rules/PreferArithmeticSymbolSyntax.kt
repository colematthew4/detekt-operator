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
 * This rule detects and reports instances in the code where the `plus()`, `minus()`, `times()`, `div()`, `rem()`,
 * `mod()`, or `rangeTo()` methods are used to perform arithmetic operations.
 *
 * <non-compliant>
 *  val counts = listOf(
 *      size.plus(5)
 *      size.minus(4)
 *      size.times(3)
 *      size.div(2)
 *      size.rem(6)
 *      size.mod(7)
 *  )
 *
 *  for (value in 1.rangeTo(10)) {
 *      println(value)
 *  }
 * </non-compliant>
 *
 * <compliant>
 *  val counts = listOf(
 *      size + 5
 *      size - 4
 *      size * 3
 *      size / 2
 *      size % 6
 *      size % 7
 *  )
 *
 *  for (value in 1..10) {
 *      println(value)
 *  }
 * </compliant>
 */
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
