package com.fan.transfer.integrational.di

import com.fan.transfer.integrational.utils.RestClient

interface RestClientFactory {
    RestClient create(String endpoint)
}
