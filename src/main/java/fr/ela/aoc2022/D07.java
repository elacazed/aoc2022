package fr.ela.aoc2022;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class D07 extends AoC {


    public class File {

        private final File parent;
        private final List<File> files = new ArrayList<>();
        private final String name;
        private final int size;

        public File(File parent, String name, int size) {
            this.name = name;
            this.size = size;
            this.parent = parent;
        }

        public File(File parent, String name) {
            this(parent, name, -1);
        }

        public int size() {
            return isDir() ? files.stream().mapToInt(File::size).sum() : size;
        }

        public File addFile(String name, int size) {
            File f= new File(this, name, size);
            files.add(f);
            return f;
        }

        public File addDirectory(String name) {
            File dir =new File(this, name);
            files.add(dir);
            return dir;
        }

        public File get(String name) {
            return files.stream().filter(f -> f.name.equals(name)).findFirst().orElseThrow();
        }

        public boolean isDir() {
            return size == -1;
        }

        public File getParent() {
            return parent;
        }

    }
    public FsCrawler buildFs(List<String> lines) {
        FsCrawler crawler = new FsCrawler();
        crawler.crawl(lines);
        return crawler;
    }

    public class FsCrawler {
        private File root = new File(null, "/");

        private List<File> directories = new ArrayList<>();

        private void crawl(List<String> input) {
            File current = root;

            Iterator<String> iterator = input.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.charAt(0) == '$') {
                    current = command(current, line.substring(2).split("\s"));
                } else {
                    if (line.startsWith("dir")) {
                        var newDir = current.addDirectory(line.substring(4));
                        directories.add(newDir);
                    } else {
                        String[] fileDef = line.split("\s");
                        File f = current.addFile(fileDef[1], Integer.parseInt(fileDef[0]));
                    }
                }
            }
        }

        private File command(File current, String[] args) {
            switch (args[0]) {
                case "ls":
                    return current;
                case "cd":
                    switch (args[1]) {
                        case "..":
                            return current.getParent();
                        case "/":
                            return root;
                        default:
                            return current.get(args[1]);
                    }
                default:
                    throw new IllegalArgumentException();
            }
        }
    }


    int spaceNeeded(FsCrawler crawler) {
        return 30000000 - (70000000 - crawler.root.size());
    }

    @Override
    public void run() {
        FsCrawler testCrawler = buildFs(list(getTestInputPath()));
        System.out.println("Test part one "+testCrawler.directories.stream().mapToInt(File::size).filter(s -> s <= 100000).sum());

        int size = testCrawler.directories.stream().mapToInt(File::size).sorted().filter(s -> s >= spaceNeeded(testCrawler)).findFirst().orElseThrow();
        System.out.println("Test part two "+size);

        FsCrawler crawler = buildFs(list(getInputPath()));
        System.out.println("Real part one "+crawler.directories.stream().mapToInt(File::size).filter(s -> s <= 100000).sum());

        size = crawler.directories.stream().mapToInt(File::size).sorted().filter(s -> s >= spaceNeeded(crawler)).findFirst().orElseThrow();
        System.out.println("Real part two "+size);

    }
}
