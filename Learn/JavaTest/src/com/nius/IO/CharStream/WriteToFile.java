package com.nius.IO.CharStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteToFile {
	
	public static void main(String[] args) {
		String parent = "./src/com/nius/IO/test";
		File f1 = new File(parent, "test.txt");
		
		String str = "\r\n这不是加需求 这个改了一堆bug[捂脸][捂脸][捂脸]😱😱😱😱 \r\n- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController {\n" + 
				"    [(YJRTabBar *)self.tabBar pubBtnView].hidden = (self.selectedIndex != 2);\n" + 
				"}";
		FileWriter wr = null;
		try {
			wr = new FileWriter(f1, true);
			wr.write(str);
			//wr.write(str, 0, str.length());
			
			// 强制冲刷管道
			// 将字符流强制冲刷到文件中
			wr.flush(); 
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != wr) {
				try {
					wr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
