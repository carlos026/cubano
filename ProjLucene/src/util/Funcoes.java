package util;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Funcoes {
	
	public Funcoes() {
		
	}

	public ArrayList<String> getStopWords() throws IOException {
		
		Scanner s = new Scanner(new FileReader("/Users/Carlos/Documents/Fucapi/Topicos de banco de dados/stopwords.txt"));
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNext()){
		    list.add(s.next());
		}
		s.close();
	    return list;
	}
	
	
	
}
