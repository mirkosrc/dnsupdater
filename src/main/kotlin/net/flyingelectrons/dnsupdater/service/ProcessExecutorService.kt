package net.flyingelectrons.dnsupdater.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.Executors
import java.util.function.Consumer

@Service
class ProcessExecutorService {

    fun execute(unixCommand: String): String {
        LOGGER.info("execute unixCommand $unixCommand")
        var out = ""
        val process: Process = Runtime.getRuntime()
            .exec(unixCommand)
        val streamGobbler = StreamGobbler(
            process.inputStream, fun(x: String?) {
                println(x)
                out = x.orEmpty()
            }
        )
        Executors.newSingleThreadExecutor().submit(streamGobbler)
        val exitCode = process.waitFor()
        assert(exitCode == 0)
        return out
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}

private class StreamGobbler(private val inputStream: InputStream, consumer: Consumer<String>) :
    Runnable {
    private val consumer: Consumer<String>
    override fun run() {
        BufferedReader(InputStreamReader(inputStream)).lines()
            .forEach(consumer)
    }

    init {
        this.consumer = consumer
    }
}
