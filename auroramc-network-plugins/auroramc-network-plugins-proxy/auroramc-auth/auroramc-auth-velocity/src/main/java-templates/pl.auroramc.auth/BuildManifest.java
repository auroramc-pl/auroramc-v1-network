package pl.auroramc.auth;

class BuildManifest {

  static final String PROJECT_ARTIFACT_ID = "${project.parent.artifactId}";
  static final String PROJECT_VERSION = "${project.version}";

  private BuildManifest() {

  }
}