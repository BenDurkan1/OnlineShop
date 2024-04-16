import java.util.ArrayList;
import java.util.List;

public class OrderManager implements Observable {
    private List<Observer> observers = new ArrayList<>();
    private String orderStatus;

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(orderStatus);
        }
    }

    public void setOrderStatus(String status) {
        this.orderStatus = status;
        notifyObservers();
    }
}
