package com.quantxt.helper;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by matin on 5/24/17.
 */
public class BWTransform {

    private static Logger logger = LoggerFactory.getLogger(BWTransform.class);
    private static final int R = 256;
    private String str;
    private String encode;
    private int first;

    public BWTransform(String in){
        str = in;
    }

    // apply Burrows-Wheeler encoding, reading from standard input and
    // writing to standard output
    public void encode() {
        int N = str.length();
        // concatenate the string to itself
        String ss = str.concat(str);
        String[] strs = new String[N];
        for (int i = 0; i < N; i++) {
            strs[i] = ss.substring(i, i + N);
        }

        // using system sort
        Arrays.sort(strs);
        StringBuilder sb = new StringBuilder();
        int index = Arrays.binarySearch(strs, str);
        first = index;
        for (int i = 0; i < N; i++) {
            sb.append(strs[i].charAt(N - 1));
        }
        encode = sb.toString();
    }

    // apply Burrows-Wheeler decoding, reading from standard input and
    // writing to standard output
    public void decode() {
        str = "";
        char[] s = encode.toCharArray();
        int N = s.length;
        // allocate the ending array
        char[] t = new char[N];
        for (int i = 0; i < N; i++)
            t[i] = s[i];
        // allocate an array to store the next array
        int[] next = new int[N];
        // allocate an array to store 1st char of the sorted suffixes
        char[] f = new char[N];
        // an array to store the total count for each character
        int[] count = new int[R+1];
        // do key-index counting, but store values in the next[] array
        for (int i = 0; i < N; i++)
            count[t[i]+1]++;
        for (int r = 0; r < R; r++)
            count[r+1] += count[r];
        for (int i = 0; i < N; i++) {
            next[count[t[i]]] = i;
            f[count[t[i]]++] = t[i];
        }
        // write out
        int current = first;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) {
            sb.append(f[current]);
            current = next[current];
        }
        str = sb.toString();
    }


    public static List<Integer> mfEncode(String msg, String symTable){
        List<Integer> output = new LinkedList<Integer>();
        StringBuilder s = new StringBuilder(symTable);
        for(char c : msg.toCharArray()){
            int idx = s.indexOf("" + c);
            output.add(idx);
            s = s.deleteCharAt(idx).insert(0, c);
        }
        return output;
    }

    public static String mfDecode(List<Integer> idxs, String symTable){
        StringBuilder output = new StringBuilder();
        StringBuilder s = new StringBuilder(symTable);
        for(int idx : idxs){
            char c = s.charAt(idx);
            output = output.append(c);
            s = s.deleteCharAt(idx).insert(0, c);
        }
        return output.toString();
    }

    private static void test(String toEncode, String symTable){
        List<Integer> encoded = mfEncode(toEncode, symTable);
        System.out.println(toEncode + ": " + encoded);
        String decoded = mfDecode(encoded, symTable);
        System.out.println((toEncode.equals(decoded) ? "" : "in") + "correctly decoded to " + decoded);
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args) {
        String str = "https://github.com/fujiawu/burrows-wheeler-compression/blob/master/BurrowsWheeler.java";
        str = str.toLowerCase().replaceAll("[^a-z]+" , "");
        BWTransform bw = new BWTransform(str);
        bw.encode();
        logger.info(bw.encode);
        bw.decode();
        logger.info(bw.str);

        String symTable = "abcdefghijklmnopqrstuvwxyz";
        List<Integer> list = mfEncode(bw.encode, symTable);
        Gson gson = new Gson();
        logger.info(mfDecode(list, symTable));
    }
}
