package budget.View;

public class View {
    public void menu(String...args) {
        for (String line: args) {
            System.out.println(line);
        }
    }

    public void requestMessage(String s) {
        System.out.println(s);
    }

    public void resultMessage(String s) {
        System.out.println(s);
        System.out.println();
    }

    public void breakLine() {
        System.out.println();
    }
}
