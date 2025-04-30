package render_sdf.renderer;

import java.io.*;
import java.net.URI;
import java.util.Arrays;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;

public class MemoryClassLoader extends ClassLoader {


    public Class<?> compileAndLoad(String className, String javaSource) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StringWriter errorWriter = new StringWriter();
        ByteArrayOutputStream compiledBytesOutputStream = new ByteArrayOutputStream();

        SimpleJavaFileObject sourceFile = new SimpleJavaFileObject(URI.create("file:///" + className.replace('.', '/') + ".java"), Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncErrors) {
                return javaSource;
            }
        };

        SimpleJavaFileObject classFile = new SimpleJavaFileObject(URI.create("file:///" + className.replace('.', '/') + ".class"), Kind.CLASS) {
            @Override
            public OutputStream openOutputStream() throws IOException {
                return compiledBytesOutputStream;
            }
        };

        ForwardingJavaFileManager fileManager = new ForwardingJavaFileManager(compiler.getStandardFileManager(null, null, null)) {
            @Override
            public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
                return classFile;
            }
        };

        // compile class
        if (!compiler.getTask(errorWriter, fileManager, null, null, null, Arrays.asList(sourceFile)).call()) {
            throw new Exception(errorWriter.toString());
        }

        // load class
        byte[] bytes = compiledBytesOutputStream.toByteArray();
        return super.defineClass(className, bytes, 0, bytes.length);
    }
}