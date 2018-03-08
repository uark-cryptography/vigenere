import java.io.*;
import java.util.*;


class Vigenere{
	
	ArrayList<String> trigrams = new ArrayList<>();
	ArrayList<String> trigramMatch = new ArrayList<>();
	ArrayList<Integer> trigramPosition = new ArrayList<>();
	
	void print(String s){
		System.out.println(s);
	}
	
	void kasiski(ArrayList<String> trigrams, ArrayList<Integer> trigramPosition){
		ArrayList<Integer> difference = new ArrayList<>();
		for(int i = 0; i < trigramPosition.size()-1; i+=2){
			difference.add(trigramPosition.get(i+1)-trigramPosition.get(i));
		}
		System.out.println("difference: " + difference);
	}
	
	void findMatch(ArrayList<String> trigrams){
		for(int i = 0; i < trigrams.size(); i++){
			for(int j = i+1; j < trigrams.size(); j++){
				if(trigrams.get(i).equals( trigrams.get(j))){
					trigramMatch.add(trigrams.get(i));
					trigramPosition.add(i+1);
					trigramPosition.add(j+1);
				}
			}
			
		}
		System.out.println("matches: " + trigramMatch);
		System.out.println("position: " + trigramPosition);
		kasiski(trigramMatch, trigramPosition);
	}
	
	void trigram(String cT){
		String t = "";
		for(int i = 0; i < cT.length()-2; i++){					
			for(int j = i; j < i+3; j++){				
				t += cT.charAt(j);
			}
			trigrams.add(t);
			t = "";	
		}
		System.out.println(trigrams);
		findMatch(trigrams);
	}
	
	public static void main (String args[]){
		String cipherText = "";
		
		try {
			BufferedReader cipherReader = new BufferedReader(new FileReader("input.txt"));
			cipherText = cipherReader.readLine();
			cipherReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} 
		
		Vigenere v = new Vigenere();
		v.trigram(cipherText);
		v.print(cipherText);
		
	}
	
}