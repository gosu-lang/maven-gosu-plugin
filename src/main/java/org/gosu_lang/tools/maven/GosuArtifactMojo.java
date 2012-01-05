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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
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
  private String gosuVersion;

  /**
   * Include gosu-core in the compile classpath.
   *
   * @parameter default-value=false
   */
  private boolean includeImpl;

  public void execute() throws MojoExecutionException {
    MavenProject project = (MavenProject)getPluginContext().get("project");

    LinkedHashSet artifacts = new LinkedHashSet(project.getDependencyArtifacts());

    for (Object o : artifacts) {
      Artifact artifact = (Artifact)o;
      if ("gw".equals(artifact.getGroupId()) && artifact.getArtifactId().startsWith("gosu")) {
        throw new MojoExecutionException("Your project cannot explicitly depend on Gosu artifacts with this plugin in use (" + artifact + ")");
      }
    }

    Artifact artifact = _factory.createArtifact("org.gosu-lang.gosu", "gosu", gosuVersion, "compile", "pom");
    getLog().info("Inserting " + artifact + " into the compile classpath.");
    artifacts.add(artifact);

    if (includeImpl) {
      artifact = _factory.createArtifact("org.gosu-lang.gosu", "gosu-core", gosuVersion, "compile", "jar");
      getLog().info("Inserting " + artifact + " into the compile classpath. Naughty!");
      artifacts.add(artifact);
    }

    artifact = _factory.createArtifact("org.gosu-lang.gosu", "gosu-test-api", gosuVersion, "test", "jar");
    getLog().info("Inserting " + artifact + " into the test classpath");
    artifacts.add(artifact);

    project.setDependencyArtifacts(artifacts);
  }
}
