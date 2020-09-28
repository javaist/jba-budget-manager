package budget.Model;

public class Transaction {
    protected double price;
    protected String text;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getBalancePrice() {
        return price;
    }
}
