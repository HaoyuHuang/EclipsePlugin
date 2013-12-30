package edu.usc.haoyu.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Haoyu
 *
 */
public class FileUtils {

	private FileUtils() {

	}

	public static File createNewFile(String path, String fileName)
			throws IOException {
		File dirs = new File(path);
		if (!dirs.exists()) {
			dirs.mkdirs();
		}
		File file = new File(path, fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	public static String readFileToString(String fileAbsolutePath)
			throws IOException {
		StringBuilder builder = new StringBuilder();
		File file = new File(fileAbsolutePath);
		if (!file.exists()) {
			throw new IOException("File does not exist!");
		}

		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String temp = "";
		while ((temp = bufferedReader.readLine()) != null) {
			builder.append(temp);
		}
		bufferedReader.close();
		return builder.toString();
	}

	public static String readBinaryFileToString(String fileAbsolutePath)
			throws IOException {
		StringBuilder builder = new StringBuilder();
		File file = new File(fileAbsolutePath);
		if (!file.exists()) {
			throw new IOException("File does not exist!");
		}
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		int temp = -1;
		while ((temp = bis.read()) != -1) {
			builder.append(temp);
		}
		bis.close();
		return builder.toString();
	}

	/**
	 * @param relativePath
	 *            e.g. resources/test.java
	 * @return
	 */
	public static String getAbsolutePath(String relativePath) {
		String projectBinPath = Thread.currentThread().getContextClassLoader()
				.getResource("").getPath();
		String[] subs = projectBinPath.split("/");
		String projectPath = projectBinPath.substring(0,
				projectBinPath.length() - subs[subs.length - 1].length() - 1);
		projectPath += "src/";
		return projectPath + relativePath;
	}

	/**
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	public static void write(File file, String content) throws IOException {
		FileWriter writer = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(writer);
		bw.write(content);
		bw.close();
	}
}
