package com.github.skjolberg.slf4j.codegen.maven;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.github.skjolber.log.domain.codegen.JavaGenerator;

@Mojo(name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresOnline = false, requiresProject = true,
        threadSafe = false, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class CodeGenMojo extends AbstractMojo {

    @Parameter(property = "outputDirectory", required = true)
    protected File outputDirectory;
    
    @Parameter(property = "domains", required = true)
    protected List<Domain> domains;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    public void execute() throws MojoExecutionException {

    	outputDirectory.mkdirs();

        if(!domains.isEmpty()) {
		    try {
		        for(Domain domain : domains) {
		    		JavaGenerator.generate(domain.getPath(), outputDirectory);
		        }
				
				if(project != null) {
					project.addCompileSourceRoot(outputDirectory.getCanonicalPath());
				}
			} catch (IOException e) {
				throw new MojoExecutionException("Problem generating sources", e);
			}
        }
        
        getLog().info("Got " + domains);
    }

}
