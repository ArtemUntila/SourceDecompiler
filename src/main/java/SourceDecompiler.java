import jdk.internal.org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

public class SourceDecompiler {

    ClassLoader classLoader;

    public SourceDecompiler(String pathToJarFile) {
        try {
            classLoader = new URLClassLoader(new URL[]{new URL("file:" + pathToJarFile)});
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getSource(String fullyQualifiedName) {
        try {
            classLoader.loadClass(fullyQualifiedName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        String classFileName = getFileName(fullyQualifiedName);
        InputStream original = classLoader.getResourceAsStream(classFileName);
        Objects.requireNonNull(original);

        try {
            ClassReader reader = new ClassReader(original);
            SourceVisitor sourceVisitor = new SourceVisitor();
            reader.accept(sourceVisitor, 1); /**{@link ClassReader.SKIP_CODE} -- skipping method bodies, etc*/
            return sourceVisitor.source;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileName(String fullyQualifiedName) {
        return fullyQualifiedName.replace('.', '/') + ".class";
    }

    private static class SourceVisitor extends ClassVisitor {

        String source;

        public SourceVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visitSource(String source, String debug) {
            this.source = source;
        }
    }
}
