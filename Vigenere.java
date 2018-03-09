import java.io.*;
import java.util.*;


class Vigenere{
	
	ArrayList<String> trigrams = new ArrayList<>();
	ArrayList<String> trigramMatch = new ArrayList<>();
	ArrayList<Integer> trigramPosition = new ArrayList<>();
	
	void indexOfIndices(int keyLength){
		
	}
	
	void likelyKeyLength(ArrayList<Integer> primeList){
		int highestCount = -1;
		int highestCountKey = 0;
		for(int i = 0; i < primeList.size()-1; i++){
			int count = 0;
			for(int j = i+1; j < primeList.size(); j++){
				if(primeList.get(i) == primeList.get(j)){
					count++;
				}
				if(count > highestCount){
					highestCount = count;
					highestCountKey = primeList.get(i);
				}
			}
		}
		System.out.println("Kasiski Likely Key Length: " + highestCountKey );
	}
	
	//finds difference in matched position returns prime factors list
	ArrayList<Integer> kasiski(ArrayList<String> trigrams, ArrayList<Integer> trigramPosition){
		ArrayList<Integer> diff = new ArrayList<>();
		ArrayList<Integer> primeList = new ArrayList<>();
		
		//find difference
		for(int i = 0; i < trigramPosition.size()-1; i+=2){
			diff.add(trigramPosition.get(i+1)-trigramPosition.get(i));
		}
		
		//find prime factors
		Collections.sort(diff);				
		for(int i = 0; i < diff.size(); i++){
			int d = diff.get(i);
			if(diff.get(i) < 2){
				System.out.println("No prime factor");
				System.exit(0);
			}else{
				int test = 2;
				while(test <= d){
					if(d%test == 0){
						primeList.add(test);						
						d /= test;
					}else{
						test++;
					}
				}				
			}
		}
		System.out.println(primeList);
		return primeList;
	}
	
	//searches for matches creates list of matches and their positions
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
	}
	
	//Builds list of three char trigrams
	ArrayList<String> trigram(String cipherText){
		String tmp = "";
		for(int i = 0; i < cipherText.length()-2; i++){					
			for(int j = i; j < i+3; j++){				
				tmp += cipherText.charAt(j);
			}
			trigrams.add(tmp);
			tmp = "";	
		}
		return trigrams;
	}
	
	String readFromFile(){
		try {
			BufferedReader cipherReader = new BufferedReader(new FileReader("input_hw.txt"));
			String cipherText = cipherReader.readLine();
			cipherReader.close();
			return cipherText;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public static void main (String args[]){
		
		Vigenere v = new Vigenere();
		String cipherText = v.readFromFile();		
		
		//Build trigrams from cipher text
		ArrayList<String> formattedTrigrams = new ArrayList<>(v.trigram(cipherText));
		
		//compare trigrams for match
		v.findMatch(formattedTrigrams);
		
		//process matching trigrams and position
		ArrayList<Integer> kasiskiTable = new ArrayList<>(v.kasiski(v.trigramMatch, v.trigramPosition));
				
		//find likely Key Length part a
		v.likelyKeyLength(kasiskiTable);
		
	}
	
}