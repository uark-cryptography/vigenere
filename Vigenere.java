import java.io.*;
import java.util.*;
import java.lang.Character;
import java.text.DecimalFormat;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.MathContext;


class Vigenere{
    
    // PART A
    
    // Searches for matches creates list of matches and their positions
    private static SimpleImmutableEntry<ArrayList<String>, ArrayList<Integer>>
    findTrigramMatches(
        ArrayList<String> trigrams
    ) {
        ArrayList<String> trigramMatch = new ArrayList<>();
        ArrayList<Integer> trigramPosition = new ArrayList<>();

        for(int i = 0; i < trigrams.size(); i++){
            for(int j = i+1; j < trigrams.size(); j++){
                if(trigrams.get(i).equals( trigrams.get(j))){
                    trigramMatch.add(trigrams.get(i));
                    trigramPosition.add(i+1);
                    trigramPosition.add(j+1);
                }
            }           
        }

        return new SimpleImmutableEntry<ArrayList<String>, ArrayList<Integer>>(
            trigramMatch,
            trigramPosition
        );
    }
    
    // Builds list of three char trigrams
    private static ArrayList<String> findTriagrams(String cipherText){
        ArrayList<String> trigrams = new ArrayList<>();
        String tmp = "";
        for(int i = 0; i < cipherText.length() - 2; i++){                 
            for(int j = i; j < i + 3; j++){               
                tmp += cipherText.charAt(j);
            }
            trigrams.add(tmp);
            tmp = "";   
        }
        return trigrams;
    }
    
    // Probable key from kasiski table
    private static int likelyKeyLength(ArrayList<Integer> primeList){
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
        return highestCountKey;
    }
    
    // Finds difference in matched position returns prime factors list
    private static ArrayList<Integer> getKasiskiTable(
        ArrayList<String> trigrams,
        ArrayList<Integer> trigramPosition
    ) {
        ArrayList<Integer> diff = new ArrayList<>();
        ArrayList<Integer> primeList = new ArrayList<>();
        
        // Find difference
        for(int i = 0; i < trigramPosition.size()-1; i+=2){
            diff.add(trigramPosition.get(i+1)-trigramPosition.get(i));
        }
        
        // Find prime factors
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
        return primeList;
    }
    // Part B

    static int lowerBound = 4;
    static int upperBound = 10;
    static int keyLength = 4;
    
    // Print table
    private static void printTable(int key, float avg, ArrayList<Float> indOC) {
        System.out.print(" " + key + "  |     ");
        System.out.printf("%.3f",avg );
        System.out.println("     | " + indOC);      
    }
    
    // Returns average index
    private static float averageIOC(ArrayList<Float> iocArray){        
        float sum = 0;
        if(!iocArray.isEmpty()){
            for(float val : iocArray){
                sum += val;
            }
            return sum/iocArray.size();
        }
        return 0;
    }
    
    private static float indexOfCoincidence(
        Map<Character, Integer> charCount,
        float stringLength,
        int key
    ) {     
        float indOC = 0;
        float tmp = 0;
        float sum = 0;
        float[] indOCArray = new float[key];    
        
        for(int value : charCount.values()) {
            sum += (value*(value-1));       
        }
        
        tmp =  sum / (stringLength * (stringLength - 1));
        
        DecimalFormat df = new DecimalFormat("0.000");
        indOC = Float.parseFloat(df.format(tmp));
        return indOC;       
    }
    
    // From stackoverflow.com/questions/6100712/
    private static Map<Character, Integer> charCount(String k) {
        Map<Character, Integer> charCount = new HashMap<>();
        if (k != null) {
            for (Character c : k.toCharArray()) {
              Integer count = charCount.get(c);
              int newCount = (count==null ? 1 : count+1);
              charCount.put(c, newCount);
            }
        }
        return charCount;
    }
    
    // Converts each keyLength array into string for shift
    private static int buildString(String cT) {
        int key = 0;
        float indOC = 0;
        float avg = 0;
        float maxAvg = -1;
        int maxKey = -1;
        ArrayList<Float> iocArray = new ArrayList<Float>();

        for(int k = lowerBound; k < upperBound; k++){
            
            int stringLength = 0;
            String tmpStr = "";
            
            String[] s = new String[k];
            for(int x = 0; x < k; x++){                     //Shift string
                for(int i = x; i < cT.length()-k; i+=k){
                    tmpStr += cT.charAt(i);
                    s[x] = tmpStr;                  
                }
                tmpStr = "";
                
                stringLength = s[x].length();               
                indOC = indexOfCoincidence(charCount(s[x]),stringLength, k);
                iocArray.add(indOC);                
            }
            key++;
            avg = averageIOC(iocArray);
            printTable(k,avg,iocArray);
            iocArray.clear();
            
            if(avg > maxAvg){
                maxAvg = avg;
                maxKey = k;
            }
        }
        return maxKey;
    }

    // PART C

    static String alphabet = "abcdefghijklmnopqrstuvwxyz";

