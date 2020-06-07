# detekt-operator

Detekt style rules to prefer expressions over named functions for kotlin operators.

[![Build Status](https://img.shields.io/travis/colematthew4/detekt-operator)](https://travis-ci.org/github/colematthew4/detekt-operator)
[![codecov](https://codecov.io/gh/colematthew4/detekt-operator/branch/master/graph/badge.svg)](https://codecov.io/gh/colematthew4/detekt-operator)

detekt-operator is a plugin for [detekt](https://github.com/arturbosch/detekt) that performs style checking for the use of named functions instead of their operator equivalents.

## Currently Supported Operators

- [x] Unary Operators
    - [x] `a.unaryPlus()` -> `+a`
    - [x] `a.unaryMinus()` -> `-a`
    - [x] `a.not()` -> `!a`
    - [x] `a.inc()` -> `a++`
    - [x] `a.dec()` -> `a--`
- [x] Arithmetic Operators
    - [x] `a.plus(b)` -> `a + b`
    - [x] `a.minus(b)` -> `a - b`
    - [x] `a.times(b)` -> `a * b`
    - [x] `a.div(b)` -> `a / b`
    - [x] `a.rem(b)` -> `a % b`
    - [x] `a.rangeTo(b)` -> `a..b`
- [x] `a.contains(b)` -> `b in a`
- [ ] `a.get(i[, j...])` -> `a[i(, j...)]`
- [ ] `a.invoke()` -> `a()`
- [ ] Augmented Assignments
    - [ ] `a.plusAssign(b)` -> `a += b`
    - [ ] `a.minusAssign(b)` -> `a -= b`
    - [ ] `a.timesAssign(b)` -> `a *= b`
    - [ ] `a.divAssign(b)` -> `a /= b`
    - [ ] `a.remAssign(b)` -> `a %= b`
- [ ] Equality and Inequality Operators
    - [ ] `a.equals(b)` -> `a == b`
    - [ ] `!a.equals(b)` -> `a != b`
- [ ] Infix functions
- [ ] Comparison Operators
    - [ ] `a.compareTo(b)` -> `a > b`
    - [ ] `a.compareTo(b)` -> `a < b`
    - [ ] `a.compareTo(b)` -> `a >= b`
    - [ ] `a.compareTo(b)` -> `a <= b`

## Include in your project

### Detekt CLI

```bash
detekt --input ... --plugins /path/to/detekt-operator/jar
```

### Gradle

```groovy
dependencies {
    detektPlugins "io.cole.matthew.detekt.operator:detekt-operator:0.0.1"
}
```

If you are encountering issues, visit [detekt's documentation](https://detekt.github.io/detekt/extensions.html#pitfalls) or raise an issue!
