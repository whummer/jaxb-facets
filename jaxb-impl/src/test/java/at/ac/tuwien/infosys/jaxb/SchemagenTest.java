package at.ac.tuwien.infosys.jaxb;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.infosys.jaxb.test.XmlTestType;

import com.pellcorp.jaxb.test.TestUtils;

public class SchemagenTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsImportTest.class);

    static File tmpDir;

    @BeforeClass
    public static void startServers() throws Exception {
    	/* disable XSD 1.1 features! */
    	XmlSchemaEnhancer.XSD_11_ENABLED.set(false);

        tmpDir = createTempDirectory();
    }

    @AfterClass
    public static void cleanup() throws Exception {
    	/* re-enable XSD 1.1 features */
    	XmlSchemaEnhancer.XSD_11_ENABLED.set(true);

        /* clean up */
        for(File dir : Arrays.asList(tmpDir)) {
            FileUtils.deleteDirectory(dir);
            if(dir.exists()) {
                LOGGER.warn("Unable to delete temporary directory: " + tmpDir);
            }
        }
    }

    @Test
    public void testGenerateFiles() throws Exception {

        LOGGER.info("Generating to directory " + tmpDir);
        File rootPath = new File(
                WsImportTest.class.getResource("/").getFile()) /* /jaxb-impl/target/test-classes */
                .getParentFile() /* /jaxb-impl/target */
                .getParentFile() /* /jaxb-impl */
                .getParentFile(); /* / */
        LOGGER.info("Using code root path: " + rootPath);

        String cmds[] = {rootPath + "/bin/schemagen.sh", 
                "-d",
                tmpDir.getAbsolutePath(),
                "-cp",
                rootPath + "/jaxb-impl/target/test-classes/:" + 
                rootPath + "/jaxb-api/target/classes/",
                XmlTestType.class.getName()};
        LOGGER.info("Running command: " + Arrays.asList(cmds));

        Process p = Runtime.getRuntime().exec(cmds);
        int result = p.waitFor();
        if(result != 0) {
        	String stdout = TestUtils.readStream(p.getInputStream());
        	String stderr = TestUtils.readStream(p.getErrorStream());
            String output = "STDOUT: " + stdout + "\nSTDERR: " + stderr;
            LOGGER.info("wsimport failed. Output: " + output + "\n------");
        }

        File genDir = tmpDir;
        String schemaName = "schema1.xsd";
        File schemaFile = new File(genDir, schemaName);
        assertTrue(schemaFile.exists());

        String file1 = TestUtils.readFile(schemaFile);
        /* '(?s)' in regex means Pattern.DOTALL */
        assertTrue(file1.matches("(?s).*:minInclusive value=\"100\".*"));

        // TODO: add more checks
    }

    private static File createTempDirectory() throws IOException {
        return createTempDirectory("jaxb-facets");
    }
    private static File createTempDirectory(String prefix) throws IOException {
        final File temp;
        temp = File.createTempFile(prefix, Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
        return temp;
    }
}
