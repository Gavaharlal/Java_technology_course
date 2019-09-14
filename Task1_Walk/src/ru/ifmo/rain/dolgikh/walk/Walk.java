package ru.ifmo.rain.dolgikh.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Walk {

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] != null || args[1] != null) {
            return;
        }
        try {
            Path inputPath;
            Path outputPath;
            try {
                try {
                    inputPath = Paths.get(args[0]);
                } catch (InvalidPathException e) {
                    throw new WalkException("Input file name is invalid");
                }
                try {
                    outputPath = Paths.get(args[1]);
                } catch (InvalidPathException e) {
                    throw new WalkException("Output file name is invalid");
                }
                try {
                    if (Files.notExists(outputPath)) {
                        if (outputPath.getParent() != null) {
                            Files.createDirectories(outputPath.getParent());
                        }
                    }
                } catch (IOException e) {
                    throw new WalkException("Can't create directories :" + outputPath.getParent().toString());
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new WalkException("Not enough arguments");
            } catch (NullPointerException e){
                throw new WalkException("Empty argument list");
            }

            List<String> stringList;
            try {
                stringList = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
            } catch (FileNotFoundException e) {
                throw new WalkException("No such input file: " + inputPath.getFileName());
            } catch (SecurityException e) {
                throw new WalkException("There is no access to input file: " + inputPath.getFileName());
            } catch (IOException e) {
                throw new WalkException("Error file reading: " + inputPath.getFileName());
            }

            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
                for (String curFileName : stringList) {
                    int hash = 0;
                    try {
                        Path curfile = Paths.get(curFileName);
                        InputStream inputStream = Files.newInputStream(curfile);
                        hash = getHash(inputStream);
                    } catch (InvalidPathException | IOException ignored) {
                    } catch (WalkException e) {
                        e.printMessage();
                    }

                    try {
                        bufferedWriter.write(String.format("%08x", hash) + " " + curFileName);
                        bufferedWriter.newLine();
                    } catch (IOException e) {
                        System.out.println("Can't write");
                    }
                }
            } catch (FileNotFoundException e) {
                throw new WalkException("Output file doesn't exists: " + outputPath.getFileName());
            } catch (SecurityException e) {
                throw new WalkException("There is no access to output file: " + outputPath.getFileName());
            } catch (UnsupportedEncodingException e) {
                throw new WalkException("Unsupported character encoding in file: " + outputPath.getFileName());
            } catch (IOException e) {
                throw new WalkException("Error writing to file: " + outputPath.getFileName());
            }
        } catch (WalkException e) {
            e.printMessage();
        }
    }


    private static int getHash(InputStream reader) throws WalkException {
        int hash = 0x811c9dc5;
        byte[] buff = new byte[1024];
        int c;
        try {
            while ((c = reader.read(buff)) >= 0) {
                for (int i = 0; i < c; i++) {
                    hash = (hash * 0x01000193) ^ (buff[i] & 0xff);
                }
            }
        } catch (IOException e) {
            throw new WalkException("Error reading");
        }

        return hash;
    }

}
