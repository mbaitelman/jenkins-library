def call(){
  return currentBuild.getRawBuild().getActions(jenkins.model.InterruptedBuildAction)[0].getCauses()[0].getShortDescription()
}
