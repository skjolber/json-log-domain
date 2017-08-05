package com.github.skjolberg.slf4j.codegen.maven;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.codegen.JavaGenerator;
import com.github.skjolber.log.domain.codegen.MarkdownGenerator;

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
		    	File javaOutput = new File(outputDirectory, "java/");
		    	File markdownOutput = new File(outputDirectory, "markdown/");
		    	
		    	if(!markdownOutput.exists() && !markdownOutput.mkdirs()) {
		    		throw new IOException("Problem creating directory " + markdownOutput.getAbsolutePath());
		    	}

		    	if(!javaOutput.exists() && !javaOutput.mkdirs()) {
		    		throw new IOException("Problem creating directory " + javaOutput.getAbsolutePath());
		    	}

		        for(Domain domain : domains) {
	        		com.github.skjolber.log.domain.codegen.Domain result = DomainFactory.parse(new FileReader(domain.getPath()));

	        		JavaGenerator.generate(result, javaOutput);
	        		MarkdownGenerator.generate(result, new File(markdownOutput, result.getName() + ".md"), true);
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
