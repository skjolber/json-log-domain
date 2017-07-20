package yaml.logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Test;

import com.github.skjolber.log.domain.codegen.JavaGenerator;

public class YamlTest {
	
	@Test
	public void generateSources() throws Exception {
	    File directory = new File("target/generated-sources/log/");

		JavaGenerator.generate(new File("src/test/resources/yaml/agresso.yaml"), directory);
		JavaGenerator.generate(new File("src/test/resources/yaml/global.yaml"), directory);
		JavaGenerator.generate(new File("src/test/resources/yaml/network.yaml"), directory);
		
		// verify that the generated classes can be loaded
		for(String domain : new String[]{"agresso", "global", "network"}) {
			File network = new File(String.format("target/generated-sources/log/com/example/%s/", domain));
			File[] results = network.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".java");
				}
			});

			List<String> files = new ArrayList<>();
			for(File result : results) {
				files.add(result.getAbsolutePath());
			}

		    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		    int status = compiler.run(null, null, null, files.toArray(new String[files.size()]));
		    if(status != 0) {
		    	throw new IllegalArgumentException();
		    }			
		}
	}
	
/*
	@Test
	public void testLogger() throws Exception {
		File file = new File("src/test/resources/yaml/network.yaml");
		Domain ontology = DomainFactory.parse(new FileReader(file));

		JavaFile logger = LoggerGenerator.logger(ontology);
		
		StringWriter writer = new StringWriter();
		
		logger.writeTo(writer );
		
		System.out.println(writer);
		
		AgressoLogger l = new AgressoLogger(LoggerFactory.getLogger(YamlTest.class));
		
		l.info().username("thomas").log("message");
		l.debug().log("TEST");
		
		
		
	}
*/
	
}
