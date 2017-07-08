package yaml.logger;

import java.io.File;

import org.junit.Test;

import com.github.skjolber.log.domain.codegen.JavaGenerator;

public class YamlTest {
	
	@Test
	public void generateSources() throws Exception {
	    File directory = new File("target/generated-sources/log/");

		JavaGenerator.generate(new File("src/test/resources/yaml/agresso.yaml"), directory);
		JavaGenerator.generate(new File("src/test/resources/yaml/global.yaml"), directory);
		JavaGenerator.generate(new File("src/test/resources/yaml/network.yaml"), directory);
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
