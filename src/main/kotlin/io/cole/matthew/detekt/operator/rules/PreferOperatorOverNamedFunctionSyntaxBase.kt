package io.cole.matthew.detekt.operator.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity

/**
 * This rule detects the usage of named functions to perform operator expressions.
 *
 * Using <value1> operator <value2> is preferred (vs <value1>.op(<value2>)
 */
abstract class PreferOperatorOverNamedFunctionSyntaxBase(
    description: String,
    config: Config = Config.empty
) : Rule(config) {
    override val issue: Issue = Issue(javaClass.simpleName, Severity.Style, description, Debt.FIVE_MINS)
}
