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

import com.pellcorp.jaxb.test.AbstractTestCase;
import com.pellcorp.jaxb.test.TestUtils;

public class WsImportTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsImportTest.class);

    static File tmpDir;
    static File wsdl;

    @BeforeClass
    public static void startServers() throws Exception {
        AbstractTestCase.createServer(PersonService.class, new PersonServiceImpl());
        tmpDir = createTempDirectory();
        wsdl = generateTempWsdl();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        AbstractTestCase.cleanupServers();
        /* clean up */
        for(File dir : Arrays.asList(wsdl.getParentFile(), tmpDir)) {
            FileUtils.deleteDirectory(dir);
            if(dir.exists()) {
                LOGGER.warn("Unable to delete temporary directory: " + tmpDir);
            }
        }
    }

    @Test
    public void testGenerateFiles() throws Exception {

        LOGGER.info("Using generated WSDL file " + wsdl);
        LOGGER.info("Generating to directory " + tmpDir);
        File rootPath = new File(
                WsImportTest.class.getResource("/").getFile()) /* /jaxb-impl/target/test-classes */
                .getParentFile() /* /jaxb-impl/target */
                .getParentFile() /* /jaxb-impl */
                .getParentFile(); /* / */
        LOGGER.info("Using code root path: " + rootPath);

        String cmds[] = {rootPath + "/bin/wsimport.sh", 
                "-keep", 
                "-B-jaxb-facets", 
                "-d", tmpDir.getAbsolutePath(), 
                wsdl.getAbsolutePath()};
        LOGGER.info("Running command: " + Arrays.asList(cmds));

        Process p = Runtime.getRuntime().exec(cmds);
        int result = p.waitFor();
        if(result != 0) {
        	String stdout = TestUtils.readStream(p.getInputStream());
        	String stderr = TestUtils.readStream(p.getErrorStream());
            String output = "STDOUT: " + stdout + "\nSTDERR: " + stderr;
            LOGGER.info("wsimport failed. Output: " + output + "\n------");
        }

        File genDir = new File(tmpDir, "at/ac/tuwien/infosys/service/personservice");
        assertTrue(new File(genDir, "PersonService.java").exists());
        assertTrue(new File(genDir, "TestRequest.java").exists());

        String file1 = TestUtils.readFile(new File(genDir, "TestRequest.java"));
        /* '(?s)' in regex means Pattern.DOTALL */
        assertTrue(file1.matches("(?s).*@Facets\\(.*pattern\\s*=\\s*\"\\[0-9\\]\\+\".*"));
        assertTrue(file1.matches("(?s).*@Facets\\(.*maxExclusive\\s*=\\s*\"840\".*"));
        assertTrue(file1.matches("(?s).*@Facets\\(.*minInclusive\\s*=\\s*\"2012-12-24T12:00:00Z\".*"));
        assertTrue(file1.matches("(?s).*@Documentation\\(.*\"doc 3\".*"));

        // TODO: add more checks
    }

    /**
     * Create a generated test WSDL in a new temporary directory.
     * @return
     * @throws IOException
     */
    private static File generateTempWsdl() throws IOException {
        File dir = createTempDirectory();
        File tmpWsdl = new File(dir, "test.wsdl");
        String doc = XmlSchemaEnhancerTest.readWsdl(PersonService.class);
        FileUtils.write(tmpWsdl, doc);
        return tmpWsdl;
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
