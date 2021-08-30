import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        String pathToJar = args[0];
        String slice = args[1];

        SourceDecompiler sourceDecompiler = new SourceDecompiler(pathToJar);

        BufferedReader reader = Files.newBufferedReader(Paths.get(slice));
        PrintWriter printWriter = new PrintWriter("slice.log");

        Map<String, String> map = new HashMap<>();

        String line = reader.readLine();
        while (line != null) {
            String[] pair = line.split(":");

            String nameWithPackage = pair[0];
            String className = nameWithPackage.replaceFirst("(\\w+\\.)*", "");

            if (!map.containsKey(className)) {
                String source = sourceDecompiler.getSource(nameWithPackage);
                map.put(className, source);
            }
            String source = map.get(className);

            printWriter.println(line.replace('.', '/').replace(className, source));

            line = reader.readLine();
        }

        printWriter.flush();
        System.out.println(map);
    }
}
