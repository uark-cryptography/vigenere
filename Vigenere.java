import java.io.*;
import java.util.*;
import java.text.DecimalFormat;


class Vigenere{
	
	ArrayList<String> trigrams = new ArrayList<>();
	ArrayList<String> trigramMatch = new ArrayList<>();
	ArrayList<Integer> trigramPosition = new ArrayList<>();
	
	
	int lowerBound = 4;
	int upperBound = 10;
	int keyLength = 4;
	
	void printTable(int key, float avg, ArrayList<Float> indOC){
		System.out.print(" " + key + "  |     ");
		System.out.printf("%.3f",avg );
		System.out.println("     | " + indOC);
		
	}
	
	float averageIOC(ArrayList<Float> iocArray){
		
		
		float sum = 0;
		if(!iocArray.isEmpty()){
			for(float val : iocArray){
				sum += val;
			}
			return sum/iocArray.size();
		}
		/*
		
		avgMap.put(key, indOC.put);
		
		indOC_array.add(indOC);
		
		float avgSum = 0.0f;
		for(int i = 0; i < indOC_array.size(); i++){
			avgSum += indOC_array.get(i); 
			System.out.println(avgSum);
		}
		float avg = 1.0f;
		avg = avgSum / indOC_array.size();
		
		System.out.println(key);
		*/
		return 0;
	}
	
	float indexOfCoincidence(Map<Character, Integer> charCount, float stringLength, int key){
		
		float indOC = 0;
		float tmp = 0;
		float sum = 0;
		float[] indOCArray = new float[key];		
		//stringLength = 30;
		
		for(int value : charCount.values()){
			sum += (value*(value-1));			
		//System.out.print(value + ", " );
		}
		//System.out.println("sum: " +sum);
		//System.out.println(charCount.values());
		
		tmp = (sum)/(stringLength*(stringLength-1));
		//averageIOC(indOC, key);
		//System.out.printf("indOC: " + "%.4f",indOC);
		//System.out.println();
		
		DecimalFormat df = new DecimalFormat("0.000");
		indOC = Float.parseFloat(df.format(tmp));
		return indOC;
		
	}
	
	//stackoverflow.com/questions/6100712/
	Map<Character, Integer> charCount(String k){
		Map<Character, Integer> charCount = new HashMap<>();
		//k="abirdinhandisworthtwointhebush";
		if (k != null) {
			for (Character c : k.toCharArray()) {
			  Integer count = charCount.get(c);
			  int newCount = (count==null ? 1 : count+1);
			  charCount.put(c, newCount);
			}
		}
		//System.out.println(charCount);	
		return charCount;
	}
	
	//converts each keyLength array into string for shift
	void buildString(String cT){		
			int key = 0;
			float indOC = 0;
			float avg = 0;
			ArrayList<Float> iocArray = new ArrayList<Float>();
		for(int k = lowerBound; k < upperBound; k++){
			
			int stringLength = 0;
			//System.out.println(indices.get(key));
			
			//StringBuilder sb = new StringBuilder();
			//sb.append(indices.get(key));
			
			//String tmp = sb.toString();
			//String str = tmp.replaceAll("[,\\s]","");
			//System.out.println(str);
			String tmpStr = "";
			
			String[] s = new String[k];
			for(int x = 0; x < k; x++){						//Shift string
				for(int i = x; i < cT.length()-k; i+=k){
					tmpStr += cT.charAt(i);
					s[x] = tmpStr;					
				}
				tmpStr = "";
				
				//System.out.println(s[x]);
				//System.out.println("Key: " + k + " String: " + (x+1) );
				stringLength = s[x].length();
				
				indOC = indexOfCoincidence(charCount(s[x]),stringLength, k);
				iocArray.add(indOC);
				
				 
				//indOC = indexOfCoincidence(charCount(s[x]),stringLength, k);
			}
			key++;
			avg = averageIOC(iocArray);
			printTable(k,avg,iocArray);
			iocArray.clear();
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
		//System.out.println(indices.get(0));
		//buildString(indices);
		
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
		
		System.out.println("Key | Average Index | Individiual Indices of Coincidence");
		System.out.println("--------------------------------------------------------");
		
		//v.separateToKeyLength(cipherText);
		v.buildString(cipherText);
		
		
		//System.out.println(cipherText);
		
	}
	
}