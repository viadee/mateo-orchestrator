package de.viadee.mateoorchestrator;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel_Flasskamp
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig
class MateoInstancesTest {

    private static final String FILENAME = "src/main/resources/mateoInstances.txt";

    @BeforeEach
    public void setUp() throws IOException {
        File fileBackup = new File("src/test/resources/mateoInstancesTestdata.txt");
        File fileCopied = new File(FILENAME);

        if (fileCopied.exists())
            fileCopied.delete();
        FileUtils.copyFile(fileBackup, fileCopied);
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        File fileExample = new File("src/test/resources/mateoInstancesExample.txt");
        File fileCopied = new File(FILENAME);

        if (fileCopied.exists())
            fileCopied.delete();
        FileUtils.copyFile(fileExample, fileCopied);
    }

    @Test
    void testGetMateos() {
        Set<URI> mateos = MateoInstances.getMateos();
        assertNotNull(mateos);
        assertEquals(2, mateos.size());
    }

    @Test
    void testAddRemoveMateo() throws URISyntaxException, IOException {
        assertEquals(2, MateoInstances.getMateos().size());

        MateoInstances.addMateo(new URI("http:localhost:1234"));
        assertEquals(3, MateoInstances.getMateos().size());

        MateoInstances.removeMateo(new URI("http:localhost:1234"));
        assertEquals(2, MateoInstances.getMateos().size());
    }

    @Test
    void testRemoveMateo_NotExists() throws URISyntaxException, IOException {
        boolean result = MateoInstances.removeMateo(new URI("http:localhost:1111"));
        assertFalse(result);
        assertEquals(2, MateoInstances.getMateos().size());
    }

    @Test
    void testRemoveAllMateo() throws IOException {
        boolean result = MateoInstances.removeAll();
        assertTrue(result);
        assertEquals(0, MateoInstances.getMateos().size());
    }
}