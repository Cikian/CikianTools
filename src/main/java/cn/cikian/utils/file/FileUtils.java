package cn.cikian.utils.file;


import cn.cikian.code.ErrorCode;
import cn.cikian.exception.CikException;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Cikian
 * @version 1.0
 * @since 2025/4/9 08:48
 */
public class FileUtils {

    /**
     * 将输入流写入文件，需要指定绝对路径，并且路径中需要包含文件名
     *
     * @param inputStream  输入流
     * @param absolutePath 绝对路径
     * @throws IOException IO异常
     */
    public static void writeStreamToFile(InputStream inputStream, String absolutePath) throws FileAlreadyExistsException {
        try {
            inputStream.mark(0);
            inputStream.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Path path = Paths.get(absolutePath);
        // 检查路径是否为文件夹
        if (path.getFileName() == null || Files.isDirectory(path)) {
            throw new CikException(ErrorCode.FAIL.code(), "路径必须包含有效文件名");
        }
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new CikException(ErrorCode.FAIL.code(), "创建目录失败: " + path.getParent(), e);
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(path));
             BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                System.out.printf("\r\u001B[32mWriting... %.1f%%\u001B[0m", (bis.available() / (double)inputStream.available()) * 100);
            }
            System.out.println("\n\u001B[32mWrite completed successfully!\u001B[0m");
        } catch (IOException e) {
            throw new CikException(ErrorCode.FAIL.code(), "I/O异常", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * 将文件压缩为zip格式，并返回输入流
     *
     * @param absolutePath 文件或文件夹绝对路径
     * @return 压缩后的zip文件输入流
     * @throws IOException IO异常
     */
    public static InputStream compressToZipStream(String absolutePath) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            compress(absolutePath, zos);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * 将文件压缩为zip格式，并返回文件绝对路径，默认输出到用户临时文件夹
     *
     * @param absolutePath 文件或文件夹绝对路径
     * @return 压缩后的zip文件绝对路径
     * @throws IOException IO异常
     */
    public static String compressToZipFile(String absolutePath) throws IOException {
        Path tempFile = Files.createTempFile("CikToolsCompressed-", ".zip");
        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            compress(absolutePath, zos);
            return tempFile.toAbsolutePath().toString();
        } catch (Exception e) {
            Files.deleteIfExists(tempFile);
            if (e instanceof UncheckedIOException) {
                throw ((UncheckedIOException) e).getCause();
            }
            throw e;
        }
    }


    /**
     * 将文件压缩为zip格式<br>
     * 传入绝对路径，若路径中包含文件名，则将文件输出到指定路径的指定文件名<br>
     * 若传入路径为文件夹，则将文件输出到指定文件夹，并使用默认命名<br>
     * 并返回文件绝对路径
     *
     * @param absolutePath 文件或文件夹绝对路径
     * @param outputPath   输出路径
     * @return 压缩后的zip文件绝对路径
     * @throws IOException IO异常
     */
    public static String compressToZipFile(String absolutePath, String outputPath) throws IOException {
        Path output = Paths.get(outputPath);

        // 处理路径分隔符
        String normalizedPath = outputPath.replace('\\', '/');
        boolean isZipFile = normalizedPath.toLowerCase().endsWith(".zip");

        Path finalOutputPath;
        if (isZipFile) {
            finalOutputPath = output;
        } else {
            finalOutputPath = output.resolve("CikToolsCompressed-" + System.currentTimeMillis() + ".zip");
        }

        // 创建父目录
        Files.createDirectories(finalOutputPath.getParent());

        // 检查文件是否存在
        if (Files.exists(finalOutputPath)) {
            throw new FileAlreadyExistsException(finalOutputPath.toString());
        }

        try (FileOutputStream fos = new FileOutputStream(finalOutputPath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Path sourcePath = Paths.get(absolutePath);
            Files.walk(sourcePath)
                    .forEach(path -> {
                        try {
                            String entryName = sourcePath.relativize(path).toString().replace('\\', '/');
                            if (entryName.isEmpty()) return;

                            zos.putNextEntry(new ZipEntry(entryName + (Files.isDirectory(path) ? "/" : "")));
                            if (!Files.isDirectory(path)) {
                                Files.copy(path, zos);
                            }
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            return finalOutputPath.toAbsolutePath().toString();
        } catch (Exception e) {
            Files.deleteIfExists(finalOutputPath);
            if (e instanceof UncheckedIOException) {
                throw ((UncheckedIOException) e).getCause();
            }
            throw e;
        }
    }

    /**
     * 删除指定路径的文件或目录（包括非空目录）
     * @param path 要删除的文件/目录路径
     * @throws IllegalArgumentException 如果路径不存在
     */
    public static void deleteFileOrDir(String path) {
        File target = new File(path);

        if (!target.exists()) {
            throw new IllegalArgumentException("路径不存在: " + path);
        }

        if (target.isFile()) {
            if (!target.delete()) {
                throw new RuntimeException("文件删除失败: " + path);
            }
        } else {
            deleteDirectory(target);
        }
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file); // 递归删除子目录
                } else {
                    if (!file.delete()) {
                        throw new RuntimeException("文件删除失败: " + file.getAbsolutePath());
                    }
                }
            }
        }
        if (!directory.delete()) {
            throw new RuntimeException("目录删除失败: " + directory.getAbsolutePath());
        }
    }

    private static void compress(String absolutePath, ZipOutputStream zos) throws IOException {
        Path sourcePath = Paths.get(absolutePath);

        Files.walk(sourcePath)
                .forEach(path -> {
                    try {
                        String entryName = sourcePath.relativize(path).toString().replace('\\', '/');
                        if (entryName.isEmpty()) return;

                        zos.putNextEntry(new ZipEntry(entryName + (Files.isDirectory(path) ? "/" : "")));
                        if (!Files.isDirectory(path)) {
                            Files.copy(path, zos);
                            System.out.printf("\r\u001B[32mCompressing... %s\u001B[0m", entryName);
                        }
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        System.out.println("\n\u001B[32mCompression completed successfully!\u001B[0m");
    }
}