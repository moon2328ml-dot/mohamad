package com.mohammadbahrami.border;

public class Truck {
    public final long id; public final String plate, driver, phone, cargo, owner, broker, origin, parking, weight; public final int status;
    public Truck(long id, String plate, String driver, String phone, String cargo, String owner, String broker, String origin, String parking, String weight, int status) {
        this.id=id; this.plate=plate; this.driver=driver; this.phone=phone; this.cargo=cargo; this.owner=owner; this.broker=broker; this.origin=origin; this.parking=parking; this.weight=weight; this.status=status;
    }
}
