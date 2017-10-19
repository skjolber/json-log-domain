package com.github.skjolberg.slf4j.codegen.maven;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    protected String outputDirectory;
    
    @Parameter(property = "domains", required = true)
    protected List<Domain> domains;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    public void execute() throws MojoExecutionException {

        if(!domains.isEmpty()) {
		    try {
	        	Path outputDirectoryPath = Paths.get(outputDirectory);
	        	
	        	if(!Files.exists(outputDirectoryPath)) Files.createDirectories(outputDirectoryPath);
        	
		    	Path javaOutput = Paths.get(outputDirectory, "java/");
		    	Path markdownOutput = Paths.get(outputDirectory, "markdown/");
		    	
		    	if(!Files.exists(javaOutput)) Files.createDirectory(javaOutput);
		    	if(!Files.exists(markdownOutput)) Files.createDirectory(markdownOutput);

		        for(Domain domain : domains) {
		            getLog().info("Processing " + domain.getPath());

	        		com.github.skjolber.log.domain.model.Domain result = DomainFactory.parse(Files.newBufferedReader(Paths.get(domain.getPath()), StandardCharsets.UTF_8));

	        		JavaGenerator.generate(result, javaOutput);
	        		MarkdownGenerator.generate(result, markdownOutput.resolve(result.getName() + ".md"), true);
		        }
				
				if(project != null) {
					project.addCompileSourceRoot(javaOutput.toRealPath().toString());
				}
			} catch (IOException e) {
				throw new MojoExecutionException("Problem generating sources", e);
			}
        } else {
            getLog().info("No files found");
        }
        
    }

}
