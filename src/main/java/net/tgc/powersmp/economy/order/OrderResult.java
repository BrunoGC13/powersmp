package net.tgc.powersmp.economy.order;

public class OrderResult {
    private final OrderResultType type;
    private final Order order;
    private final int revenue;
    private final String msg;

    public OrderResult(Order order, OrderResultType type) {
        this.type = type;
        this.order = order;
        this.revenue = 0;
        this.msg = null;
    }

    public OrderResult(Order order, OrderResultType type, String msg) {
        this.type = type;
        this.order = order;
        this.revenue = 0;
        this.msg = msg;
    }

    public OrderResult(Order order, OrderResultType type, int revenue) {
        this.type = type;
        this.order = order;
        this.revenue = revenue;
        this.msg = null;
    }

    public OrderResult(Order order, OrderResultType type, int revenue, String msg) {
        this.type = type;
        this.order = order;
        this.revenue = revenue;
        this.msg = msg;
    }

    public Order getOrder() {
        return order;
    }

    public OrderResultType getType() {
        return type;
    }

    public int getRevenue() {
        return revenue;
    }

    public String getMsg() {
        return msg;
    }
}
