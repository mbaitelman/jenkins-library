def call(){
  return currentBuild.getRawBuild().getActions(jenkins.model.InterruptedBuildAction).getCauses().getDisplayName()
}
