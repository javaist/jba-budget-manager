package budget.Model;

public class Purchase extends Transaction {
    @Override
    public double getPrice() {
        return 0d - price;
    }

    @Override
    public String toString() {
        return String.format("%s $%.2f", this.text, (0 - this.price));
    }

    @Override
    public void setPrice(double price) {
        this.price = 0d - price;
    }
}
