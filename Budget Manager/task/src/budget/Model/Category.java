package budget.Model;

public class Category {
    private String name;
    private double sum;

    public Category(String value) {
        this.name = value;
    }

    public String getName() {
        return name;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
