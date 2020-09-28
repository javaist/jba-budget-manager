package budget.Presenter;

import budget.Model.Category;
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
            view.breakLine();
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
        do {
            String action = selectCategory();
            view.breakLine();
            if ("Back".equals(action)) {
                return;
            }
            Purchase purchase = new Purchase(Category.valueOf(action));
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
        } while (true);
    }


    private void show() {
        if (transactions.size() == 0) {
            view.resultMessage("Purchase list is empty");
            return;
        }
        while (true) {
            String action = selectCategory(true);
            view.breakLine();
            if ("Back".equals(action)) {
                return;
            }

            int counter = 0;
            double sum = 0;
            for (Transaction transaction : transactions) {
                if (transaction instanceof Purchase) {
                    Purchase purchase = (Purchase) transaction;
                    if ("All".equals(action) ||
                            Objects.equals(purchase.getCategory(), Category.valueOf(action))) {
                        sum += transaction.getPrice();
                        view.requestMessage(transaction.toString());
                        counter++;
                    }
                }
            }
            view.requestMessage(action + ":");
            if (counter == 0) {
                view.resultMessage("Purchase list is empty");

            } else {
                view.resultMessage(String.format("Total sum: $%.2f", sum));
            }
        }
    }

    private void getBalance() {
        double sum = 0;
        for (Transaction transaction : transactions) {
            sum += transaction.getBalancePrice();
        }
        // According to the task balance can't be negative
        sum = Math.max(sum, 0d);
        view.resultMessage(String.format("Balance: $%.2f", sum));

    }


    private int selectAction() {
        int choice;
        do {
            view.menu(
                    "Choose your action:",
                    "1) Add income",
                    "2) Add purchase",
                    "3) Show list of purchases",
                    "4) Balance",
                    "0) Exit"
            );
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (0 > choice ||  choice > 4) {
                    throw new NumberFormatException();
                }
                return choice;
            } catch (NumberFormatException e) {
                view.resultMessage("Please input a number from 0 to 4");
            }
        } while (true);
    }

    private String selectCategory() {
        return selectCategory(false);
    }

    private String selectCategory(boolean all) {
        int choice;
        int extra = all ? 3 : 2;
        String[] choices = new String[Category.values().length + extra];
        choices[0] = "Choose the type of purchase";

        for (int i = 0; i < Category.values().length; i++) {
            choices[i + 1] = String.format("%d) %s", i + 1, Category.values()[i]);
        }

        if (all) {
            choices[choices.length - 2] = String.format("%d) All", choices.length - 2);
        }

        choices[choices.length - 1] = String.format("%d) Back", choices.length - 1);
        do {
            view.menu(choices);
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (1 > choice || choice >= choices.length) {
                    throw new NumberFormatException();
                } else if (choice == choices.length - 1) {
                    return "Back";
                } else if (all && choice == choices.length - 2) {
                    return "All";
                } else {
                    return Category.values()[choice - 1].toString();
                }
            } catch (NumberFormatException e) {
                view.resultMessage(String.format("Please input a number from 1 to %d", Category.values().length));
            }

        } while (true);
    }
}
