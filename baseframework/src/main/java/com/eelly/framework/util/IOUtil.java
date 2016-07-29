package com.eelly.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;

/**
 * IO工具类
 * 
 * @author 李欣
 */
public class IOUtil {

	/**
	 * 缓冲区大小
	 */
	private static final int DEFAULT_BUFFER_SIZE = 4096;

	/**
	 * 默认字符编码
	 */
	private static final String DEFAULT_ENCODE = "utf8";

	/**
	 * 从输入流读取字符串,使用utf8编码
	 * 
	 * @see #toString(InputStream, String)
	 * @param input 输入流
	 * @return
	 * @throws IOException
	 */
	public static String toString(InputStream input) throws IOException {
		return toString(input, DEFAULT_ENCODE);
	}

	/**
	 * 从输入流读取字符串
	 * 
	 * @param inputStream 输入流
	 * @param encode 字符编码
	 * @return
	 * @throws IOException
	 */
	public static String toString(InputStream inputStream, String encode) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, encode), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}

		return sb.toString();
	}

	/**
	 * 拷贝输入流到输出流
	 * @param input 输入流
	 * @param output 输出流
	 * @return 拷贝的字节数
	 * @throws IOException
	 */
	public static long copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * 关闭输入流,不抛出异常
	 * 
	 * @param input 可以为null
	 */
	public static void closeQuietly(InputStream input) {
		if (input == null)
			return;
		try {
			input.close();
		} catch (IOException e) {

		}
	}

	/**
	 * 关闭输出流,不抛出异常
	 * 
	 * @param output 可以为null
	 */
	public static void closeQuietly(OutputStream output) {
		if (output == null)
			return;
		try {
			output.close();
		} catch (IOException e) {

		}
	}
	
	/**
	 * 关闭写入类,不抛出异常
	 * 
	 * @param input 可以为null
	 */
	public static void closeQuietly(Writer writer) {
		if (writer == null)
			return;
		try {
			writer.close();
		} catch (IOException e) {

		}
	}

}
