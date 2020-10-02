package budget.Presenter;

import budget.Model.Category;
import budget.Model.Income;
import budget.Model.Purchase;
import budget.Model.Transaction;
import budget.View.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Presenter {
    View view = new View();
    ArrayList<Transaction> transactions = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);
    String fileName = "purchases.txt";
    final HashMap<String, Category> categoryByName = new HashMap<>();
    final HashMap<Integer, Category> categoryByOrder = new HashMap<>();

    {
        Category category;
        String[] categories = {"Food", "Clothes", "Entertainment", "Other"};
        for (int i = 0; i < categories.length; i++) {
            category = new Category(categories[i]);
            categoryByName.put(categories[i], category);
            categoryByOrder.put(i + 1, category);
        }
    }

    public void run() {
        int choice;
        do {
            view.requestMessage("Choose your action:");
            choice = this.selectAction(
                    new String[]{
                            "1) Add income",
                            "2) Add purchase",
                            "3) Show list of purchases",
                            "4) Balance",
                            "5) Save",
                            "6) Load",
                            "7) Analyze (Sort)",
                            "0) Exit"
                    }
            );
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
                case 5:
                    this.saveToFile();
                    break;
                case 6:
                    this.loadFromFile();
                    break;
                case 7:
                    this.analyze();
                    break;
            }
        } while (choice != 0);
        view.resultMessage("Bye!");
    }

    private void analyze() {
        int choice;
        do {
            view.requestMessage("How do you want to sort?");
            choice = selectAction(new String[]{
                    "1) Sort all purchases",
                    "2) Sort by type",
                    "3) Sort certain type",
                    "4) Back"
            }, 1);
            view.breakLine();
            switch (choice) {
                case 1:
                    this.sort(null);
                    break;
                case 2:
                    this.sortByType();
                    break;
                case 3:
                    this.sortCertainType();
                    break;
            }

        } while (choice != 4);
    }

    private void sort(Category category) {
        ArrayList<Purchase> purchases = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction instanceof Purchase) {
                Purchase purchase = (Purchase) transaction;
                if (category == null ||
                        Objects.equals(category.getName(), purchase.getCategory().getName())) {
                    purchases.add((Purchase) transaction);
                }
            }
        }

        if (purchases.size() == 0) {
            view.resultMessage("Purchase list is empty");
            return;
        }

        purchases.sort((purchase, t1) -> {
            if (purchase.getPrice() > t1.getPrice()) {
                return -1;
            } else if (purchase.getPrice() < t1.getPrice()) {
                return 1;
            }
            return 0;
        });
        showPurchases(purchases, "All");

    }

    private void sortByType() {
        ArrayList<Purchase> purchases = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction instanceof Purchase) {
                purchases.add((Purchase) transaction);
            }
        }

        ArrayList<Category> categories = new ArrayList<>();
        HashMap<String, Double> categorySum = new HashMap<>();
        for (Purchase purchase : purchases) {
            String key = purchase.getCategory().getName();
            if (categorySum.containsKey(key)) {
                categorySum.replace(key, categorySum.get(key) + purchase.getPrice());
            } else {
                categorySum.put(key, purchase.getPrice());
            }
        }
        for (int i = 0; i < categoryByName.size(); i++) {
            Category category = categoryByOrder.get(i + 1);
            if (categorySum.containsKey(category.getName())) {
                category.setSum(categorySum.get(category.getName()));
            } else {
                category.setSum(0);
            }
            categories.add(category);
        }
        categories.sort((cat1, cat2) -> {
            if (cat1.getSum() > cat2.getSum()) {
                return -1;
            } else if (cat1.getSum() < cat2.getSum()) {
                return 1;
            }
            return 0;
        });

        double total = 0;
        view.requestMessage("Types:");
        for (Category category : categories) {
            view.requestMessage(String.format(
                    "%s - $%.2f",
                    category.getName(),
                    category.getSum())
            );
            total += category.getSum();
        }
        view.resultMessage(String.format("Total sum: $%.2f", total));
    }

    private void sortCertainType() {
        String category = selectCategory(false, false);
        view.breakLine();
        this.sort(categoryByName.get(category));

    }


    private String encode(String string) {
        byte[] bytes = string.getBytes();
        return String.format("%s\n", new String(Base64.getEncoder().encode(bytes)));
    }

    private String decode(String string) {
        return new String(Base64.getDecoder().decode(string.getBytes()));
    }

    private void loadFromFile() {
        try (Scanner fileReader = new Scanner(new File(fileName))) {
            while (fileReader.hasNext()) {
                String type = decode(fileReader.nextLine());
                String text = decode(fileReader.nextLine());
                double price = Double.parseDouble(decode(fileReader.nextLine()));
                if (type.contains("Purchase")) {
                    Category category = categoryByName.get(decode(fileReader.nextLine()));
                    Purchase transaction = new Purchase(category);
                    transaction.setPrice(price);
                    transaction.setText(text);
                    transactions.add(transaction);
                } else if (type.contains("Income")) {
                    Income transaction = new Income();
                    transaction.setPrice(price);
                    transaction.setText(text);
                    transactions.add(transaction);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        view.resultMessage("Purchases were loaded!");
    }

    private void saveToFile() {
        try (FileWriter fileWriter = new FileWriter(new File(fileName))) {
            for (Transaction transaction : transactions) {
                fileWriter.write(encode(transaction.getClass().toString()));
                fileWriter.write(encode(transaction.getText()));
                fileWriter.write(encode(String.valueOf(transaction.getPrice())));
                if (transaction instanceof Purchase) {
                    fileWriter.write(encode(((Purchase) transaction).getCategory().getName()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.resultMessage("Purchases were saved!");
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
            String action = selectCategory(false, true);
            view.breakLine();
            if ("Back".equals(action)) {
                return;
            }
            Purchase purchase = new Purchase(categoryByName.get(action));
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

    private void showPurchases(ArrayList<Purchase> purchases, String category) {
        view.requestMessage(category + ":");
        int counter = 0;
        double sum = 0;
        for (Purchase purchase : purchases) {
            if ("All".equals(category) ||
                    Objects.equals(purchase.getCategory().getName(), category)) {
                sum += purchase.getPrice();
                view.requestMessage(purchase.toString());
                counter++;
            }
        }
        if (counter == 0) {
            view.resultMessage("Purchase list is empty");
        } else {
            view.resultMessage(String.format("Total sum: $%.2f", sum));
        }
    }

    private void show() {
        ArrayList<Purchase> purchases = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction instanceof Purchase) {
                purchases.add((Purchase) transaction);
            }
        }
        if (purchases.size() == 0) {
            view.resultMessage("Purchase list is empty");
            return;
        }
        while (true) {
            String action = selectCategory(true, true);
            view.breakLine();
            if ("Back".equals(action)) {
                return;
            }
            showPurchases(purchases, action);
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

    private int selectAction(String[] choices) {
        return selectAction(choices, 0);
    }


    private int selectAction(String[] choices, int index) {
        int choice;

        do {
            view.menu(choices);
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (index > choice || choice > choices.length) {
                    throw new NumberFormatException();
                }
                return choice;
            } catch (NumberFormatException e) {
                view.resultMessage(String.format(
                        "Please input a number from %d to %d", index,
                        choices.length - 1 + index));
            }
        } while (true);
    }

    private String selectCategory(boolean all, boolean back) {
        int choice;
        int extra = 1;
        extra += all ? 1 : 0;
        extra += back ? 1 : 0;
        String[] choices = new String[categoryByName.size() + extra];
        choices[0] = "Choose the type of purchase";

        for (int i = 1; i < categoryByOrder.keySet().size() + 1; i++) {
            choices[i] = String.format("%d) %s", i, categoryByOrder.get(i).getName());
        }
        int allChoice = 0;
        int backChoice = 0;
        if (all && back) {
            choices[choices.length - 2] = String.format("%d) All", choices.length - 2);
            allChoice = choices.length - 2;
            choices[choices.length - 1] = String.format("%d) Back", choices.length - 1);
            backChoice = choices.length - 1;
        } else if (all) {
            choices[choices.length - 1] = String.format("%d) All", choices.length - 1);
            allChoice = choices.length - 1;
        } else if (back) {
            choices[choices.length - 1] = String.format("%d) Back", choices.length - 1);
            backChoice = choices.length - 1;
        }
        do {
            view.menu(choices);
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (1 > choice || choice >= choices.length) {
                    throw new NumberFormatException();
                } else if (back && choice == backChoice) {
                    return "Back";
                } else if (all && choice == allChoice) {
                    return "All";
                } else {
                    return categoryByOrder.get(choice).getName();
                }
            } catch (NumberFormatException e) {
                view.resultMessage(String.format("Please input a number from 1 to %d",
                        categoryByName.size() + extra));
            }
        } while (true);
    }
}
