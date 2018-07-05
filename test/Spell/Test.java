package Spell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Test {

	public static void main(String[] args) {
		LCSMap map = new LCSMap();
		
		File f = new File("/path/to/log-file");
		FileReader fr;
		BufferedReader br;
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			
			String line;
			while((line = br.readLine()) != null) {
				map.insert(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		p("Test Results:");
		p(map.toString());
	}
	
	public static void p(String s) {
		System.out.println(s);
	}
}
