package com.pls.documentmanagement.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Utilities for files processing.
 *
 * @author Denis Zhupinsky (Team International)
 */
public final class FileOperationsUtils {
    private FileOperationsUtils() {
    }

    /**
     * Writes the Load document to the file system as a pdf document.
     *
     * @param path file path where document need to be stored
     * @param fileName file name of document
     * @param content content to save to file
     *
     * @throws IOException
     *             thrown for any exception while writing the document to file system.
     */
    public static void writeToFileSystem(String path, String fileName, byte[] content) throws IOException {
        FileUtils.forceMkdir(new File(path));

        FileUtils.writeByteArrayToFile(new File(path, fileName), content);
    }

    /**
     * Writes the Load document to the file system as a pdf document.
     *
     * @param path file path where document need to be stored
     * @param fileName file name of document
     * @param inputStream input stream that need to be copied as content of file to save
     *
     * @throws IOException
     *             thrown for any exception while writing the document to file system.
     */
    public static void writeToFileSystem(String path, String fileName, InputStream inputStream) throws IOException {
        FileUtils.forceMkdir(new File(path));
        IOUtils.copy(inputStream, new FileOutputStream(new File(path, fileName)));
    }

    /**
     * Writes the Load document to the file system as a pdf document.
     *
     * @param srcFile file to move from
     * @param destFile file to move to
     *
     * @throws IOException
     *             thrown for any exception while writing the document to file system.
     */
    public static void moveFile(File srcFile, File destFile) throws IOException {
        FileUtils.forceMkdir(destFile.getParentFile());
        File destDir = destFile.getParentFile();
        if (srcFile == null) {
            throw new IOException("Source must not be null");
        }
        if (destDir == null) {
            throw new IOException("Destination directory must not be null");
        }
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        if (!destDir.exists()) {
            throw new FileNotFoundException("Destination directory '" + destDir + "' does not exist ");
        }
        if (!destDir.isDirectory()) {
            throw new IOException("Destination '" + destDir + "' is not a directory");
        }
        FileUtils.moveFile(srcFile, new File(destDir, destFile.getName()));

    }
}
