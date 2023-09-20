package net.flyingelectrons.dnsupdater

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DnsupdaterApplication

fun main(args: Array<String>) {
    runApplication<DnsupdaterApplication>(*args)
}
