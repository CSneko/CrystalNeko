package org.cneko.crystalneko.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

open class ThreadExecutor(var threadName: String) : Runnable {
    private val taskQueue: LinkedBlockingQueue<Runnable> = LinkedBlockingQueue()
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun start() {
        executorService.execute(this)
    }

    fun executeTask(runnable: Runnable) {
        taskQueue.offer(runnable)
    }

    override fun run() {
        println("$threadName started.")
        while (!Thread.currentThread().isInterrupted) {
            val task = taskQueue.poll(1, TimeUnit.SECONDS)
            task?.run()
        }
        println("$threadName stopped.")
    }

    /**
     * Gracefully shuts down the executor service.
     */
    fun shutdown() {
        executorService.shutdown()
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executorService.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}