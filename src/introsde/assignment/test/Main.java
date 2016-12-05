package introsde.assignment.test;

import introsde.assignment.soap.*;

import javax.xml.datatype.DatatypeFactory;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        runTest();
    }

    private static void runTest() throws Exception {
        try(PrintWriter log = new PrintWriter(new FileOutputStream("client.log"))) {
            log.println("Endpoint: http://introsde-a3-server.herokuapp.com/ws/people?wsdl\n");
            System.out.println("Endpoint: http://introsde-a3-server.herokuapp.com/ws/people?wsdl");
            People service = new PeopleService().getPeopleImplPort();

            service.resetDB();

            // method #4: createPerson
            System.out.println("#4: Creating random people...");
            for (int i = 1; i <= 3; i++) {
                PersonTO p = new PersonTO();
                p.setFirstname("Name" + i);
                p.setLastname("Surname" + i);
                service.createPerson(p);
                log.println("Method #4: createPerson\n[Input]\n"+ representPerson(p) + "\n");
            }

            // method #1: readPersonList
            System.out.println("\n#1: Fetching newly created people...");
            log.println("Method #1: readPersonList\n[Output]");
            List<PersonTO> people = service.readPersonList();
            for (PersonTO person : people) {
                System.out.println(representPerson(person));
                log.println(representPerson(person));
            }
            log.println();

            // method #3: updatePerson
            final PersonTO onePerson = people.get(0);
            System.out.println("\n#3: Changing surname of person with id " + onePerson.getId() + "...");
            onePerson.setLastname("New Surname");
            service.updatePerson(onePerson);
            log.println("Method #3: updatePerson\n[Input]\n" + representPerson(onePerson) + "\n");

            // method #2: readPerson
            System.out.println("\n#2: Checking update...");
            PersonTO person = service.readPerson(onePerson.getId());
            log.println("Method #2: readPerson\n[Input]\n" + onePerson.getId() + "\n[Output]\n" + representPerson(person) + "\n");
            System.out.println(representPerson(person));

            // method #5: deletePerson
            System.out.println("\n#5: Deleting person with id " + onePerson.getId() + "...");
            service.deletePerson(onePerson.getId());
            log.println("Method #5: deletePerson\n[Input]\n" + onePerson.getId() + "\n");
            System.out.println("Checking if the person is still present...");
            System.out.println("Is present? Result: " + service.readPersonList().parallelStream()
                    .filter(p -> p.getId().equals(onePerson.getId())).findAny().isPresent());

            // method #9: savePersonMeasure
            PersonTO otherPerson = people.get(1);
            System.out.println("\n#9: Adding measures to person with id " + otherPerson.getId() + "...");
            String latestMeasureType = "";
            Long latestMid = 0L;
            for (int i = 1; i <= 3; i++) {
                for (int j = 1; j <= 2; j++) {
                    MeasureTO measure = new MeasureTO();
                    measure.setMeasureType("MeasureType" + i);
                    measure.setMeasureValueType("MeasureValueType" + i + "-" + j);
                    measure.setMeasureValue("MeasureValue" + i + "-" + j);
                    GregorianCalendar c = new GregorianCalendar();
                    c.setTime(new Date());
                    measure.setDateRegistered(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
                    MeasureTO addedMeasure = service.savePersonMeasure(otherPerson.getId(), measure);
                    log.println("Method #9: savePersonMeasure\n[Input]\n" + otherPerson.getId() + "\n"+
                            representMeasure(measure) + "\n[Output]\n" + representMeasure(addedMeasure) + "\n");
                    System.out.println("Added: " + representMeasure(addedMeasure));
                    latestMeasureType = "MeasureType" + i;
                    latestMid = addedMeasure.getMid();
                }
            }

            // method #7: readMeasureTypes
            System.out.println("\n#7: Reading measure types...");
            List<String> types = service.readMeasureTypes();
            log.println("Method #7: readMeasureTypes\n[Output]");
            for (String type : types) {
                System.out.println(type);
                log.println(type);
            }
            log.println();

            // method #6: readPersonHistory
            System.out.println("\n#6: Reading history of person with id " + otherPerson.getId() + " for measure " + latestMeasureType + "...");
            List<MeasureTO> measureHistory = service.readPersonHistory(otherPerson.getId(), latestMeasureType);
            log.println("Method #6: readPersonHistory\n[Input]\n" + otherPerson.getId() + "\n" + latestMeasureType + "\n[Output]");
            for (MeasureTO measure : measureHistory) {
                System.out.println(representMeasure(measure));
                log.println(representMeasure(measure));
            }
            log.println();

            // method #8: readPersonMeasure
            System.out.println("\n#8: Reading measure with id " + latestMid + " of person with id " + otherPerson.getId() + " for measure " + latestMeasureType + "...");
            MeasureTO measure = service.readPersonMeasure(otherPerson.getId(), latestMeasureType, latestMid);
            System.out.println(representMeasure(measure));
            log.println("Method #8: readPersonMeasure\n[Input]\n" + otherPerson.getId() + "\n" + latestMeasureType + "\n" + latestMid + "\n[Output]\n" + representMeasure(measure) + "\n");

            // method #10: updatePersonMeasure
            System.out.println("\n#10: Updating measure with id " + latestMid + " of person with id " + otherPerson.getId() + " for measure " + latestMeasureType + "...");
            log.println("Method #10: updatePersonMeasure\n[Input]\n" + otherPerson.getId() + "\n" + representMeasure(measure));
            measure.setMeasureValue(measure.getMeasureValue() + "_NEW");
            measure.setMeasureValueType(measure.getMeasureValueType() + "_NEW");
            MeasureTO updatedMeasure = service.updatePersonMeasure(otherPerson.getId(), measure);
            log.println("[Output]\n" + representMeasure(updatedMeasure));
            System.out.println(representMeasure(updatedMeasure));

            service.resetDB();
        }
    }

    private static String representPerson(PersonTO person) {
        return "Id: " + person.getId() +
                "\nName: " + person.getFirstname() +
                "\nSurname: " + person.getLastname() +
                "\nCurrent Health: " + String.join(", ", person.getCurrentHealth().stream().map(Main::representMeasure).collect(Collectors.toList())) +
                "\nMeasure History: " + String.join(", ", person.getHealthHistory().stream().map(Main::representMeasure).collect(Collectors.toList()));
    }

    private static String representMeasure(MeasureTO m) {
        return  "Id: " + m.getMid() + " - " +
                "Type: " + m.getMeasureType() + " - " +
                "Date: " + m.getDateRegistered() + " - " +
                "Value: " + m.getMeasureValue() + " - " +
                "ValueType: " + m.getMeasureValueType();
    }
}
