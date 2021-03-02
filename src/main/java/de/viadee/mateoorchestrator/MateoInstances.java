package de.viadee.mateoorchestrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Marcel_Flasskamp
 */
public class MateoInstances {

    private static final Logger LOGGER = LoggerFactory.getLogger(MateoInstances.class);

    private static final String FILENAME = "src/main/resources/mateoInstances.txt";

    private static final String EMPTY_STRING = "";

    private MateoInstances() {
    }

    public static boolean addMateo(URI mateoUri) throws IOException {
        if (!getMateos().contains(mateoUri)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, true))) {
                writer.newLine();
                writer.write(mateoUri.toString());
                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean removeAll() throws IOException {
        return Files.deleteIfExists(new File(FILENAME).toPath());
    }

    public static boolean removeMateo(URI mateoUri) throws IOException {
        List<String> listLines = readFileToList();
        if (listLines.contains(mateoUri.toString())) {
            listLines.remove(mateoUri.toString());
            writeListToFile(listLines);
        } else {
            return false;
        }
        return true;
    }

    private static List<String> readFileToList() throws IOException {
        List<String> listLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            while (reader.ready()) {
                String line = reader.readLine();
                listLines.add(line);
            }
        }
        return listLines;
    }

    public static Set<URI> getMateos() {
        Set<URI> mateoList = new HashSet<>();
        ifFileDoesntExistsGenerateIt();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (!line.isBlank() && !line.startsWith("#"))
                    mateoList.add(new URI(line));
            }
        } catch (IOException e) {
            LOGGER.error("Couldn't read mateoInstances File.");
        } catch (URISyntaxException e) {
            LOGGER.error("Some lines doesn't contain a valid URI");
        }
        return mateoList;
    }

    private static void ifFileDoesntExistsGenerateIt() {
        if (!new File(FILENAME).exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
                writer.write(EMPTY_STRING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean writeListToFile(List<String> lines) throws IOException {
        boolean first = true;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            for (String line : lines) {
                if (first) {
                    writer.write(line);
                    first = false;
                } else {
                    writer.newLine();
                    writer.write(line);
                }
            }
        }
        return new File(FILENAME).exists();
    }
}
