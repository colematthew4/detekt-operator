package io.cole.matthew.detekt.operator

import io.cole.matthew.detekt.operator.rules.PreferInOverContainsSyntax
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class DetektOperatorProvider : RuleSetProvider {
    override val ruleSetId: String = "detekt-operator"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(PreferInOverContainsSyntax())
    )
}