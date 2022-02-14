package hu.bsstudio.raktr.model;

public enum BackStatus {
    OUT, BACK, PLANNED
    //Simple rent: added => OUT ---> brought back => BACK
    //Complex rent: added => PLANNED ---> packing => OUT ---> back => BACK
}
