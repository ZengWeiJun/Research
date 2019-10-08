package com.nius.IO.CharStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class ReadFromFile {
	public static void main(String[] args) {
		readFile();
	}
	
	public static void readFile() {
		// 关联文件
		String parent = "./src/com/nius/IO/test";
		File f1 = new File(parent, "test.txt");
		
		// 字符流使用Reader
		Reader reader = null;
		try {
			reader = new FileReader(f1);
			
			// 创建缓冲区
			StringBuilder sb = new StringBuilder();
			char[] chars = new char[1024];
			int len = 0;
			while (-1 != (len = reader.read(chars))) {
				String str = new String(chars, 0, len);
				sb.append(new String(str.getBytes(), "utf-8"));
			}
			System.out.println(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
