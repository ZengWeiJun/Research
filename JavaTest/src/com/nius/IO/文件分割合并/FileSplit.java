package com.nius.IO.文件分割合并;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.IIOException;

import com.nius.IO.bjsxt.io.others.SplitFile;

public class FileSplit {
	public static void main(String[] args) {
		
		try {
			System.out.println("----------------------------");
			// 分割文本（不包含中文）
			split("./src/com/nius/IO/test/test-----.txt", 
					"./src/com/nius/IO/test/test-----", 
					50);
			
			// 分割图片
			split("./src/com/nius/IO/test/33333333.jpg", 
					"./src/com/nius/IO/test/33333333", 
					1024 * 8);
			System.out.println("----------------------------");
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	/**
	 * 文件分割(目前仅适用于分割非中文字符，因为中文字符2个字符)
	 * 中文：https://blog.csdn.net/caiwencongwyj/article/details/45226339
	 * @param srcPath 源文件路径
	 * @param destPath 目标问价目录
	 * @param blockSize 大小bytes
	 * @throws IOException 
	 */
	public static void split(String srcPath, String destDir, long blockSize) 
			throws NullPointerException,IOException {
		if (null == srcPath) { 
			throw new NullPointerException("文件路径不能为空"); 
		}
		
		File src = new File(srcPath);
		if (!src.exists() || !src.isFile()) {
			throw new IIOException("只能分割文件");
		}

		// 获取文件名
		String filename = src.getName();
		String filenamePrefix = "";
		String filenameSuffix = "";
		if (filename.contains(".")) {
			// . 点属于特殊字符 . $、|、(、)、[、{、^、?、*、+、\\，需要使用\\转义
			String[] filenameComps = filename.split("\\.");
			if (filenameComps.length == 2) {
				filenamePrefix = filenameComps[0];
				filenameSuffix = filenameComps[1];
			}
		}
		
		long totalSize = src.length();
		if (blockSize > totalSize || blockSize <= 0) {
			blockSize = totalSize; 
		}

		int tileCount = (int)Math.ceil((((double)totalSize) / blockSize));
		
		long pos = 0;
		for (int i = 0; i < tileCount; i++) {
			// 访问和插入文件任意位置，使用RandomAccessFile
			// r : 表示RandomAccessFile具有读取功能
			RandomAccessFile raf = new RandomAccessFile(src, "r");
			
			// 设置指针
			raf.seek(pos);
			
			// 这里使用FileOutputStream写入文件，也可以使用RandomAccessFile
			File destD = new File(destDir);
			if (!destD.exists()) destD.mkdirs();
			File dest = new File(destD, filenamePrefix + "_part" + i + "." + filenameSuffix); 
			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(dest, true));

			long shouldReadSize = blockSize;
			int len = 0;
			byte[] bytes = new byte[100];
			while (-1 != (len = raf.read(bytes))) {
				if (shouldReadSize > len) { // 应该读取的长度大于读取到的长度
					os.write(bytes, 0, len);
					shouldReadSize -= len;
				} else { // 应该读取的长度小于读取到的长度，只写入需要的部分
					os.write(bytes, 0, (int)shouldReadSize);
					break;
				}
			}
			os.flush();
			
			pos += blockSize;
			if (i == tileCount - 1) {
				blockSize = totalSize - pos;
			}
		}
	}
}

