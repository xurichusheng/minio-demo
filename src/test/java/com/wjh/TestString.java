package com.wjh;

import org.junit.Test;

/**
 * @author wenjianhai
 * @date 2021年7月16日
 * @since JDK 11
 */
public class TestString {

	@Test
	public void getFileNameFormUrl() {
		try {
			String filenamerel = "http://127.0.0.1:9000/miniobuckets/16424990054531451.jpeg";

			int lastIdx = filenamerel.lastIndexOf("/");

			String fileName = null;

			if (lastIdx >= 0) {
				fileName = filenamerel.substring(lastIdx + 1, filenamerel.length());
			}
			System.out.println("fileName=" + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
