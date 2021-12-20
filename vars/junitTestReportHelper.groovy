import hudson.tasks.test.AbstractTestResultAction
import hudson.tasks.test.TestResult;

/*
Calling this allows for setting the build result based on the test result, not on script passing/failing
Run in a `post { always {} }` block so script issues dont affect status. 
```
stage("Run Tests"){
    steps {
        sh "./runtests.sh"
    }
    post {
        always {
            junit "./junit.xml"
            script {
                junitTestReportHelper.setBuildStatus()
            }
        }
    }
}
```
*/

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

/*
This method allows for more detailed reporting on errors. 
Based on https://github.com/jenkinsci/slack-plugin/blob/master/src/main/java/jenkins/plugins/slack/ActiveNotifier.java#L236
All options are optional
junitTestReportHelper.getSlackMessage(showFailures: true, showDiff: true, maxFailedTests: 5)
showFailures = (boolean) when enabled prints out maxFailedTests count of failed tests
maxFailedTests = (int) number of failed test messages to show
showDiff = (boolean) when enabled shows how many more/less test failures there were (used for when multiple jobs are called with different params and the historical data isnt useful)
*/

def getSlackMessage(Map args){
    StringBuilder slackMessage = new StringBuilder()
    boolean showDiff = args.showDiff
    boolean showFailures = args.showFailures
    int maxFailedTests = args.maxFailedTests ?: 5
    

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
            int count = 0;
            for(TestResult result : testResultAction.getFailedTests()) {
                slackMessage.append("\t").append(result.getFullDisplayName()).append(" after ")
                    .append(result.getDurationString()).append("\n");
                if (++count == maxFailedTests) break;
            }
        }
    }
    return slackMessage.toString()
}
