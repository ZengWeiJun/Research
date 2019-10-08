package com.nius.IO.CharStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

/**
 * 注意：字符流FileReader、FileWriter只能拷贝纯文本
 * @author nius
 *
 */
public class CopyFile {
	public static void main(String[] args) {
		
		long start1 = new Date().getTime();
		copyTextFile1();
		long end1 = new Date().getTime();
		System.out.println((end1 - start1) / 1000.);
		

		long start2 = new Date().getTime();
		copyTextFile2();
		long end2 = new Date().getTime();
		System.out.println((end2 - start2) / 1000.);
	}
	
	public static void copyTextFile1() {
		String path = "./src/com/nius/IO/test";
		File src = new File(path, "test.txt");
		File dest = new File(path, "test123.txt");
		
		FileReader fileReader = null;
		FileWriter fileWriter = null;
		try {
			fileReader = new FileReader(src);
			fileWriter = new FileWriter(dest, false);
			
			char[] chars = new char[100];
			while (-1 != (fileReader.read(chars))) {
				fileWriter.write(chars);
			}
			fileWriter.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileWriter) { fileWriter.close(); }
				if (null != fileReader) { fileReader.close(); }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void copyTextFile2() {
		String path = "./src/com/nius/IO/test";
		File src = new File(path, "test.txt");
		File dest = new File(path, "test123.txt");
		
		BufferedReader fileReader = null;
		BufferedWriter fileWriter = null;
		try {
			fileReader = new BufferedReader(new FileReader(src));
			fileWriter = new BufferedWriter(new FileWriter(dest, true));

			char[] chars = new char[100];
			while (-1 != (fileReader.read(chars))) {
				fileWriter.write(chars);
			}

//			// BufferedReader提供你方法，可以一行一行的读取
//			String line = null;
//			while (null != (line = fileReader.readLine())) {
//				fileWriter.write(line);
//				fileWriter.newLine(); // fileWriter.write("\r\n");
//			}

			fileWriter.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileWriter) { fileWriter.close(); }
				if (null != fileReader) { fileReader.close(); }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
