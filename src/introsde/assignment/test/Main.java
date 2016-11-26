package introsde.assignment.test;

import introsde.assignment.soap.Measure;
import introsde.assignment.soap.People;
import introsde.assignment.soap.PeopleService;
import introsde.assignment.soap.Person;

import javax.xml.ws.Holder;

public class Main {
    public static void main(String[] args) {
        PeopleService peopleService = new PeopleService();
        People peopleImplPort = peopleService.getPeopleImplPort();

        Person alex = new Person();
        alex.setFirstname("Marco");
        alex.setLastname("Test");
        Holder<Person> holder = new Holder<>();
        holder.value = alex;
        printPerson(holder.value);
        peopleImplPort.createPerson(holder);
        printPerson(holder.value);

        holder.value.setLastname("Scibilia");
        peopleImplPort.updatePerson(holder);
        printPerson(holder.value);




    }



    private static void printPerson(Person person) {
        System.out.println(person.getId() + " " + person.getFirstname() + " " + person.getLastname());
        System.out.println("CH");
        person.getCurrentHealth().forEach(Main::printMeasure);
        System.out.println("History");
        person.getHealthHistory().forEach(Main::printMeasure);
    }

    private static void printMeasure(Measure m) {
        System.out.println(m.getMid() + " " +
                m.getMeasureType() + " " +
                m.getDateRegistered() + " " +
                m.getMeasureValue() + " " +
                m.getMeasureValueType());
    }
}
