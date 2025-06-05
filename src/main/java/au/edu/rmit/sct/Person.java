package au.edu.rmit.sct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A class representing a Person in the RoadRegistry system.

    format would be :
    personID|firstName|lastName|address|birthdate|isSuspended|demeritData
*/

public class Person {

    // fields

    private String personID;
    private String firstName;
    private String lastName;
    private String address;      // "StreetNumber|Street|City|State|Country"
    private String birthdate;    // "DD-MM-YYYY"
    private boolean isSuspended;
    private HashMap<Date, Integer> demeritPoints = new HashMap<>();

    private static final String FILE_PATH = "data/persons.txt";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);


    // constructors, getters, and setters:::
    // constructor for creating a new Person without any demerit history.
    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID    = personID;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.address     = address;
        this.birthdate   = birthdate;
        this.isSuspended = false;
    }

    // private constructor used when parsing an existing Person from a file line,
    // including any existing demerit history and suspension status
    private Person(String personID,
                   String firstName,
                   String lastName,
                   String address,
                   String birthdate,
                   boolean isSuspended,
                   HashMap<Date, Integer> demeritPoints) {
        this.personID      = personID;
        this.firstName     = firstName;
        this.lastName      = lastName;
        this.address       = address;
        this.birthdate     = birthdate;
        this.isSuspended   = isSuspended;
        this.demeritPoints = demeritPoints;
    }

    public String getPersonID() {
        return personID;
    }
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isSuspended() {
        return isSuspended;
    }
    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }

    public HashMap<Date, Integer> getDemeritPoints() {
        return demeritPoints;
    }
    public void setDemeritPoints(HashMap<Date, Integer> demeritPoints) {
        this.demeritPoints = demeritPoints;
    }


    // HELPER: parseFromLine() and toLine()
    // parse exactly one line from data/persons.txt into a Person object.
    // format of each line: personID|firstName|lastName|address|birthdate|isSuspended|demeritData
     // @return a new Person object, or null if parsing fails

    public static Person parseFromLine(String line) {
        try {

            int idx1 = line.indexOf('|');
            if (idx1 < 0) return null;
            int idx2 = line.indexOf('|', idx1 + 1);
            if (idx2 < 0) return null;
            int idx3 = line.indexOf('|', idx2 + 1);
            if (idx3 < 0) return null;


            int idxLast1 = line.lastIndexOf('|');
            if (idxLast1 < 0 || idxLast1 <= idx3) return null;
            int idxLast2 = line.lastIndexOf('|', idxLast1 - 1);
            if (idxLast2 < 0 || idxLast2 <= idx3) return null;
            int idxLast3 = line.lastIndexOf('|', idxLast2 - 1);
            if (idxLast3 < 0 || idxLast3 <= idx3) return null;

            String pid        = line.substring(0, idx1);
            String fName      = line.substring(idx1 + 1, idx2);
            String lName      = line.substring(idx2 + 1, idx3);
            String addr       = line.substring(idx3 + 1, idxLast3);
            String bdate      = line.substring(idxLast3 + 1, idxLast2);
            boolean suspended = Boolean.parseBoolean(line.substring(idxLast2 + 1, idxLast1));
            String demeritData = line.substring(idxLast1 + 1);


            HashMap<Date, Integer> dpMap = new HashMap<>();
            if (!demeritData.isBlank()) {
                String[] entries = demeritData.split(";", -1);
                for (String entry : entries) {
                    if (entry.isBlank()) continue;
                    String[] pair = entry.split(":", 2);
                    if (pair.length != 2) continue;
                    Date offenseDate = DATE_FORMAT.parse(pair[0]);
                    int pts = Integer.parseInt(pair[1]);
                    dpMap.put(offenseDate, pts);
                }
            }

            return new Person(pid, fName, lName, addr, bdate, suspended, dpMap);
        } catch (ParseException | NumberFormatException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // convert this Person object into exactly one line for data/persons.txt, in the format:
    // personID|firstName|lastName|address|birthdate|isSuspended|demeritData

    public String toLine() {
        StringBuilder dpBuilder = new StringBuilder();
        for (Date d : demeritPoints.keySet()) {
            String dateStr = DATE_FORMAT.format(d);
            int pts = demeritPoints.get(d);
            dpBuilder.append(dateStr).append(":").append(pts).append(";");
        }
        String demeritData = dpBuilder.toString();

        return String.join("|",
                personID,
                firstName,
                lastName,
                address,
                birthdate,
                Boolean.toString(isSuspended),
                demeritData
        );
    }

    //VALIDATION HELPERS
    // condition 1 for personID:
     // must be exactly 10 characters
      // first two chars must be digits '2'..'9'
      // last two chars must be uppercase letters 'A'..'Z'

    private static boolean isValidPersonID(String id) {
        if (id == null || id.length() != 10) return false;

        // check the first two chars ('2'..'9')
        for (int i = 0; i < 2; i++) {
            char c = id.charAt(i);
            if (c < '2' || c > '9') return false;
        }
        // check the last two chars ('A'..'Z')
        for (int i = 8; i < 10; i++) {
            char c = id.charAt(i);
            if (c < 'A' || c > 'Z') return false;
        }
        // check that the six middle chars contain at least two special chars
        String middle = id.substring(2, 8);
        int specialCount = 0;
        for (char c : middle.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                specialCount++;
            }
        }
        return specialCount >= 2;
    }

    //condition 2 for address:
    // split into exactly five parts by '|'
    // the 4th part (index 3) must equal "Victoria"

    private static boolean isValidAddress(String address) {
        if (address == null) return false;
        String[] parts = address.split("\\|", -1);
        if (parts.length != 5) return false;
        return "Victoria".equals(parts[3]);
    }

    // condition 3: Any birthdate or offense date must be exactly "DD-MM-YYYY" and a real calendar date.
    // we set DATE_FORMAT.setLenient(false) so invalid dates like "32-01-2020" are rejected.

    private static boolean isValidDate(String dateStr) {
        if (dateStr == null) return false;
        DATE_FORMAT.setLenient(false);
        try {
            DATE_FORMAT.parse(dateStr.trim());
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    // calculate whole-year age given birthdateStr="DD-MM-YYYY" and referenceDateStr="DD-MM-YYYY".
     // returns −1 if parsing fails

    private static int calculateAge(String birthdateStr, String referenceDateStr) {
        try {
            DATE_FORMAT.setLenient(false);
            Date birth = DATE_FORMAT.parse(birthdateStr);
            Date ref   = DATE_FORMAT.parse(referenceDateStr);

            Calendar cBirth = Calendar.getInstance();
            cBirth.setTime(birth);
            Calendar cRef = Calendar.getInstance();
            cRef.setTime(ref);

            int age = cRef.get(Calendar.YEAR) - cBirth.get(Calendar.YEAR);
            if ((cRef.get(Calendar.MONTH) < cBirth.get(Calendar.MONTH)) ||
                    (cRef.get(Calendar.MONTH) == cBirth.get(Calendar.MONTH) &&
                            cRef.get(Calendar.DAY_OF_MONTH) < cBirth.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            return -1;
        }
    }

    // FILE I/O HELPERS
    //  read all Person lines from data/persons.txt into a List<Person>. If file doesn’t exist, returns an empty list.
    private static List<Person> readAllPersonsFromFile() {
        List<Person> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Person p = parseFromLine(line.trim());
                if (p != null) {
                    list.add(p);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return list;
    }

    private static void writeAllPersonsToFile(List<Person> persons) {
        File file = new File(FILE_PATH);
        // ensure parent directory exists
        file.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (Person p : persons) {
                writer.write(p.toLine());
                writer.newLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

     // addPerson() featuers:
            // validate personID, address, birthdate according to the three conditions.
            // if valid and no existing person has the same ID, append a new line to data/persons.txt and return true.
            // otherwise return false

    public boolean addPerson() {
        if (!isValidPersonID(this.personID)) {
            return false;
        }
        if (!isValidAddress(this.address)) {
            return false;
        }
        if (!isValidDate(this.birthdate)) {
            return false;
        }

        // prevent duplicate ID generation
        List<Person> all = readAllPersonsFromFile();
        for (Person existing : all) {
            if (existing.getPersonID().equals(this.personID)) {
                return false;
            }
        }

        // append this Person to data/persons.txt
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();  // this ensure "data/" exists
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(this.toLine());
            writer.newLine();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    // updatePersonalDetails(originalID):
        // conditions:
            // the Person (found by originalID) is under 18, they may NOT change their address.
            // they change their birthdate, they may NOT change any other field (ID, firstName, lastName, address).
            // the first digit of the originalID is even, then their ID may NOT change.
            // changed fields still must pass the same validation as in addPerson.
            // returns true if the update succeeds (and data/persons.txt is rewritten), otherwise false.

    public boolean updatePersonalDetails(String originalID) {
        List<Person> all = readAllPersonsFromFile();
        Person target = null;
        for (Person p : all) {
            if (p.getPersonID().equals(originalID)) {
                target = p;
                break;
            }
        }
        if (target == null) {
            return false;  // no such Person found
        }

        // Person’s age today for under 18 rule

        int age = calculateAge(target.getBirthdate(), DATE_FORMAT.format(new Date()));
        if (age < 0) {
            return false;  // stored birthdate is malformed
        }

        boolean birthdateChanged = !this.birthdate.equals(target.getBirthdate());
        boolean idChanged        = !this.personID.equals(target.getPersonID());
        boolean fNameChanged     = !this.firstName.equals(target.getFirstName());
        boolean lNameChanged     = !this.lastName.equals(target.getLastName());
        boolean addressChanged   = !this.address.equals(target.getAddress());

        // birthdate is changed?  no other field may change

        if (birthdateChanged) {
            if (idChanged || fNameChanged || lNameChanged || addressChanged) {
                return false;
            }
            if (!isValidDate(this.birthdate)) {
                return false;
            }
        }

        // no change in their address if the Person is under 18
        if (age < 18 && addressChanged) {
            return false;
        }

        // no change in their ID if originalID’s first digit is even
        char firstDigit = originalID.charAt(0);
        if (((firstDigit - '0') % 2 == 0) && idChanged) {
            return false;
        }

        //re-validate new ID format if ID is change
        if (idChanged && !isValidPersonID(this.personID)) {
            return false;
        }

        if (addressChanged && !isValidAddress(this.address)) {
            return false;
        }

        // if All checks passed ⇒ apply updates & rewrite data/persons.txt
        target.setPersonID(this.personID);
        target.setFirstName(this.firstName);
        target.setLastName(this.lastName);
        target.setAddress(this.address);
        target.setBirthdate(this.birthdate);

        writeAllPersonsToFile(all);
        return true;
    }
}
