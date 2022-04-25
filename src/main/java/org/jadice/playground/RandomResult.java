package org.jadice.playground;

import java.util.Random;

// https://bundesliga-tipphilfe.de/1-bundesliga-tipphilfe/statistiken/2020-2021
public class RandomResult {
    private static final int HOME = 42;
    private static final int DRAW = 27;
    private static final int AWAY = 31;

    private static final int zeroZero = 18;
    private static final int oneOne = 42;
    private static final int twoTwo = 16;

    private static final int twoOne = 47;
    private static final int oneZero = 37;
    private static final int twoZero = 29;
    private static final int threeOne = 27;
    private static final int threeZero = 20;
    private static final int threeTwo = 14;
    private static final int fourOne =13;
    private static final int fourZero = 11;

    public static int[] drawResult(){
        int i = new Random().nextInt(HOME+DRAW+AWAY);
        // Draw
        if (i < DRAW){
            int j = new Random().nextInt(oneOne+zeroZero+twoTwo);
            if (j < oneOne){
                return new int[]{0,0};
            }
            else if (j < oneOne+twoTwo) {
                return new int[]{1, 1};
            }
            else{
                return new int[]{2,2};
            }

        }
        else{
            int[] returnValue;
            // home win (i >= DRAW && i < DRAW+ AWAY
            int j = new Random().nextInt(twoOne+oneZero+twoZero+threeOne+threeZero+threeTwo+fourOne+fourZero);
            if (j < twoOne){
                returnValue = new int[]{2, 1};
            }
            else if (j < twoOne+oneZero){
                returnValue = new int[]{1,0};
            }
            else if (j < twoOne+oneZero+twoZero){
                returnValue = new int[]{2,0};
            }
            else if (j < twoOne+oneZero+twoZero+threeOne){
                returnValue = new int[]{3,1};
            }
            else if (j < twoOne+oneZero+twoZero+threeOne+threeZero){
                returnValue = new int[]{3,0};
            }
            else if (j < twoOne+oneZero+twoZero+threeOne+threeZero+threeTwo){
                returnValue = new int[]{3,2};
            }
            else if (j < twoOne+oneZero+twoZero+threeOne+threeZero+threeTwo+fourOne){
                returnValue = new int[]{4,1};
            }
            else{
                returnValue = new int[]{4,0};
            }

            // away win -> switch home and away goals
            if (i >= HOME+DRAW){
                int temp = returnValue[0];
                returnValue[0] = returnValue[1];
                returnValue[1] = temp;
            }
            return returnValue;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100;i++){
            int[] ints = drawResult();
            System.out.println(ints[0] + "." + ints[1]);
        }
    }
}
