package kz.greetgo.sandbox.controller.model;

public class User {
    public String id;
    public String surname;
    public String name;
    public String patronymic;
    public long birthDate;
    public CharmType charm;
    public Phone[] phones;
    public Address factualAddress;
    public Address registeredAddress;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        String userstr = id+" "+surname+" "+name+" "+patronymic+" "+birthDate+ " "+charm+"\n";
        StringBuilder phonestr = new StringBuilder();
        for(Phone phone: phones){
                phonestr.append(phone.toString()).append("\n");
        }

        sb.append(userstr).append(phonestr).append(factualAddress.toString()).append(registeredAddress);
        return registeredAddress.toString();
    }
}


