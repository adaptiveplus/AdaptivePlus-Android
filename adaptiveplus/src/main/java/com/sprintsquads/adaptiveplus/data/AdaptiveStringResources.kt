package com.sprintsquads.adaptiveplus.data


private val strings = mapOf(
    "en" to mapOf(
        "ap_seconds" to "seconds",
        "ap_minutes" to "minutes",
        "ap_hours" to "hours",
        "ap_days" to "days",
        "ap_months" to "months",
        "ap_select_answer_option" to "Select answer option:",
        "ap_thank_you_for_your_reply" to "Thank you for your reply!",
        "ap_thank_you" to "Thank you!",
        "ap_we_got_your_response" to "We got your response"
    ),
    "ru" to mapOf(
        "ap_seconds" to "секунды",
        "ap_minutes" to "минуты",
        "ap_hours" to "часы",
        "ap_days" to "дни",
        "ap_months" to "месяцы",
        "ap_select_answer_option" to "Выберите вариант ответа:",
        "ap_thank_you_for_your_reply" to "Благодарим за ответ!",
        "ap_thank_you" to "Спасибо!",
        "ap_we_got_your_response" to "Мы получили ваш ответ"
    ),
    "kz" to mapOf(
        "ap_seconds" to "секунд",
        "ap_minutes" to "минут",
        "ap_hours" to "сағат",
        "ap_days" to "күн",
        "ap_months" to "ай",
        "ap_select_answer_option" to "Жауап опциясын таңдаңыз:",
        "ap_thank_you_for_your_reply" to "Жауабыңыз үшін рахмет!",
        "ap_thank_you" to "Рахмет!",
        "ap_we_got_your_response" to "Біз сіздің жауабыңызды алдық"
    )
)

internal fun getAdaptiveStringResource(stringId: String): String? {
    return strings[LOCALE]?.get(stringId)
        ?: strings["ru"]?.get(stringId)
}