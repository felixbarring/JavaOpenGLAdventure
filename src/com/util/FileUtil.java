package com.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {
	
	public static FileUtil instance = new FileUtil();

	public String readFromFile(String str) {
		try {
			InputStream input = getClass().getResourceAsStream(str);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
				StringBuilder s = new StringBuilder();
				String l;
				while ((l = reader.readLine()) != null)
					s.append(l).append('\n');
				return s.toString();
			} catch (Exception exc) {
				throw new RuntimeException("Failure reading input stream", exc);
			}
		} catch (Exception exc) {
			throw new RuntimeException("Failure reading file " + str, exc);
		}
	}

}
