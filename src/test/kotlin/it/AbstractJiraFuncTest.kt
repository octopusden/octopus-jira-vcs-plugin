package it

import com.atlassian.jira.functest.framework.FuncTestCase
import java.util.concurrent.TimeUnit

const val RESTORING_RETRY_THRESHOLD = 3

abstract class AbstractJiraFuncTest : FuncTestCase() {

    override fun setUpTest() {
        restoreDump()
    }

    private fun restoreDump() {
        var success = false
        var count = 0
        do {
            try {
                if (count > 0) {
                    TimeUnit.SECONDS.sleep(2)
                }
                count++
                val testDumpName = "dump.zip"
                logger.log("Restoring $testDumpName")
                administration.restoreData(testDumpName)
                success = true
            } catch (e: Throwable) {
                logger.log(e)
            }
        } while (count < RESTORING_RETRY_THRESHOLD && !success)
        if (!success) {
            throw AssertionError("Restoring dump attempts ($RESTORING_RETRY_THRESHOLD) exceeded")
        }
    }
}
