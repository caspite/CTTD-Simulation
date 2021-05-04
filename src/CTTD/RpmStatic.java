package CTTD;

import java.util.Random;

public final class RpmStatic {



        double[][] careTime = {{0, 180}, {1, 170}, {2, 160}, {3, 150}, {4, 140}, {5, 130}, {6, 120}, {7, 110}, {8, 90}, {9, 60}, {10, 50}, {11, 40}, {12, 30}};
        double[][] survivalProbability = {{0, 0.052}, {1, 0.089}, {2, 0.15}, {3, 0.23}, {4, 0.35}, {5, 0.49}, {6, 0.63}, {7, 0.75}, {8, 0.84}, {9, 0.90}, {10, 0.94}, {11, 0.97}, {12, 0.98}};
        double[][] URGENTrpmProbability = {{0, 0.0476}, {1, 0.0714}, {2, 0.0952}, {3, 0.119}, {4, 0.19}, {5, 0.238}, {6, 0.238}};
        double[][] MEDUIMrpmProbability = {{7, 0.32}, {8, 0.32}, {9, 0.36}};
        double[][] NONURGENTrpmProbability = {{10, 0.38}, {11, 0.38}, {12, 0.238}};
        double[][] deterioration = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {4, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {6, 5, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0},
                {8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0},
                {9, 8, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0},
                {9, 8, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0},
                {10, 9, 9, 8, 8, 7, 6, 6, 5, 5, 4, 4},
                {11, 11, 10, 10, 9, 8, 8, 7, 7, 6, 6, 5},
                {12, 12, 11, 11, 10, 10, 10, 10, 9, 9, 8, 8}};


}
