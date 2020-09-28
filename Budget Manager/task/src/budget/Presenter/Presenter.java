package budget.Presenter;

import budget.Model.Income;
import budget.Model.Purchase;
import budget.Model.Transaction;
import budget.View.View;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Presenter {
    View view = new View();
    ArrayList<Transaction> transactions = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);

    public void run() {
        int choice;
        do {
            choice = this.selectAction();
            view.resultMessage("");
            switch (choice) {
                case 1:
                    this.addIncome();
                    break;
                case 2:
                    this.addPurchase();
                    break;
                case 3:
                    this.show();
                    break;
                case 4:
                    this.getBalance();
                    break;
            }
        } while (choice != 0);
        view.resultMessage("Bye!");
    }


    private void addIncome() {
        Income income = new Income();

        do {
            view.requestMessage("Enter income:");
            try {
                income.setPrice(Double.parseDouble(scanner.nextLine()));
            } catch (NumberFormatException e) {
                view.resultMessage("Should be a number!");
            }
        } while (income.getPrice() == 0d);

        transactions.add(income);
        view.resultMessage("Income was added!");
    }

    private void addPurchase() {
        Purchase purchase = new Purchase();
        do {
            view.requestMessage("Enter purchase name:");
            purchase.setText(scanner.nextLine());
        } while (purchase.getText() == null || Objects.equals("", purchase.getText()));
        do {
            view.requestMessage("Enter its price:");
            try {
                purchase.setPrice(Double.parseDouble(scanner.nextLine()));
            } catch (NumberFormatException e) {
                view.resultMessage("Should be a number!");
            }
        } while (purchase.getPrice() == 0d);

        transactions.add(purchase);
        view.resultMessage("Purchase was added!");
    }


    private void show() {
        if (transactions.size() == 0) {
            view.resultMessage("Purchase list is empty");
            return;
        }
        int counter = 0;
        double sum = 0;
        for (Transaction transaction: transactions) {
            if (transaction instanceof Purchase) {
                sum += transaction.getPrice();
                view.requestMessage(transaction.toString());
                counter++;
            }
        }
        if (counter == 0) {
            view.resultMessage("Purchase list is empty");
            return;
        }

        view.resultMessage(String.format("Total sum: $%.2f", sum));
    }

    private void getBalance() {
        double sum = 0;
        for (Transaction transaction: transactions) {
            sum += transaction.getBalancePrice();
        }
        // According to the task balance can't be negative
        sum = Math.max(sum, 0d);
        view.resultMessage(String.format("Balance: $%.2f", sum));

    }


    private int selectAction() {
        int choice;
        do {
            view.menu();
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 0 || choice > 4) {
                    throw new NumberFormatException();
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.println("Please input a number from 0 to 4");
            }

        } while (true);
    }
}