    private static ArrayList<String> buildBlocks(String cipherText, int keyLength) {
        String[] blocks = new String[keyLength];
        for (int i = 0; i < keyLength; i += 1) {
            blocks[i] = "";
        }

        for (int i = 0; i < cipherText.length(); i += keyLength) {
            for (int j = 0; j < keyLength && (j + i) != cipherText.length(); j += 1) {
                blocks[j] = blocks[j] + Character.toString(cipherText.charAt(i + j));
            }
        }

        return new ArrayList<String>(Arrays.asList(blocks));
    }

    private static Map<Character, BigDecimal> buildFrequencies(String s) {
        Character c;
        Map<Character, BigDecimal> frequencies = new HashMap<>();
        for (int i = 0; i < s.length(); i += 1) {
            c = Character.toLowerCase(s.charAt(i));
            BigDecimal curFreq = frequencies.get(c);
            if (curFreq == null) {
                frequencies.put(c, BigDecimal.ONE);
            } else {
                frequencies.put(c, curFreq.add(BigDecimal.ONE));
            }
        }
        return frequencies;
    }

    private static BigDecimal mutualIndexOfCoincidence(String s1, String s2) {
        BigDecimal n = BigDecimal.valueOf(s1.length());
        BigDecimal m = BigDecimal.valueOf(s2.length());

        Map<Character, BigDecimal> s1Frequencies = buildFrequencies(s1);
        Map<Character, BigDecimal> s2Frequencies = buildFrequencies(s2);

        Character curChar;
        BigDecimal index = BigDecimal.ONE;
        BigDecimal s1Freq;
        BigDecimal s2Freq;
        for (int i = 0; i < alphabet.length(); i += 1) {
            curChar = alphabet.charAt(i);

            s1Freq = s1Frequencies.get(curChar);
            s2Freq = s2Frequencies.get(curChar);

            if (s1Freq != null && s2Freq != null ) {
                index = index.add(s1Freq.multiply(s2Freq));
            }

            s1Freq = null;
            s2Freq = null;
        }

        BigDecimal x = (BigDecimal.ONE).divide(n.multiply(m), 5 , RoundingMode.HALF_DOWN);
        return x.multiply(index);
    }

    private static ArrayList<ArrayList<BigDecimal>> mutualIndiciesOfRotatedBlocks(
        ArrayList<String> blocks
    ) {
        int numberOfBlocks = blocks.size();
        ArrayList<ArrayList<BigDecimal>> indicies = new ArrayList<>();

        int jOffset = 5;
        for (int i = 1; i <= numberOfBlocks; i += 1) {
            String iBlock = blocks.get(i - 1);
            for (int j = numberOfBlocks - jOffset; j <= numberOfBlocks; j += 1) {
                System.out.print(i + ", " + j + " ");
                String jBlock = blocks.get(j - 1);
                for (int x = 0; x < alphabet.length(); x += 1) {
                    System.out.print(mutualIndexOfCoincidence(iBlock, shiftString(jBlock, x)) + ", ");
                }
                System.out.print("\n");
            }
            jOffset -= 1;
        }
        
        return indicies;
    }

    // HELPERS

    private static String shiftString(String s, int n){
        StringBuilder sb = new StringBuilder();
       for (int i = 0; i < s.length(); i++) {
           char c = s.charAt(i);
           if       (c >= 'a' && c <= 'm') c += n;
           else if  (c >= 'A' && c <= 'M') c += n;
           else if  (c >= 'n' && c <= 'z') c -= n;
           else if  (c >= 'N' && c <= 'Z') c -= n;
           sb.append(c);
       }
       return sb.toString();
    }

    private static String readFromFile(String filename) {
        try {
            BufferedReader cipherReader = new BufferedReader(
                new FileReader(filename)
            );
            String cipherText = cipherReader.readLine();
            cipherReader.close();
            return cipherText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } 
    }
    
    public static void main (String args[]){
        String cipherText = readFromFile("input_ex.txt");
        
        // Find list of trigrams from cipher text
        ArrayList<String> formattedTrigrams = findTriagrams(cipherText);
        
        // Compare trigrams for match
        SimpleImmutableEntry<ArrayList<String>, ArrayList<Integer>> result =
            findTrigramMatches(formattedTrigrams);
        ArrayList<String> trigramMatch = result.getKey();
        ArrayList<Integer> trigramPosition = result.getValue();

        System.out.println("Part A");
        System.out.println("Trigrams: " + trigramMatch);
        
        ArrayList<Integer> kasiskiTable = getKasiskiTable(trigramMatch, trigramPosition);
        int kasikiKey = likelyKeyLength(kasiskiTable);
        System.out.println("Kasiski Likely Key Length: " + kasikiKey);
        
        System.out.println("\nPart B");
        System.out.println("Key | Average Index | Individiual Indices of Coincidence");
        System.out.println("--------------------------------------------------------");
        int keyLength = buildString(cipherText);      
        System.out.println("Probable key length is " + keyLength);

        System.out.println("\nPart C");
        ArrayList<String> blocks = buildBlocks(cipherText, keyLength);
        BigDecimal index = mutualIndexOfCoincidence(
            "abirdinhandisworthtwointhebush",
            "astitchintimesavesnine"
        );
        System.out.println(index);
        // mutualIndiciesOfRotatedBlocks(blocks);
    }
}
