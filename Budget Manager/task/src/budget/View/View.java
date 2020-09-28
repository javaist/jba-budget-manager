package budget.View;

public class View {
    public void menu() {
        System.out.println("Choose your action:");
        System.out.println("1) Add income");
        System.out.println("2) Add purchase");
        System.out.println("3) Show list of purchases");
        System.out.println("4) Balance");
        System.out.println("0) Exit");
    }

    public void requestMessage(String s) {
        System.out.println(s);
    }

    public void resultMessage(String s) {
        System.out.println(s);
        System.out.println();
    }
}
