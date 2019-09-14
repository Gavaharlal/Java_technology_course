package ru.ifmo.rain.dolgikh.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

/**
 * {@link JarImpler} interface implementation
 */

public class Implementor implements JarImpler {

    /**
     * Suffix for constructed class.
     */
    private final String SUFFIX = "Impl";

    /**
     * Java-file extension
     */
    private final String JAVAEXT = ".java";

    /**
     * Space for separating.
     */
    private final String SPACE = " ";

    /**
     * Comma for separating.
     */
    private final String COMMA = ",";

    /**
     * Line separator.
     */
    private final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Returns <code>tabsNum</code> number of tabs.
     *
     * @param tabsNum number of tabs to return.
     * @return {@link String} consisting of <code>tabsNum</code> number of tabs.
     */
    private String getTabs(int tabsNum) {
        String tab = "    ";
        return Stream.generate(() -> tab).limit(tabsNum).collect(Collectors.joining());
    }

    /**
     * Concatenates class name with "Impl" suffix.
     *
     * @param classToken class token to concatenate.
     * @return {@link String} consisting of concatenation.
     */
    private String toClassnameImpl(Class<?> classToken) {
        return classToken.getSimpleName() + SUFFIX;
    }

    /**
     * Returns default value of given type token.
     *
     * @param token type to get default value.
     * @return {@link String} string representation of default value.
     */
    private String getDefaultValue(Class<?> token) {
        if (token.equals(void.class)) {
            return "";
        } else if (token.equals(boolean.class)) {
            return " false";
        } else if (token.isPrimitive()) {
            return " 0";
        } else {
            return " null";
        }
    }

    /**
     * Generates class file implementing <code>inputToken</code> interface.
     *
     * @param inputToken type token for creating implementation.
     * @param root       root directory.
     * @throws ImplerException if some errors occurred
     */
    @Override
    public void implement(Class<?> inputToken, Path root) throws ImplerException {

        if (Objects.isNull(inputToken) || Objects.isNull(root)) {
            throw new ImplerException("Expected non-null arguments");
        }

        if (!inputToken.isInterface()) {
            throw new ImplerException("Invalid interface inputToken");
        }

        Path resolvedPath = expandPath(root, inputToken, JAVAEXT);

        if (resolvedPath.getParent() != null) {
            try {
                Files.createDirectories(resolvedPath.getParent());
            } catch (IOException e) {
                throw new ImplerException("Error creating directories for output file");
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(resolvedPath)) {
            writer.write(toUnicode(getClassDeclaration(inputToken)));
            for (Method method : inputToken.getMethods()) {
                writer.write(toUnicode(getMethodCode(method)));
            }
            writer.write(toUnicode("}" + LINE_SEPARATOR));
        } catch (IOException e) {
            throw new ImplerException("Error writing to output file");
        }
    }

    /**
     * Creates new instance of class {@link Implementor}
     */
    public Implementor() {
    }

    /**
     * Converts given {@link String} to unicode-friendly
     *
     * @param in input {@link String}
     * @return representation
     */
    private String toUnicode(String in) {
        return in.chars()
                .mapToObj(value -> String.format("\\u%04X", value))
                .collect(Collectors.joining());
    }


    /**
     * Generates {@link String} containing concatenation of class package, class name and implemented interface
     *
     * @param interfaceToken interface which implementation is generating
     * @return {@link String} containing beginning of class declaration
     */
    private String getClassDeclaration(Class<?> interfaceToken) {

        StringBuilder result = new StringBuilder();

        if (!interfaceToken.getPackage().getName().isEmpty()) {
            result.append("package").append(SPACE)
                    .append(interfaceToken.getPackage().getName()).append(";")
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR);
        }

        result.append("public class ")
                .append(toClassnameImpl(interfaceToken)).append(SPACE)
                .append("implements").append(SPACE)
                .append(interfaceToken.getSimpleName()).append(SPACE)
                .append("{")
                .append(LINE_SEPARATOR);

        return result.toString();
    }


    /**
     * Generates {@link String} containing bracketed list of given {@link Method} parameters with their types
     *
     * @param method which list parameters to generate
     * @return {@link String} containing parameters
     */
    private String getParams(Method method) {
        return Arrays.stream(method.getParameters())
                .map(parameter -> parameter.getType().getCanonicalName() + SPACE + parameter.getName())
                .collect(Collectors.joining(COMMA + SPACE, "(", ")"));
    }

