import parallelism.ConcurentBlockRunner;
import parallelism.Production;

import java.io.IOException;

public class Main {

    private static Equation equation;
    private static ConcurentBlockRunner scheduler = new ConcurentBlockRunner();

    public static void main(String[] args) throws IOException, InterruptedException {

        equation = Parser.readEquationFromFile("/Users/kamil/Documents/agh/tw/parallel-gaussian-elimination/src/main/java/resources/test.txt");

        equation.printLeftMatrix();
        equation.printRightMatrix();

        parallelGaussianElimination();

        equation.printLeftMatrix();
        equation.printRightMatrix();

        equation.getResults("/Users/kamil/Documents/agh/tw/parallel-gaussian-elimination/src/main/java/");
    }

    static void parallelGaussianElimination() throws InterruptedException {

        for (int currentDiagonalIndex = 0; currentDiagonalIndex < equation.size; currentDiagonalIndex++) {

            swapIfCurrentcurrentDiagonalIndexIsZero(currentDiagonalIndex);

            double[] ratios = calculateRatioForRows(currentDiagonalIndex);

            substract(currentDiagonalIndex, ratios);


        }

        for (int i = 0; i < equation.size; i++) {
            double value = equation.leftMatrix[i][i];
            equation.leftMatrix[i][i] = 1.0;
            equation.rightMatrix[i] /= value;
        }

    }

    private static double[] calculateRatioForRows(int currentDiagonalIndex) throws InterruptedException {
        Thread[] threadsPool = new Thread[equation.size];
        double[] ratios = new double[equation.size];

        for (int row = 0; row < equation.size; row++) {
            if (row == currentDiagonalIndex) continue;

            final int i = row;
            final int j = currentDiagonalIndex;

            scheduler.addThread(new Production() {
                @Override
                public void run() {
                    super.run();
                    ratios[i] = equation.leftMatrix[i][j] / equation.leftMatrix[j][j];
                }
            });

        }

        scheduler.startAll();

        return ratios;
    }

    private static void substract(final int currentDiagonalIndex, double[] ratios) throws InterruptedException {
        Thread[] threadsPool = new Thread[equation.size + 1];

        for (int row = 0; row < equation.size; row++) {
            if (row == currentDiagonalIndex) continue;
            for (int column = 0; column < equation.size; column++) {

                final int i = row;
                final int j = column;

                scheduler.addThread(new Production() {
                    @Override
                    public void run() {
                        super.run();
                        equation.leftMatrix[i][j] = equation.leftMatrix[i][j] - ratios[i] * equation.leftMatrix[currentDiagonalIndex][j];

                    }
                });

            }

            final int row_f = row;
            int indexOfLast = equation.size;

            scheduler.addThread(new Production() {
                @Override
                public void run() {
                    super.run();
                    equation.rightMatrix[row_f] = equation.rightMatrix[row_f] - ratios[row_f] * equation.rightMatrix[currentDiagonalIndex];

                }
            });
            scheduler.startAll();
        }
    }

    private static void swapIfCurrentcurrentDiagonalIndexIsZero(int currentDiagonalIndex) {
        if (equation.leftMatrix[currentDiagonalIndex][currentDiagonalIndex] == 0) {
            for (int i = currentDiagonalIndex; i < equation.size; i++) {
                if (equation.leftMatrix[i][currentDiagonalIndex] != 0) {
                    // swap rows
                    swapRows(currentDiagonalIndex, i);
                }
            }
        }
    }

    private static void swapRows(int a, int b) {
        double[] nonZeroRow = equation.leftMatrix[a];
        double nonZeroCellRight = equation.rightMatrix[a];
        double[] currentRow = equation.leftMatrix[b];
        double currentRowRight = equation.rightMatrix[b];

        equation.leftMatrix[a] = currentRow;
        equation.rightMatrix[a] = currentRowRight;

        equation.leftMatrix[b] = nonZeroRow;
        equation.rightMatrix[b] = nonZeroCellRight;
    }

}
