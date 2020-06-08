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
 * The Kotlin unary operations `unaryPlus`, `unaryMinus`, and `not` can be replaced with their traditional prefix
 * operators - `+`, `-`, and `!`. This rule detects and reports the usage of `unaryPlus()`, `unaryMinus()`, and
 * `not()` in the code.
 *
 * <non-compliant>
 *  val index = 1
 *  val negIndex = index.unaryMinus()
 *  assert((index < negIndex).not())
 * </non-compliant>
 *
 * <compliant>
 *  val index = 1
 *  val negIndex = -index
 *  assert(!(index < negIndex))
 * </compliant>
 */
class PreferUnaryPrefixOverFunctionSyntax : PreferOperatorOverNamedFunctionSyntaxBase(
    """
        The unary prefix operators are referenced by their named function translation. 
        This can be replaced by [+-!]<value1>.
    """.trimIndent()
) {
    override fun visitCallExpression(expression: KtCallExpression) {
        (expression.getResolvedCall(bindingContext)?.candidateDescriptor as? FunctionDescriptor)?.let { descriptor ->
            if (descriptor.name != OperatorNameConventions.UNARY_PLUS &&
                descriptor.name != OperatorNameConventions.UNARY_MINUS &&
                descriptor.name != OperatorNameConventions.NOT) {
                return
            }

            if (descriptor.isValidOperator()) {
                report(CodeSmell(issue, Entity.from(expression), """
                    The ${descriptor.fqNameOrNull()} method can be replaced with its unary prefix operator equivalent.
                """.trimIndent()))
            }
        }

        super.visitCallExpression(expression)
    }
}