    /**
     * Generates {@link String} containing list of exceptions which can be thrown by given {@link Method}
     *
     * @param method to get exceptions from
     * @return {@link String} containing list of exceptions
     */
    private String getExceptions(Method method) {
        StringBuilder result = new StringBuilder();
        Class[] exceptions = method.getExceptionTypes();
        if (exceptions.length > 0) {
            result.append("throws" + SPACE);

            result.append(Arrays.stream(exceptions)
                    .map(Class::getCanonicalName)
                    .collect(Collectors.joining(COMMA + SPACE))
            );
        }

        return result.toString();
    }

    /**
     * Returns return type and name of given {@link Method}
     *
     * @param method to get result
     * @return {@link String} containing such return type and name
     */
    private String getReturnTypeAndName(Method method) {
        return method.getReturnType().getCanonicalName() + SPACE + method.getName();
    }

    /**
     * Returns fully constructed {@link Method}, that returns default value of given {@link Method} return type
     *
     * @param method to get result
     * @return {@link String} containing code of given {@link Method}
     */
    private String getMethodCode(Method method) {
        StringBuilder res = new StringBuilder(getTabs(1));
        int modifiers = method.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT;

        res.append(Modifier.toString(modifiers))
                .append(modifiers != 0 ? SPACE : "")
                .append(getReturnTypeAndName(method))
                .append(getParams(method))
                .append(SPACE)
                .append(getExceptions(method))
                .append(SPACE)
                .append("{")
                .append(LINE_SEPARATOR)
                .append(getTabs(2))
                .append("return").append(getDefaultValue(method.getReturnType())).append(";")
                .append(LINE_SEPARATOR)
                .append(getTabs(1))
                .append("}")
                .append(LINE_SEPARATOR);
        return res.toString();
    }


    /**
     * Generates class file implementing <code>inputToken</code> interface.
     * Generated class is wrapped in jar.
     *
     * @param inputToken type token for creating implementation.
     * @param root       root directory.
     * @throws ImplerException if some errors occurred
     */
    @Override
    public void implementJar(Class<?> inputToken, Path root) throws ImplerException {

        Path tempDir = root.resolve(root.toAbsolutePath().getParent()).resolve("tmp");
        implement(inputToken, tempDir);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        String[] args = new String[]{
                "-cp",
                tempDir.toString() + File.pathSeparator + System.getProperty("java.class.path"),
                expandPath(tempDir, inputToken, JAVAEXT).toString()
        };
        if (compiler == null || compiler.run(null, null, null, args) != 0) {
            throw new ImplerException("Can't get java compiler");
        }

        try (JarOutputStream writer = new JarOutputStream(Files.newOutputStream(root), new Manifest())) {
            String CLASSEXT = ".class";
            writer.putNextEntry(new ZipEntry(inputToken.getName().replace('.', '/') + SUFFIX + CLASSEXT));
            Files.copy(expandPath(tempDir, inputToken, CLASSEXT), writer);
        } catch (IOException e) {
            throw new ImplerException("Error writing to JAR file: " + e);
        }
    }

    /**
     * Adds <code>token</code> and <code>end</code> as suffix to given <code>path</code>
     *
     * @param path  source directory
     * @param token class which full name adding to <code>path</code>
     * @param end   suffix of the generated path
     * @return {@link Path} representing path to certain file
     */
    private Path expandPath(Path path, Class<?> token, String end) {
        return path.resolve(token.getPackage().getName().replace('.', File.separatorChar))
                .resolve(token.getSimpleName() + SUFFIX + end);
    }

    /**
     * This is method <code>main</code>. Used for executing {@link Implementor#implementJar(Class, Path)}
     *
     * @param args arguments for executing
     */
    public static void main(String[] args) {
        try {
            if (args == null || (args.length < 2)) {
                throw new ImplerException("Two arguments expected");
            }
            if (args[0] == null || args[1] == null) {
                throw new ImplerException("All arguments must be non-null");
            }
            JarImpler implementor = new Implementor();
            if (args.length == 2) {
                implementor.implement(Class.forName(args[0]), Paths.get(args[1]));
            } else {
                implementor.implementJar(Class.forName(args[0]), Paths.get(args[1]));
            }
            System.out.println("Successfully done");
        } catch (InvalidPathException e) {
            System.err.println("Invalid root path");
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid class name: " + e);
        } catch (ImplerException e) {
            System.err.println(e.getMessage());
        }
    }
}