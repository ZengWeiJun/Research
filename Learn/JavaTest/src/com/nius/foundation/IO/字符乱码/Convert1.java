package com.nius.foundation.IO.字符乱码;

import java.io.UnsupportedEncodingException;

public class Convert1 {
	
	/// 字符串的编码解码方式必须保持一致，否则出现乱码
	/// 编码： 字符串 ---> 二进制码
	/// 解码： 二进制码 ---> 字符串
	public static void main(String[] args) {
		String str = "hello 你是谁？";
		System.out.println(str);
		
		byte[] bytes1 = str.getBytes();
		String str1 = new String(bytes1);
		System.out.println(str1);
		
		byte[] bytes2;
		try {
			bytes2 = str.getBytes("gbk");
			String str2 = new String(bytes2, "gbk");
			System.out.println(str2);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		byte[] bytes3;
		try {
			bytes3 = str.getBytes("gb2312");
			String str3 = new String(bytes3, "gb2312");
			System.out.println(str3);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		byte[] bytes4;
		try {
			bytes4 = str.getBytes("utf-8");
			String str4 = new String(bytes4, "utf-8");
			System.out.println(str4);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
