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
 * Similar to the ExplicitCollectionElementAccessMethod rule in detekt's base ruleset, but extended so all
 * implementations of `get` and `set` can be replaced with the shorter operator - `[]`.
 * See [https://kotlinlang.org/docs/reference/operator-overloading.html#indexed].
 *
 * Prefer the usage of the indexed access operator `[]` for all implementations of access or insert methods.
 *
 * <non-compliant>
 *  val map = mutableMapOf<String, String>()
 *  map.set("key", "value")
 *  val value = map.get("key")
 *  val newValue = StringBuilder(value).set(1, value.get(1).capitalize())
 *  val array = arrayOf<Int>()
 *  array.set(0, 3)
 *  println(array.get(0))
 * </non-compliant>
 *
 * <compliant>
 *  val map = mutableMapOf<String, String>()
 *  map["key"] = "value"
 *  val value = map["key"]
 *  val newValue = StringBuilder(value)[1, value[1].capitalize()]
 *  val array = arrayOf<Int>()
 *  array[0, 3]
 *  println(array[0])
 * </compliant>
 */
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
