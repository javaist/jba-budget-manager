package budget.Model;

public class Purchase extends Transaction {
    Category category;

    public Purchase(Category category) {
        this.category = category;
    }

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

    public Category getCategory() {
        return this.category;
    }
}
