package budget;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double sum = 0d;
        String line;
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            sum += Double.parseDouble(line.split("\\$")[1]);
            System.out.println(line);
        }
        System.out.printf("\nTotal: $%.2" +
                "f", sum);
    }
}
