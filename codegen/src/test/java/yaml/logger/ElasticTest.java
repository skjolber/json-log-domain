package yaml.logger;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileReader;

import org.junit.Test;

import com.github.skjolber.log.domain.codegen.Domain;
import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.codegen.ElasticGenerator;

public class ElasticTest {
	
	@Test
	public void generateSources() throws Exception {
	    Domain domain = DomainFactory.parse(new FileReader(new File("src/test/resources/yaml/network.yaml")));
	    
		String generate = ElasticGenerator.generate(domain);
		
		assertThat(generate, containsString("host"));
		
		System.out.println(generate);
	}

}

