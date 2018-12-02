import parallelism.ConcurentBlockRunner;
import parallelism.Production;

import java.io.IOException;

public class Main {

    private static Equation equation;
    private static ConcurentBlockRunner sheduler = new ConcurentBlockRunner();

    public static void main(String[] args) throws IOException, InterruptedException {

        equation = Parser.readEquationFromFile("/Users/kamil/Documents/agh/tw/parallel-gaussian-elimination/src/main/java/test.txt");


        equation.printLeftMatrix();
        equation.printRightMatrix();

        parallelGaussianElimination();

        equation.printLeftMatrix();
        equation.printRightMatrix();

    }

    static void parallelGaussianElimination() throws InterruptedException {

        for (int currentDiagonalIndex = 0; currentDiagonalIndex < equation.size; currentDiagonalIndex++) {

            double[] ratios = calculateRatioForRows(currentDiagonalIndex);

            substract(currentDiagonalIndex, ratios);
        }

    }

    private static void startAndJoinThreads(Thread[] threads) throws InterruptedException {
        for (Thread thread : threads) {
            if (thread != null)
                thread.start();
        }

        for (Thread thread : threads) {
            if (thread != null)
                thread.join();
        }
    }

    private static double[] calculateRatioForRows(int currentDiagonalIndex) throws InterruptedException {
        Thread[] threadsPool = new Thread[equation.size];
        double[] ratios = new double[equation.size];

        for (int row = 0; row < equation.size; row++) {
            if (row == currentDiagonalIndex) continue;

            final int i = row;
            final int j = currentDiagonalIndex;

//            threadsPool[row] = new Thread(() -> {
//                System.out.println(String.format("A[%s][%s] = %s / A[%s][%s] = %s",
//                        i, j, equation.leftMatrix[i][j], j, j, equation.leftMatrix[j][j]));
//
//                ratios[i] = equation.leftMatrix[i][j] / equation.leftMatrix[j][j];
//                System.out.println("Ratio for " + i + " : " + ratios[i]);
//            });

            sheduler.addThread(new Production() {
                @Override
                public void run() {
                    super.run();
                    System.out.println(String.format("A[%s][%s] = %s / A[%s][%s] = %s",
                            i, j, equation.leftMatrix[i][j], j, j, equation.leftMatrix[j][j]));

                    ratios[i] = equation.leftMatrix[i][j] / equation.leftMatrix[j][j];
                    System.out.println("Ratio for " + i + " : " + ratios[i]);
                }
            });

        }


//        startAndJoinThreads(threadsPool);
        sheduler.startAll();

        return ratios;
    }

    private static void substract(final int currentDiagonalIndex, double[] ratios) throws InterruptedException {
        Thread[] threadsPool = new Thread[equation.size + 1];

        for (int row = 0; row < equation.size; row++) {
            if (row == currentDiagonalIndex) continue;
            for (int column = 0; column < equation.size; column++) {

                final int i = row;
                final int j = column;
//                threadsPool[column] = new Thread(() -> {
//                    System.out.println("Start thread ------------");
//                    System.out.println(String.format("i = %s j=%s", i, j));
//                    System.out.println(String.format("first: %s ration: %s second: %s",
//                            equation.leftMatrix[i][j], ratios[i], equation.leftMatrix[currentDiagonalIndex][j]));
//
//                    equation.leftMatrix[i][j] = equation.leftMatrix[i][j] - ratios[i] * equation.leftMatrix[currentDiagonalIndex][j];
//
//                    System.out.println(String.format("After: %s", equation.leftMatrix[i][j]));
//                    System.out.println("End -----------------------");
//                });
//            }

                sheduler.addThread(new Production() {
                    @Override
                    public void run() {
                        super.run();
                        equation.leftMatrix[i][j] = equation.leftMatrix[i][j] - ratios[i] * equation.leftMatrix[currentDiagonalIndex][j];

                    }
                });

            }

            final int row_f = row;
            int indexOfLast = equation.size;

//                threadsPool[indexOfLast] = new Thread(() ->
//                        equation.rightMatrix[row_f] = equation.rightMatrix[row_f] - ratios[row_f] * equation.rightMatrix[currentDiagonalIndex]
//                );

            sheduler.addThread(new Production() {
                @Override
                public void run() {
                    super.run();
                    equation.rightMatrix[row_f] = equation.rightMatrix[row_f] - ratios[row_f] * equation.rightMatrix[currentDiagonalIndex];

                }
            });

//                startAndJoinThreads(threadsPool);

            sheduler.startAll();


        }


    }
}
