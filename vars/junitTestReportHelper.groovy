import hudson.tasks.test.AbstractTestResultAction
import hudson.tasks.test.TestResult;

def call() {
    def testStatus = ""
    AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    if (testResultAction != null) {
        def total = testResultAction.totalCount
        def failed = testResultAction.failCount
        def skipped = testResultAction.skipCount
        def passed = total - failed - skipped
        testStatus = "Test Status: Total: ${total}, Skipped: ${skipped}, Passed: ${passed}, Failed: ${failed} ${testResultAction.failureDiffString}"

        if (failed == 0) {
            currentBuild.result = 'SUCCESS'
        } else {
            currentBuild.result = 'UNSTABLE'
        }
    }
    echo("TestStatus is:" + testStatus)
    return testStatus.toString()
}

def setBuildStatus(){
    AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    if (testResultAction != null) {
        def total = testResultAction.totalCount
        def failed = testResultAction.failCount
        def skipped = testResultAction.skipCount
        def passed = total - failed - skipped
        if(passed == 0){
            currentBuild.result = 'FAILURE'
        } else if(failed == 0) {
            currentBuild.result = 'SUCCESS'
        } else {
            currentBuild.result = 'UNSTABLE'
        }
    }
}

def getSlackMessage(Map args){
    StringBuilder slackMessage = new StringBuilder()
    boolean showDiff = args.showDiff
    boolean showFailures = args.showFailures
    

    AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    if (testResultAction != null) {
        def total = testResultAction.totalCount
        def failed = testResultAction.failCount
        def skipped = testResultAction.skipCount
        def passed = total - failed - skipped
        slackMessage.append("Test Status: Total: ${total}, Skipped: ${skipped}, Passed: ${passed}, Failed: ${failed}")
        if(showDiff){
            slackMessage.append(" ${testResultAction.failureDiffString}")
        }
        if (failed > 0 && showFailures) {
            slackMessage.append("\nFailed Tests:\n")
            for(TestResult result : action.getFailedTests()) {
                slackMessage.append("\t").append(result.getFullDisplayName()).append(" after ")
                    .append(result.getDurationString()).append("\n");
            }
        }
    }
    return slackMessage..toString()
}
