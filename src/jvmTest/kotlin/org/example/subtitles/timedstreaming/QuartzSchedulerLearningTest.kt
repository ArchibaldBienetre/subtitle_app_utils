package org.example.subtitles.timedstreaming

import org.junit.Test
import org.quartz.Job
import org.quartz.JobBuilder.newJob
import org.quartz.JobExecutionContext
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import java.lang.System.currentTimeMillis
import java.util.*


val jobDataKeyStartTimeMs = "startTimeMs"
val jobDataKeyShowForMs = "showForMs"
val jobDataKeyScheduledAtOffsetMs = "scheduledAtOffsetMs"

class QuartzSchedulerLearningTest {


    @Test
    fun test() {
        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        val currentTime = currentTimeMillis()

        val trigger1 = newTrigger()
            .startAt(inXMilliseconds(2_000L))
            .build()
        val job1 = newJob(MyTestJob::class.java)
            .usingJobData(jobDataKeyStartTimeMs, currentTime)
            .usingJobData(jobDataKeyShowForMs, 1_000L)
            .usingJobData(jobDataKeyScheduledAtOffsetMs, 2_000L)
            .build()
        val trigger2 = newTrigger()
            .startAt(inXMilliseconds(8_000L))
            .build()
        val job2 = newJob(MyTestJob::class.java)
            .usingJobData(jobDataKeyStartTimeMs, currentTime)
            .usingJobData(jobDataKeyShowForMs, 3_000L)
            .usingJobData(jobDataKeyScheduledAtOffsetMs, 8_000L)
            .build()
        val trigger3 = newTrigger()
            .startAt(inXMilliseconds(15_000L))
            .build()
        val job3 = newJob(MyTestJob::class.java)
            .usingJobData(jobDataKeyStartTimeMs, currentTime)
            .usingJobData(jobDataKeyShowForMs, 5_000L)
            .usingJobData(jobDataKeyScheduledAtOffsetMs, 15_000L)
            .build()

        scheduler.start()

        scheduler.scheduleJob(job1, trigger1)
        scheduler.scheduleJob(job2, trigger2)
        scheduler.scheduleJob(job3, trigger3)


        print("Sleeping...")
        System.out.flush()
        Thread.sleep(15_000L)
        print("End of sleep.")
        scheduler.shutdown(true)
    }


    @Test
    fun test2() {
        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        scheduler.start()

        // define the job and tie it to our HelloJob class
        val job = newJob(HelloJob::class.java)
            .withIdentity("myJob", "group1")
            .build()
        val trigger = newTrigger()
            .withIdentity("myTrigger", "group1")
            .startNow()
            .withSchedule(
                simpleSchedule()
                    .withIntervalInSeconds(1)
                    .repeatForever()
            )
            .build()
        scheduler.scheduleJob(job, trigger)

        print("Sleeping...")
        System.out.flush()
        Thread.sleep(3_000L)
        print("End of sleep.")
        scheduler.shutdown(true)
    }

    private fun inXMilliseconds(x: Long): Date? {
        return Date(currentTimeMillis() + x)
    }
}

class MyTestJob : Job {

    override fun execute(context: JobExecutionContext) {
        println("MyTestJob is executing: '${context.jobDetail.key}'")
        val jobDataMap = context.jobDetail.jobDataMap
        val expectedOffset = jobDataMap.get(jobDataKeyScheduledAtOffsetMs)
        val startTime = jobDataMap.get(jobDataKeyStartTimeMs) as Long
        val actualOffset = currentTimeMillis() - startTime
        val showForMs = jobDataMap.get(jobDataKeyShowForMs) as Long
        println("Expected Offset: \t ${expectedOffset}")
        println("Actual Offset: \t ${actualOffset}")
        println("Showing for ${showForMs}ms")
        Thread.sleep(showForMs)
        println("MyTestJob is shutting down: '${context.jobDetail.key}'")
    }
}

class HelloJob : Job {

    override fun execute(context: JobExecutionContext) {
        println("Hello!  HelloJob is executing.")
    }
}