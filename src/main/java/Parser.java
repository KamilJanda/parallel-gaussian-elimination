import java.io.*;

class Parser {

    static Equation readEquationFromFile(String path) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(new File(path)));

        String line = in.readLine();

        int size = Integer.parseInt(line);
        double[][] leftMatrix = new double[size][size];
        double[] rightMatrix = new double[size];

        for (int i = 0; i < size; i++) {

            String[] sp = in.readLine().split(" ");

            for (int j = 0; j < size; j++) {
                leftMatrix[i][j] = Double.parseDouble(sp[j]);
            }
        }
        String[] sp = in.readLine().split(" ");
        for (int j = 0; j < size; j++) {
            rightMatrix[j] = Double.parseDouble(sp[j]);
        }

        return new Equation(size,leftMatrix,rightMatrix);
    }

}
