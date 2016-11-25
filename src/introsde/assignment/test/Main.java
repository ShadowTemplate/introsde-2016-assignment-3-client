package introsde.assignment.test;

import introsde.assignment.soap.PeopleImplService;
import introsde.assignment.soap.Person;

public class Main {
    public static void main(String[] args) {
        PeopleImplService peopleImplService = new PeopleImplService();
        Person person = peopleImplService.getPeopleImplPort().readPerson(0);
        System.out.println(person.getFirstname() + " " + person.getLastname());
    }
}
