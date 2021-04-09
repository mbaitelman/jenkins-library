def call(){
  return currentBuild.getRawBuild().getActions(jenkins.model.InterruptedBuildAction)[0].getDisplayName()
}
