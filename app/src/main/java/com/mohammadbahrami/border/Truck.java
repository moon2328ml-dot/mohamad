package com.mohammadbahrami.border;

public class Truck {
    public final long id; public final String plate, driver, phone, cargo, owner, broker, origin, parking, weight, entryDate, exitDate, borderStatus; public final int status;
    public Truck(long id, String plate, String driver, String phone, String cargo, String owner, String broker, String origin, String parking, String weight, int status, String entryDate, String exitDate, String borderStatus) {
        this.id=id; this.plate=plate; this.driver=driver; this.phone=phone; this.cargo=cargo; this.owner=owner; this.broker=broker; this.origin=origin; this.parking=parking; this.weight=weight; this.status=status; this.entryDate=entryDate; this.exitDate=exitDate; this.borderStatus=borderStatus;
    }
}
