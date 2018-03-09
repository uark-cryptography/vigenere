import java.io.*;
import java.util.*;


class Vigenere{
	
	ArrayList<String> trigrams = new ArrayList<>();
	ArrayList<String> trigramMatch = new ArrayList<>();
	ArrayList<Integer> trigramPosition = new ArrayList<>();
	
	void print(String s){
		System.out.println(s);
	}
	
	void likelyKeyLength(ArrayList<Integer> primeList){
		int highestCount = -1;
		int highestCountKey = 0;
		int count = 0;
		for(int i = 0; i < primeList.size(); i++){
			for(int j = i+1; j < primeList.size() -1; j++){
				if(primeList.get(i) == primeList.get(j)){
					count++;
				}
				if(count > highestCount){
					highestCount = count;
					highestCountKey = primeList.get(i);
				}
			}
		}
		System.out.println("Kasiski Likely Key: " + highestCountKey );
	}
	
	void primeFactor(ArrayList<Integer> diff){
		
		ArrayList<Integer> primeListTmp = new ArrayList<>();
		for(int i = 0; i < diff.size(); i++){
			int d = diff.get(i);
			if(diff.get(i) < 2){
				System.out.println("No prime factor");
				System.exit(0);
			}else{
				int test = 2;
				while(test <= d){
					if(d%test == 0){
						primeListTmp.add(test);						
						d /= test;
					}else{
						test++;
					}
				}				
			}
		}
		//System.out.println(primeListTmp);
		likelyKeyLength(primeListTmp);
	}
	
	void kasiski(ArrayList<String> trigrams, ArrayList<Integer> trigramPosition){
		ArrayList<Integer> difference = new ArrayList<>();
		for(int i = 0; i < trigramPosition.size()-1; i+=2){
			difference.add(trigramPosition.get(i+1)-trigramPosition.get(i));
		}
		Collections.sort(difference);
		//System.out.println("difference: " + difference);
		primeFactor(difference);
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
		System.out.println("Trigram: " + trigramMatch);
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
		findMatch(trigrams);
	}
	
	public static void main (String args[]){
		String cipherText = "";
		
		try {
			BufferedReader cipherReader = new BufferedReader(new FileReader("input_hw.txt"));
			cipherText = cipherReader.readLine();
			cipherReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} 
		
		Vigenere v = new Vigenere();
		v.trigram(cipherText);
		
	}
	
}