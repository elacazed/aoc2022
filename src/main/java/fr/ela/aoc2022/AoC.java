package fr.ela.aoc2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class AoC {

    public abstract void run();

    public static void repeat(int times, IntConsumer action) {
        IntStream.range(0, times).forEach(action);
    }

    private static String getDirectoryName(Class clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    private static String getFileName(String name) {
        String fileName = name;
        if (Boolean.parseBoolean(System.getProperty("test", "false"))) {
            fileName = fileName.concat("-test");
        }
        return fileName.concat(".txt");
    }

    private Path getPath(String name) {
        return Paths.get("target", "classes", getDirectoryName(getClass()), getFileName(name));
    }

    public Path getTestInputPath() {
        return getPath("input-test");
    }

    public Path getInputPath() {
        return getPath("input");
    }
    public String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    public List<List<String>> splitOnEmptyLines(Path path) {
        List<List<String>> result = new ArrayList<>();
        List<String> current = new ArrayList<>();
        result.add(current);
        for (String line : list(path)) {
            if (line.isEmpty()) {
                current = new ArrayList<>();
                result.add(current);
            } else {
                current.add(line);
            }
        }
        return result;
    }


    public <T> Stream<T> oneLineStream(Path path, String sep, Function<String, T> mapper) {
        return Arrays.stream(readFile(path).split(sep)).map(mapper);
    }

    public <T> List<T> oneLineList(Path path, String sep, Function<String, T> mapper) {
        return Arrays.stream(readFile(path).split(sep)).map(mapper).collect(Collectors.toList());
    }

    public <T> List<T> list(Path path, Function<String, T> mapper) {
        return stream(path, mapper).collect(Collectors.toList());
    }

    public Stream<String> stream(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public List<String> list(Path path) {
        return stream(path).collect(Collectors.toList());
    }

    public <T> Stream<T> stream(Path path, Function<String, T> mapper) {
        try {
            return Files.lines(path).map(mapper);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    boolean inRange(int i, int low, int high) {
        return i >= low && i < high;
    }

    public static void main(String[] args) {
        try {
            String className = args[0];
            Class<AoC> clazz = (Class<AoC>) Class.forName(AoC.class.getPackageName()+"."+className);
            AoC instance = clazz.getDeclaredConstructor().newInstance();
            instance.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
