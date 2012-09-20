package org.gosu_lang.tools.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Set;

/**
 * Goal which touches a timestamp file.
 *
 * @goal insert-gosu-artifact
 * 
 * @phase process-sources
 */
public class GosuArtifactMojo
    extends AbstractMojo
{
  private static final String GOSU_GROUPID = "org.gosu-lang.gosu";
  private static final String CORE_API_ARTIFACTID = "gosu-core-api";
  private static final String CORE_ARTIFACTID = "gosu-core";

  /**
   * Used to look up Artifacts in the remote repository.
   *
   * @component
   * @required
   * @readonly
   */
  protected ArtifactFactory _factory;

  /**
   * The version of Gosu to compile against.
   *
   * @parameter
   * @required
   */
  @SuppressWarnings("UnusedDeclaration")
  private String gosuVersion;

  /**
   * Include gosu-core in the compile classpath.
   *
   * @parameter default-value=false
   */
  @SuppressWarnings("UnusedDeclaration")
  private boolean includeImpl;

  /**
   * Include gosu-test-api in the compile classpath.
   *
   * @parameter default-value=false
   */
  @SuppressWarnings("UnusedDeclaration")
  private boolean includeTestApi;

  public void execute() throws MojoExecutionException {
    MavenProject project = (MavenProject)getPluginContext().get("project");

    @SuppressWarnings("unchecked") List<Dependency> dependencies = (List<Dependency>) project.getModel().getDependencies();

    for (Dependency dep : dependencies) {
      String artifactId = dep.getArtifactId();
      String groupId = dep.getGroupId();
      if (GOSU_GROUPID.equals(groupId) &&
              (CORE_API_ARTIFACTID.equals(artifactId)
                || CORE_ARTIFACTID.equals(artifactId))) {
        throw new MojoExecutionException("Your project cannot explicitly depend on Gosu artifacts with this plugin in use (" + dep + ")");
      }
    }

    @SuppressWarnings("unchecked") Set<Artifact> artifacts = (Set<Artifact>) project.getDependencyArtifacts();

    Artifact artifact = _factory.createArtifact(GOSU_GROUPID, CORE_API_ARTIFACTID, gosuVersion, "compile", "jar");
    getLog().info("Inserting " + artifact + " into the compile classpath.");
    if (!artifacts.contains(artifact)) {
      artifacts.add(artifact);
    }

    if (includeImpl) {
      artifact = _factory.createArtifact(GOSU_GROUPID, CORE_ARTIFACTID, gosuVersion, "compile", "jar");
      getLog().info("Inserting " + artifact + " into the compile classpath. Naughty!");
      if (!artifacts.contains(artifact)) {
        artifacts.add(artifact);
      }
    }
  }
}
