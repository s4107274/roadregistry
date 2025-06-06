package au.edu.rmit.sct;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PersonTest {
    private Person person;

    
    private static final String PERSON_FILE = "data/persons.txt";

    @BeforeEach
    void setUp() {
    
        person = new Person("00000000AA", "Dummy", "Dummy", "1|X|X|Victoria|Australia", "01-01-2000");
    
        try {
            Files.deleteIfExists(Paths.get(PERSON_FILE));
        } catch (IOException e) {
            System.err.println("Warning: could not delete " + PERSON_FILE + ": " + e.getMessage());
        }
    }

    // 1) ADD PERSON FUNCTION TESTS (5 test cases)

    @Test
    @DisplayName("AddPerson Test 1: Valid person with all conditions met")
    void testAddPerson_ValidData_ShouldReturnTrue() {
        person.setPersonID("56s_d%&fAB");
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("32|Highland Street|Melbourne|Victoria|Australia");
        person.setBirthdate("15-11-1990");

        boolean result = person.addPerson();
        assertTrue(result, "addPerson should return true for valid person data");
    }

    @Test
    @DisplayName("AddPerson Test 2: Invalid personID format")
    void testAddPerson_InvalidPersonID_ShouldReturnFalse() {
        person.setPersonID("12345678AB"); 
        person.setFirstName("Jane");
        person.setLastName("Smith");
        person.setAddress("45|Main Street|Melbourne|Victoria|Australia");
        person.setBirthdate("20-05-1995");

        boolean result = person.addPerson();
        assertFalse(result, "addPerson should return false for invalid personID format");
    }

    @Test
    @DisplayName("AddPerson Test 3: Invalid address with wrong state")
    void testAddPerson_InvalidAddress_ShouldReturnFalse() {
        person.setPersonID("78#$%@*ABC"); 
        person.setFirstName("Alice");
        person.setLastName("Johnson");
        person.setAddress("123|Queen Street|Sydney|NSW|Australia"); 
        person.setBirthdate("10-03-1988");

        boolean result = person.addPerson();
        assertFalse(result, "addPerson should return false when state is not Victoria");
    }

    @Test
    @DisplayName("AddPerson Test 4: Invalid birth date format")
    void testAddPerson_InvalidBirthDate_ShouldReturnFalse() {
        person.setPersonID("29!@#$%XYZ");
        person.setFirstName("Bob");
        person.setLastName("Wilson");
        person.setAddress("67|Collins Street|Melbourne|Victoria|Australia");
        person.setBirthdate("1990-11-15"); 

        boolean result = person.addPerson();
        assertFalse(result, "addPerson should return false for invalid birth date format");
    }

    @Test
    @DisplayName("AddPerson Test 5: PersonID with invalid first digit")
    void testAddPerson_InvalidFirstDigit_ShouldReturnFalse() {
        person.setPersonID("19!@#$%DEF"); 
        person.setFirstName("Carol");
        person.setLastName("Brown");
        person.setAddress("89|Bourke Street|Melbourne|Victoria|Australia");
        person.setBirthdate("25-12-1992");

        boolean result = person.addPerson();
        assertFalse(result, "addPerson should return false when first digit is not in range 2-9");
    }

    
    // 2) UPDATE PERSONAL DETAILS FUNCTION TESTS (5 test cases)
   

    @Test
    @DisplayName("UpdatePersonalDetails Test 1: Valid update for adult")
    void testUpdatePersonalDetails_ValidAdultUpdate_ShouldReturnTrue() {
        String originalID = "34@#$%^GHI";
        person = new Person(originalID, "David", "Lee",
                "12|Flinders Street|Melbourne|Victoria|Australia",
                "15-06-1985"); 
        assertTrue(person.addPerson());

        Person updated = new Person(originalID,
                "Daniel", "Li",
                "56|Spencer Street|Melbourne|Victoria|Australia",
                "15-06-1985");
        boolean result = updated.updatePersonalDetails(originalID);
        assertTrue(result, "updatePersonalDetails should return true for valid adult update");
    }

    @Test
    @DisplayName("UpdatePersonalDetails Test 2: Invalid—address change for minor")
    void testUpdatePersonalDetails_MinorAddressChange_ShouldReturnFalse() {
        String originalID = "45#$%@*JKL";
        person = new Person(originalID, "Emily", "Davis",
                "78|Elizabeth Street|Melbourne|Victoria|Australia",
                "15-06-2010"); 
        assertTrue(person.addPerson());

        Person updated = new Person(originalID,
                "Emily", "Davis",
                "90|Swanston Street|Melbourne|Victoria|Australia",
                "15-06-2010");
        boolean result = updated.updatePersonalDetails(originalID);
        assertFalse(result, "updatePersonalDetails should return false when updating address for minor");
    }

    @Test
    @DisplayName("UpdatePersonalDetails Test 3: Invalid—birthday change with other changes")
    void testUpdatePersonalDetails_BirthdayWithOtherChanges_ShouldReturnFalse() {
        String originalID = "67*&^%$MNO";
        person = new Person(originalID, "Frank", "Miller",
                "23|Russell Street|Melbourne|Victoria|Australia",
                "20-08-1990");
        assertTrue(person.addPerson());

        Person updated = new Person(originalID,
                "Francis", "Miller",
                "23|Russell Street|Melbourne|Victoria|Australia",
                "21-08-1990");
        boolean result = updated.updatePersonalDetails(originalID);
        assertFalse(result, "updatePersonalDetails should return false when changing birthday and another field");
    }

    @Test
    @DisplayName("UpdatePersonalDetails Test 4: Invalid—ID change when first digit is even")
    void testUpdatePersonalDetails_EvenFirstDigitIDChange_ShouldReturnFalse() {
        String originalID = "89!@#$%PQR";
        person = new Person(originalID, "Grace", "Taylor",
                "45|King Street|Melbourne|Victoria|Australia",
                "12-04-1993");
        assertTrue(person.addPerson());

        Person updated = new Person("23@#$%^STU", 
                "Grace", "Taylor",
                "45|King Street|Melbourne|Victoria|Australia",
                "12-04-1993");
        boolean result = updated.updatePersonalDetails(originalID);
        assertFalse(result, "updatePersonalDetails should return false when changing ID with even-first-digit");
    }

    @Test
    @DisplayName("UpdatePersonalDetails Test 5: Valid—only birthday change")
    void testUpdatePersonalDetails_OnlyBirthdayChange_ShouldReturnTrue() {
        String originalID = "35%^&*@VWX";
        person = new Person(originalID, "Henry", "Clark",
                "67|La Trobe Street|Melbourne|Victoria|Australia",
                "30-09-1987");
        assertTrue(person.addPerson());

        Person updated = new Person(originalID, "Henry", "Clark",
                "67|La Trobe Street|Melbourne|Victoria|Australia",
                "01-10-1987");
        boolean result = updated.updatePersonalDetails(originalID);
        assertTrue(result, "updatePersonalDetails should return true when changing only birthday");
    }

   
    // 3) ADD DEMERIT POINTS FUNCTION TESTS (5 test cases)


    @Test
    @DisplayName("AddDemeritPoints Test 1: Valid demerit points addition")
    void testAddDemeritPoints_ValidAddition_ShouldReturnSuccess() {
        String originalID = "46#$%@*YZA";
        person = new Person(originalID, "Isabella", "Rodriguez",
                "10|St Kilda Road|Melbourne|Victoria|Australia",
                "15-03-1995");
        assertTrue(person.addPerson());

        Person proxy = new Person(originalID, null, null, null, null);
        String result = proxy.addDemeritPoints("20-11-2023", 3);
        assertEquals("Success", result, "addDemeritPoints should return 'Success' for valid input");
    }

    @Test
    @DisplayName("AddDemeritPoints Test 2: Invalid points range")
    void testAddDemeritPoints_InvalidPointsRange_ShouldReturnFailed() {
        String originalID = "57@#$%^BCD";
        person = new Person(originalID, "Jack", "Anderson",
                "5|Prahran Street|Melbourne|Victoria|Australia",
                "22-07-1990");
        assertTrue(person.addPerson());

        Person proxy = new Person(originalID, null, null, null, null);
        String result = proxy.addDemeritPoints("15-10-2023", 8);
        assertEquals("Failed", result, "addDemeritPoints should return 'Failed' for invalid point range");
    }

    @Test
    @DisplayName("AddDemeritPoints Test 3: Invalid offense date format")
    void testAddDemeritPoints_InvalidDateFormat_ShouldReturnFailed() {
        String originalID = "68*&^%$EFG";
        person = new Person(originalID, "Kate", "Wilson",
                "100|Bourke Street|Melbourne|Victoria|Australia",
                "10-12-1988");
        assertTrue(person.addPerson());

        Person proxy = new Person(originalID, null, null, null, null);
        String result = proxy.addDemeritPoints("2023-11-15", 2);
        assertEquals("Failed", result, "addDemeritPoints should return 'Failed' for invalid date format");
    }

    @Test
    @DisplayName("AddDemeritPoints Test 4: Suspension for minor with >6 points")
    void testAddDemeritPoints_MinorSuspension_ShouldReturnSuccessAndSuspend() throws IOException {
        String originalID = "79!@#$%HIJ";
        person = new Person(originalID, "Liam", "Johnson",
                "12|Argyle Street|Melbourne|Victoria|Australia",
                "15-06-2005"); // minor
        assertTrue(person.addPerson());

        Person proxy1 = new Person(originalID, null, null, null, null);
        assertEquals("Success", proxy1.addDemeritPoints("10-01-2023", 4));

        Person proxy2 = new Person(originalID, null, null, null, null);
        String result = proxy2.addDemeritPoints("15-02-2023", 4);
        assertEquals("Success", result, "addDemeritPoints should return 'Success' even when suspension is triggered");

        Person fromFile = Person.parseFromLine(Files.readAllLines(Paths.get(PERSON_FILE)).get(0).trim());
        assertNotNull(fromFile);
        assertTrue(fromFile.isSuspended(), "Person under 21 should be suspended when total points > 6");
    }

    @Test
    @DisplayName("AddDemeritPoints Test 5: Suspension for adult with >12 points")
    void testAddDemeritPoints_AdultSuspension_ShouldReturnSuccessAndSuspend() throws IOException {
        String originalID = "24%^&*@KLM";
        person = new Person(originalID, "Mia", "Thompson",
                "8|Bourke Street|Melbourne|Victoria|Australia",
                "20-01-1980"); // adult
        assertTrue(person.addPerson());

        Person proxy1 = new Person(originalID, null, null, null, null);
        assertEquals("Success", proxy1.addDemeritPoints("05-03-2023", 6));

        Person proxy2 = new Person(originalID, null, null, null, null);
        assertEquals("Success", proxy2.addDemeritPoints("10-06-2023", 6));

        Person proxy3 = new Person(originalID, null, null, null, null);
        String result = proxy3.addDemeritPoints("20-08-2023", 3);
        assertEquals("Success", result, "addDemeritPoints should return 'Success' even when suspension is triggered");

        Person fromFile = Person.parseFromLine(Files.readAllLines(Paths.get(PERSON_FILE)).get(0).trim());
        assertNotNull(fromFile);
        assertTrue(fromFile.isSuspended(), "Person over 21 should be suspended when total points > 12");
    }
}
