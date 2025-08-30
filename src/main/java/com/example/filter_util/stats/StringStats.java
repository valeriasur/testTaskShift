package com.example.filter_util.stats;

public class StringStats {
    private long count = 0;
    private int shortestLength = Integer.MAX_VALUE;
    private int longestLength = 0;

    public void update(String value) {
        count++;
        int length = value.length();
        if (length < shortestLength) {
            shortestLength = length;
        }
        if (length > longestLength) {
            longestLength = length;
        }
    }

    public long getCount() {
        return count;
    }

    public int getShortestLength() {
        return shortestLength == Integer.MAX_VALUE ? 0 : shortestLength;
    }

    public int getLongestLength() {
        return longestLength;
    }
}
