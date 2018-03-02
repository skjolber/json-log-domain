package com.github.skjolberg.slf4j.codegen.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class CodeGenTest extends AbstractMojoTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGen() throws Exception {
        File pom = getTestFile("src/test/resources/unit/domain-log-codegen/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        CodeGenMojo myMojo = (CodeGenMojo) lookupMojo("generate", pom);
        assertNotNull(myMojo);
        myMojo.execute();
    }
}