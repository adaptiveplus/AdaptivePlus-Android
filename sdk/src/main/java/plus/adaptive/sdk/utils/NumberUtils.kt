package plus.adaptive.sdk.utils


internal fun restrictToRange(value: Int, minValue: Int, maxValue: Int): Int {
    if (minValue > maxValue) return value

    return minOf(maxOf(value, minValue), maxValue)
}