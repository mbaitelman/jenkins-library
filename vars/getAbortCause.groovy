def call(){
  return currentBuild.getRawBuild().getActions(jenkins.model.InterruptedBuildAction).getCauses()[0].getDisplayName()
}
