import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

class Equation {

    final int size;
    final double[][] leftMatrix;
    final double[] rightMatrix;

    Equation(int size, double[][] leftMatrix, double[] rightMatrix) {
        this.size = size;
        this.leftMatrix = leftMatrix;
        this.rightMatrix = rightMatrix;
    }

    void substractCells() {

    }

    void printLeftMatrix() {
        System.out.println("Left matrix: ");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                System.out.print(leftMatrix[i][j] + " ");
            System.out.print("\n");
        }
    }

    void printRightMatrix() {
        System.out.println("Right matrix: ");
        for (double x : rightMatrix) {
            System.out.println(x);
        }
    }

    void getResults(String path) throws FileNotFoundException {
        File file = new File(path + "res.txt");
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        System.setOut(ps);
        System.out.println(size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                System.out.print(leftMatrix[i][j] + " ");
            System.out.print("\n");
        }

        for (double x : rightMatrix) {
            System.out.print(x + " ");
        }
    }
}
