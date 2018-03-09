import java.io.*;
import java.util.*;


class Vigenere{
	
	ArrayList<String> trigrams = new ArrayList<>();
	ArrayList<String> trigramMatch = new ArrayList<>();
	ArrayList<Integer> trigramPosition = new ArrayList<>();
	
	
	int lowerBound = 4;
	int upperBound = 10;
	int keyLength = 4;
	
	//stackoverflow.com/questions/6100712/
	Map<Character, Integer> letterCount(String k){
		Map<Character, Integer> charCount = new HashMap<>();
		int counter = 0;
		if (k != null) {
			for (Character c : k.toCharArray()) {
			  Integer count = charCount.get(c);
			  int newCount = (count==null ? 1 : count+1);
			  charCount.put(c, newCount);
			}
		}
		return charCount;
	}
	
	//converts each keyLength array into string for shift
	void buildString(List<List<String>> indices){		
		for(int k = lowerBound; k < upperBound; k++){
			
			StringBuilder sb = new StringBuilder();
			for(String s : indices.get(k-4)){
				sb.append(s);
			}
			String str = sb.toString();
			String tmpStr = "";
			
			String[] s = new String[k];
			for(int x = 0; x < k; x++){						//Shift string
				for(int i = 0; i < str.length(); i+=k){
					tmpStr += str.charAt(i+x);
					s[x] = tmpStr;					
				}
				tmpStr = "";
				System.out.println(s[x]);
				letterCount(s[x]);
				System.out.println();
			}
			
			//System.out.println(indices.get(k-4));
		}
		
	}
	
	
	//An array holding arrays of keyLength 4 through 9
	void separateToKeyLength(String cT){
		List<List<String>> indices = new ArrayList<>();
		ArrayList<String> tmpList = new ArrayList<>();
		String tmp = "";
		for(int key = lowerBound; key < upperBound; key++){
			tmpList = new ArrayList<String>(tmpList);
			for(int i = 0; i < cT.length()-(key-1); i++){					
				for(int j = i; j < i + keyLength; j++){				
					tmp += cT.charAt(j);
				}
				tmpList.add(tmp);
				tmp = "";								
			}			
				keyLength++;
				indices.add(new ArrayList<String>(tmpList));
				tmpList.clear();
		}
		buildString(indices);
		
		//System.out.println(indices);
		//System.out.println();
		
	}
	
	void likelyKeyLength(ArrayList<Integer> primeList){
		int highestCount = -1;
		int highestCountKey = 0;
		for(int i = 0; i < primeList.size()-1; i++){
			int count = 1;
			for(int j = i+1; j < primeList.size(); j++){
				if(primeList.get(i) == primeList.get(j) && primeList.get(i) > 2){
					count++;
				}
				if(count > highestCount){
					highestCount = count;
					highestCountKey = primeList.get(i);
				}
			}
		}
		System.out.println("Kasiski Likely Key Length: " + highestCountKey);
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
		//System.out.println(primeList);
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
			BufferedReader cipherReader = new BufferedReader(new FileReader("input_ex.txt"));
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
		System.out.println();
		
		v.separateToKeyLength(cipherText);
		
		//System.out.println(cipherText);
		
	}
	
}