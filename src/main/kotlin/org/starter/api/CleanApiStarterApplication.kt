package org.starter.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CleanApiStarterApplication

fun main(args: Array<String>) {
    runApplication<CleanApiStarterApplication>(*args)
}
