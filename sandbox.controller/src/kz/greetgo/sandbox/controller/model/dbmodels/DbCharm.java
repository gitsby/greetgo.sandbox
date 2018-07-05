package kz.greetgo.sandbox.controller.model.dbmodels;

public class DbCharm{
    public int id;
    public String name;
    public String description;
    public Float energy;

    @Override
    public String toString() {
        return "DbCharm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", energy=" + energy +
                '}';
    }
}