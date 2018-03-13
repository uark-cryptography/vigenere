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

        return index.divide(n.multiply(m), 3 , RoundingMode.HALF_UP);
    }

    private static ArrayList<ArrayList<BigDecimal>> mutualIndiciesOfRotatedBlocks(
        ArrayList<String> blocks
    ) {
        int numberOfBlocks = blocks.size();
        ArrayList<ArrayList<BigDecimal>> indicies = new ArrayList<>();

        int jOffset = numberOfBlocks - 2;
        for (int i = 1; i <= numberOfBlocks; i += 1) {
            String iBlock = blocks.get(i - 1);
            for (int j = numberOfBlocks - jOffset; j <= numberOfBlocks; j += 1) {
                ArrayList<BigDecimal> curIndicies = new ArrayList<>();
                String jBlock = blocks.get(j - 1);
                for (int x = 0; x < alphabet.length(); x += 1) {
                    curIndicies.add(mutualIndexOfCoincidence(
                        iBlock,
                        shiftString(jBlock, x)
                    ));
                }

                indicies.add(curIndicies);
            }
            jOffset -= 1;
        }
        return indicies;
    }

    private static BigDecimal UPPER_LIMIT = new BigDecimal("0.065");
    private static void printRelations(
        int numberOfBlocks,
        ArrayList<ArrayList<BigDecimal>> mutualIndicies
    ) {
        ArrayList<Integer> shifts = new ArrayList<>();

        System.out.println(" i | j | shift ");
        System.out.println("---------------");
        int counter = 0;
        int jOffset = numberOfBlocks - 2;
        BigDecimal b;
        for (int i = 1; i <= numberOfBlocks; i += 1) {
            for (int j = numberOfBlocks - jOffset; j <= numberOfBlocks; j += 1) {
                ArrayList<BigDecimal> curIndicies = mutualIndicies.get(counter);
                counter += 1;
                for (int x = 0; x < alphabet.length(); x += 1) {
                    b = curIndicies.get(x);
                    if (b.compareTo(UPPER_LIMIT) > -1) {
                        System.out.println(" " +i  +" | " + j + " | " + x);
                    }
                }
            }
            jOffset -= 1;
        }
    }

    private static void guessKeywordAndDecode(
        String cipherText, 
        int keywordLength, 
        ArrayList<Integer> relativeShifts
    ) {
        System.out.println(" keyword | decoded");
        System.out.println("-----------------");
        String keyword = "";
        for (int i = 0; i < alphabet.length(); i += 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(shiftCharacter('a', i));
            for (int j = 0; j < (keywordLength - 1); j += 1) {
                Integer shift = relativeShifts.get(j);
                sb.append(shiftCharacter('a', i + shift));
            }

            keyword = sb.toString();
            System.out.print(" " + keyword + " | ");

            sb = new StringBuilder();
            for (int j = 0; j < cipherText.length(); j += 1) {
                char curChar = cipherText.charAt(j);
                int shift = (int)keyword.charAt(j % keywordLength) - 97;
                sb.append(shiftCharacter(curChar, -shift));
            }
            System.out.print(sb.toString() + "\n");
        }
    }


    // HELPERS

    private static void printMutualIndiciesOfCoincidence(
        int numberOfBlocks,
        ArrayList<ArrayList<BigDecimal>> indicies
    ) {
        System.out.println("Block | Mutual Indices of Coincidence");
        System.out.println("B_i | B_j |    0      1      2      3      4      5      6      7      8      9      10     11     12     13     14     15     16     17     18     19     20     21     22     23    24      25");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        int counter = 0;
        int jOffset = numberOfBlocks - 2;
        for (int i = 1; i <= numberOfBlocks; i += 1) {
            for (int j = numberOfBlocks - jOffset; j <= numberOfBlocks; j += 1) {
                System.out.print(" " + i + "  |  " + j + "  | ");
                System.out.println(indicies.get(counter));
                counter += 1;
            }
            jOffset -= 1;
        }
    }

    private static String shiftString(String s, int n){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            c = shiftCharacter(c, n);
            sb.append(c);
       }
       return sb.toString();
    }

    private static char shiftCharacter(char c, int n) {
        c = Character.toLowerCase(c);
        c += n;

        if (c > 122) {
            int diff = c - 122;
            return (char)(96 + diff);
        } else if (c < 97) {
            int diff = 97 - c;
            return (char)(123 - diff);
        } 
        return c;
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
        if (args.length == 0 || (!args[0].equals("a") && !args[0].equals("b"))) {
            System.out.println("Please pass in 'a' or 'b' as an argument.");
            return;
        }

        String cipherText = readFromFile("input_hw.txt");
            
        if (args[0].equals("a")) {
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

            ArrayList<ArrayList<BigDecimal>> mutualIndicies =
                mutualIndiciesOfRotatedBlocks(blocks);

            printMutualIndiciesOfCoincidence(blocks.size(), mutualIndicies);

            System.out.println();
            printRelations(blocks.size(), mutualIndicies);

            System.out.println(
                "\nPlease solve the system of equations, and pass the keylength " +
                "& relatives shifts them in to part b as 'java Vigenere [keylength] " +
                "[shift1] [shift2] ...'"
            );
        } else if (args[0].equals("b")) {
            int keyworkdLength = Integer.parseInt(args[1]);
            ArrayList<Integer> relativeShifts = new ArrayList<>();
            for (int i = 0; i < (keyworkdLength - 1); i += 1) {
                relativeShifts.add(Integer.parseInt(args[2 + i]));
            }

            guessKeywordAndDecode(cipherText, keyworkdLength, relativeShifts);
        }
    }
}
