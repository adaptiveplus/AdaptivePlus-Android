package com.sprintsquads.adaptiveplusqaapp.data


enum class Environment(val value: String) {
    STAGING("STAGING"),
    MOCK("MOCK"),
    HOMEBANK_DEV("HOMEBANK_DEV"),
    HOMEBANK_PROD("HOMEBANK_PROD"),
    MYCAR("MYCAR"),
    MYCAR_PREPROD("MYCAR_PREPROD")
}