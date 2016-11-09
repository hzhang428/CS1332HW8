import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Your implementations of various pattern matching algorithms.
 *
 * @author YOUR NAME HERE
 * @version 1.0
 */
public class PatternMatching {

    /**
     * Brute Force Algorithm compares a pattern with the text for each possible
     * shift of pattern with respect to the text.
     *
     * Runtime: O(nm) where n is the size of text and m is the size of pattern
     *
     * @throws IllegalArgumentException if the pattern is null or of length 0 or
     * text is null
     * @param pattern the pattern you are trying to match
     * @param text the body of text where you search for the pattern
     * @return list of integers representing the first index a match occurs or
     * an empty list if the text is of length 0
     */
    public static List<Integer> bruteForce(CharSequence pattern,
        CharSequence text) {
        try {
            List<Integer> index = new ArrayList<>();
            int p = pattern.length();
            int t = text.length();
            if (p == 0) {
                throw new IllegalArgumentException();
            } else if (p > t) {
                return index;
            }
            for (int i = 0; i <= t - p; i++) {
                int k = 0;
                while (k < p && text.charAt(i + k) == pattern.charAt(k)) {
                    k++;
                }
                if (k == p) {
                    index.add(i);
                }
            }
            return index;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Boyer Moore algorithm that uses a last table. Works better with large
     * alphabets.
     *
     * Runtime : O(nm + s) where n is the size of text, m is the size of pattern
     * and s is the size of alphabet
     *
     * NOTE: Make sure to implement {@code buildLastTable} before 
     * implementing this method.
     *
     * @throws IllegalArgumentException if the pattern is null or of length 0 or
     * if text is null
     * @param pattern the pattern you are trying to match
     * @param text the body of text where you search for the pattern
     * @return list of integers representing the first index a match occurs or
     * an empty list if the text is of length 0
     */
    public static List<Integer> boyerMoore(CharSequence pattern,
        CharSequence text) {
        try {
            List<Integer> index = new ArrayList<>();
            int p = pattern.length();
            int t = text.length();
            Map<Character, Integer> indexTable = buildLastTable(pattern);
            System.out.println(indexTable);
            if (p == 0) {
                throw new IllegalArgumentException();
            } else if (p > t) {
                return index;
            }
            int i = p - 1;
            int j = p - 1;
            while (i < t) {
                char tt = text.charAt(i);
                char pp = pattern.charAt(j);
                if (tt == pp) {
                    if (j == 0) {
                        index.add(i);
                        j = p - 1;
                        i += p;
                    } else {
                        i--;
                        j--;
                    }
                } else {
                    int jump;
                    if (indexTable.get(tt) == null) {
                        jump = -1;
                    } else {
                        jump = indexTable.get(tt);
                    }
                    i += p - Math.min(j, 1 + jump);
                    j = p - 1;
                }
            }
            return index;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Builds last occurrence table for the Boyer Moore algorithm.
     *
     * NOTE : each char x will have an entry at table.get(x).
     * Each entry should be the last index of x where x is a particular
     * character in your pattern.
     * If x is not in the pattern, then the table will not contain the key x
     * and you will have to check for that in your BoyerMoore
     *
     * Ex. octocat
     *
     * table.get(o) = 3
     * table.get(c) = 4
     * table.get(t) = 6
     * table.get(a) = 5
     * table.get(everything else) = null, which you will interpret in
     * Boyer-Moore as -1
     *
     * If the pattern is empty, return an empty map.
     *
     * @throws IllegalArgumentException if the pattern is null
     * @param pattern a {@code CharSequence} you are building last table for
     * @return a Map with keys of all of the characters in the pattern mapping
     *         to their last occurrence in the pattern
     */
    public static Map<Character, Integer> buildLastTable(CharSequence pattern) {
        Map<Character, Integer> indexMap = new HashMap<>();
        try {
            int p = pattern.length();
            if (p == 0) {
                return indexMap;
            }
            for (int j = p - 1; j >= 0; j--) {
                char x = pattern.charAt(j);
                if (!indexMap.containsKey(x)) {
                    indexMap.put(x, j);
                }
            }
            return indexMap;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("pattern can not be null.");
        }
    }

    /**
     * Knuth-Morris-Pratt (KMP) algorithm that relies on the failure table (also
     * called failure function). Works better with small alphabets.
     *
     * Runtime: O(m+n) where n is the size of the text and m is the size of the
     * pattern
     *
     * NOTE: Make sure to implement {@code buildFailureTable} before 
     * implementing this method.
     *
     * @throws IllegalArgumentException if the pattern is null or of length 0 or
     * if text is null
     * @param pattern the pattern you are trying to match
     * @param text the body of text where you search for the pattern
     * @return list of integers representing the first index a match occurs or
     * an empty list if the text is of length 0
     */
    public static List<Integer> kmp(CharSequence pattern, CharSequence text) {
        try {
            List<Integer> indexTable = new ArrayList<>();
            int p = pattern.length();
            int t = text.length();
            if (p == 0) {
                throw new IllegalArgumentException();
            } else if (p > t) {
                return indexTable;
            }
            int[] fail = buildFailureTable(pattern);
            int i = 0;
            int j = 0;
            while (i < t && t >= p + i - j) {
                if (text.charAt(i) == pattern.charAt(j)) {
                    if (j == p - 1) {
                        indexTable.add(i - j);
                        i = i + 1;
                        j = fail[j];
                    } else {
                        i++;
                        j++;
                    }
                } else {
                    if (j > 0) {
                        j = fail[j - 1];
                    } else {
                        i++;
                    }
                }
            }
            return indexTable;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Pattern or text cannot be null.");
        }
    }

    /**
     * Builds failure table that will be used to run the Knuth-Morris-Pratt
     * (KMP) algorithm.
     *
     * The table built should be the length of the input text.
     *
     * A given index i will be the largest prefix of the pattern
     * indices [0..i] that is also a suffix of the pattern indices [1..i].
     * This means that index 0 of the returned table will always be equal to 0
     *
     * Ex. ababac
     *
     * table[0] = 0
     * table[1] = 0
     * table[2] = 1
     * table[3] = 2
     * table[4] = 3
     * table[5] = 0
     *
     * If the pattern is empty, return an empty array.
     *
     * @throws IllegalArgumentException if the pattern is null
     * @param pattern a {@code CharSequence} you are building failure table for
     * @return integer array of size text.length that you are building a failure
     * table for
     */
    public static int[] buildFailureTable(CharSequence pattern) {
        try {
            int p = pattern.length();
            int[] table = new int[p];
            if (p == 0) {
                return table;
            }
            table[0] = 0;
            int j = 0;
            int i = 1;
            while (i < p) {
                if (pattern.charAt(i) == pattern.charAt(j)) {
                    table[i] = j + 1;
                    i++;
                    j++;
                } else if (j > 0) {
                    j = table[j - 1];
                } else {
                    table[i] = 0;
                    i++;
                }
            }
            return table;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("pattern cannot be null.");
        }
    }

}
