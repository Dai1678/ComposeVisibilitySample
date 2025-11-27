package dev.dai.compose.visibility.sample.di

import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient
