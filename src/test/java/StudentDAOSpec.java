import at.campus02.dbp2.jpa.Gender;
import at.campus02.dbp2.jpa.Student;
import at.campus02.dbp2.jpa.StudentDAO;
import at.campus02.dbp2.jpa.StudentDAOImpl;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.text.DateFormatter;
import javax.swing.text.html.parser.Entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;  //der Teil, wird bei Verwendung dieser Klasse immmer automatisch zigefügt
import static org.junit.Assert.assertThat;

public class StudentDAOSpec {
    //Klasse für Tests, Tests sind Methoden der Klasse die ausgeführt werden
    //Strukturierung der tests in 3 Blöckke given(Vorraussetzungen die gebraucht werden um Test durchzuführen,
    // when (was getestet werden soll), then (überprüfen ob funktioniert)
    private EntityManager manager;
    private EntityManagerFactory factory;   //bruchen wir um entities zu machen
    private StudentDAO dao; //wollen wir prüfen


    // <editor-fold desc="Hilfsfunktionen">
    private Student prepareStudent(
            String firstname,
            String lastname,
            Gender gender,
            String birthdayString
    ){
        Student student = new Student();
        student.setGender(gender);
        student.setFirstName(firstname);
        student.setLastName(lastname);
        if(birthdayString != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            student.setBirthday(LocalDate.parse(birthdayString,formatter));
        }
        return student;
    }
    // <editor-fold desc=Hilfsfunktionen>

    @Before     //alle Dinge die für man den Test braucht werden hier vorbereitet
    public void setUp(){
        factory = Persistence.createEntityManagerFactory("nameOfJpaPersistenceUnit");   //Wichtig: Name der in der persistance.xml steht
        manager = factory.createEntityManager();
        dao = new StudentDAOImpl(factory);
    }

    //<editor-fold desc="Queries>
    private void create(Student student){
        manager.getTransaction().begin();
        manager.persist(student);
        manager.getTransaction().commit();
    }

   //wenn Test nicht berücksichtigt werden soll, dann kann mit der @Ignore Annotation der Test ausgelassen werden.
    @Test
    public void ensureThatToUpperCaseResultsInUpperCaseLetters() {
        //given
        String str1 = "string";

        //when
        //hier Test ob to Upper Case funktioniert hat

        String result = str1.toUpperCase();

        //then
        Assert.assertThat(result, is("STRING"));    //annotation (aasert that) kommt von Hamcrest-Matchern
    }

    @Test
    public void createNullAsStudentReturnsFalse(){


        //given
        StudentDAO dao = new StudentDAOImpl(factory);
        //when
        boolean result = dao.create(null);
        //then
        assertThat(result, is(false));  //Assert.assertThat wurde static importiert
    }

    @Test
    public void createPersitsStudentInDatabaseAndReturnsTrue(){
        //Testet ob Student tatsäschlich angelegt wird und somit auch aus Datenbank ausgelesen werden kann

        //given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, null);

        student.setLastName("Lastname");
        student.setFirstName("Firstname");
        student.setGender(Gender.FEMALE);

        //when
        boolean result = dao.create(student);

        //then
        assertThat(result, is(true));
        //überprüfen, ob der Student in der Datenbank existiert

        EntityManager manager = factory.createEntityManager();
        Student fromDB = manager.find(Student.class, student.getId());
        assertThat(fromDB, is(student));
    }

    @Test
    public void createAlreadyExistingStudentReturnsFalse(){
        //wir testen, ob wenn ein bereits existierender Studen in DB eingelesen wird, ob dann auch tatsächlich falsch ausgegben wird
        //given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, "22.09.1978");
        create(student);

        //when
        boolean result = dao.create(student);

        //then
        assertThat(result, is(false));
    }

    @Test
    public void findStudentReturnsEntityFromDatabase(){
        //given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, "22.09.1978");
        create(student);

        //when
        //der Student, den wir vom DAO bekommen:
        Student result = dao.find(student.getId());
        //...un der Student, den wir im Test aus der DB lesen...
        Student fromDB = manager.find(Student.class, student.getId());

        //then


        //...sollen die gleichen sein
        assertThat(result, is(fromDB));
    }

    @Test
    public void findStudentWithNullAsIdReturnsNull(){
        //Bei Angabe einer nicht existierenden ID (oder null) soll null zurückgegeben werden.
        //expect
        assertThat(dao.find(null), is(nullValue()));
    }

    @Test
    public void findStudentWithNotExistingIDReturnsNull(){
        //Bei Angabe einer nicht existierenden ID (oder null) soll null zurückgegeben werden.
        //expect
        assertThat(dao.find(4711), is(nullValue()));
    }

    @Test
    public void updateStudentChangesValuesInDatabase(){
        //Nach erfolgreichem Aufruf soll die Entity in der Datenbank die geänderten Werte übernommen haben und das geänderte Objekt zurückgegeben werden.

        //given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, "22.09.1978");
        create(student);

        //vorsichtahalber Cache ausleeren, weil wir denselben EntityManager für persist und dann find verwenden...
        manager.clear();

        //when
        //Namensänderung
        student.setLastName("Married-now");
        //geänderter Student wird
        Student result = dao.update(student);
        //...un der Student, den wir im Test aus der DB lesen...
        Student fromDB = manager.find(Student.class, student.getId());

        //then
        //...sollen die gleichen sein (Rückgabe wert (result) und die Entity aus DB (fromDB))
        assertThat(result.getLastName(), is("Married-now"));
        assertThat(fromDB.getLastName(), is("Married-now"));
        //test ob beide Objekte gleich sind (wäre nicht notwendig
        assertThat(result, is(fromDB));
    }

    @Test
    public void updateNullAsStudentReturnsNull(){
        //Bei Angabe eines nicht existierenden Studenten soll null zurückgegeben werden.
        //expect
        assertThat(dao.update(null), is(nullValue()));
    }

    @Test
    public void updateNotExistingStudentReturnsNull(){
        //Bei Angabe eines nicht existierenden Studenten soll null zurückgegeben werden.
        //expect
        //given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, "22.09.1978");

        //when
        Student result = dao.update(student);

        //then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void deleteStudentRemovesEntityFromDatabase(){
        //Bei Angabe eines nicht existierenden Studenten soll null zurückgegeben werden.
        //expect
        //given
        Student student = prepareStudent("firstname", "lastname", Gender.FEMALE, "22.09.1978");
        create(student);
        manager.clear();

        //when
        int id = student.getId();
        dao.delete(student);

        //then
        assertThat(dao.find(id), is(nullValue()));
        assertThat(manager.find(Student.class, id), is(nullValue()));
    }

    @Test
    public void deleteNullOrNotExistingStudentDoesNotThrowException(){
        //expect no Exception
        dao.delete(null);
        dao.delete(prepareStudent("firstname", "lastname", Gender.FEMALE, "22.09.1978"));
    }

    @Test   //Test laufen immer gleich ab: Studenten erzeugen, die abfragen, die ich haben will und ob ich alle bekomme, das wars
    public void findAllReturnsAllEntitiesFromDatabase(){
        //given
        Student student1 = prepareStudent("firstname1", "lastname1", Gender.FEMALE, "22.09.1978");
        Student student2 = prepareStudent("firstname2", "lastname2", Gender.FEMALE, "22.09.1978");
        Student student3 = prepareStudent("firstname3", "lastname3", Gender.MALE, "22.09.1978");

        create(student1);
        create(student2);
        create(student3);

        manager.clear();    //wegen caching vom manager müssen wir sicher sein, dass er wert aus DB holt und nicht aus Cache, deshalb leeren

        //when
        List<Student> result = dao.findAll();   //nullpointer

        //then
        assertThat(result.size(), is(3));
        assertThat(result, hasItems(student1, student2, student3));
    }


    @Test
    public void findByLastnameReturnsMatchingStudents() {
        //given
        String lastNameToFind = "lastname1";

        Student student1 = prepareStudent("firstname1", lastNameToFind, Gender.FEMALE, "22.09.1978");
        Student student2 = prepareStudent("firstname2", "lastname2", Gender.FEMALE, "22.09.1978");
        Student student3 = prepareStudent("firstname3", lastNameToFind, Gender.MALE, "22.09.1978");

        create(student1);
        create(student2);
        create(student3);

        //when
        List<Student> result = dao.findAllByLastname(lastNameToFind);

        //then
        assertThat(result.size(), is(2));
        assertThat(result, hasItems(student1, student3));
    }

    @Test
    public void findByLastNameWithNullParameterReturnsAllEntities() {
        //hierfür machen wir das gleich wie findByLastname, prüfen aber ob alle Studenten in zurückgegebener Liste sind, wenn lastname=null
        //given
        String lastNameToFind = "lastname1";

        Student student1 = prepareStudent("firstname1", lastNameToFind, Gender.FEMALE, "22.09.1978");
        Student student2 = prepareStudent("firstname2", "lastname2", Gender.FEMALE, "22.09.1978");
        Student student3 = prepareStudent("firstname3", "lastname3", Gender.MALE, "22.09.1978");

        create(student1);
        create(student2);
        create(student3);

        //when
        List<Student> result = dao.findAllByLastname(null);

        //then
        assertThat(result.size(), is(3));
        assertThat(result, hasItems(student1, student2, student3));
    }

    @Test
    public void findByLastnameReturnsMatchingStudentsCaseInsensitive() {
        //given
        String lastNameLoweCase = "lastname1";
        String lastNameUpperCase = "Lastname1";

        Student student1 = prepareStudent("firstname1", lastNameLoweCase, Gender.FEMALE, "22.09.1978");
        Student student2 = prepareStudent("firstname2", "lastname2", Gender.FEMALE, "22.09.1978");
        Student student3 = prepareStudent("firstname3", lastNameUpperCase, Gender.MALE, "22.09.1978");

        create(student1);
        create(student2);
        create(student3);

        //when
        List<Student> result = dao.findAllByLastname(lastNameUpperCase);

        //then
        assertThat(result.size(), is(2));
        assertThat(result, hasItems(student1, student3));
    }

    @Test
    public void findAllByGenderReturnsMatchingEntities() {
        //hierfür machen wir das gleich wie findByLastname, prüfen aber ob alle Studenten in zurückgegebener Liste sind, wenn lastname=null
        //given
        Gender genderEnum = Gender.FEMALE;

        Student student1 = prepareStudent("firstname1", "lastname1", genderEnum, "15.09.1937");
        Student student2 = prepareStudent("firstname2", "lastname2", genderEnum, "22.09.1978");
        Student student3 = prepareStudent("firstname3", "lastname3", Gender.MALE, "22.09.1978");

        create(student1);
        create(student2);
        create(student3);

        //when
        List<Student> result = dao.findAllByGender(genderEnum);

        //then
        assertThat(result.size(), is(2));
        assertThat(result, hasItems(student1, student2));
    }

    @Test
    public void findAllByGenderWithNullReturnsEmptyList() {
        //hierfür machen wir das gleich wie findByLastname, prüfen aber ob alle Studenten in zurückgegebener Liste sind, wenn lastname=null
        //given
        Gender genderEnum = Gender.FEMALE;

        Student student1 = prepareStudent("firstname1", "lastname1", genderEnum, "15.09.1937");
        Student student2 = prepareStudent("firstname2", "lastname2", genderEnum, "22.09.1978");
        Student student3 = prepareStudent("firstname3", "lastname3", Gender.MALE, "22.09.1978");

        create(student1);
        create(student2);
        create(student3);

        //when
        List<Student> result = dao.findAllByGender(null);

        //then
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void findAllBornBeforeReturnsMatchingEntities() {
        //given
        String birthdayString = "22.09.1978";

        Student student1 = prepareStudent("firstname1", "lastname1", Gender.FEMALE, "15.09.1937");
        Student student2 = prepareStudent("firstname2", "lastname2", Gender.FEMALE, "22.09.1978");
        Student student3 = prepareStudent("firstname3", "lastname3", Gender.MALE, "22.09.1980");
        Student student4 = prepareStudent("firstname4", "lastname4", Gender.MALE, null);

        create(student1);
        create(student2);
        create(student3);
        create(student4);

        //when
        List<Student> result = dao.findAllBornBefore(1977);

        //then
        assertThat(dao.findAllBornBefore(1980), hasItems(student1, student2));
        assertThat(dao.findAllBornBefore(1980).size(), is(2));
        assertThat(dao.findAllBornBefore(1981), hasItems(student1, student2, student3));
        assertThat(dao.findAllBornBefore(1981).size(), is(3));

        assertThat(dao.findAllBornBefore(1920).isEmpty(), is(true));

    }

    @After      //alles was nach dem Test gemacht werden soll
    public void tearDown(){
        //Die close-Methode soll am Ende jedes Tests aufgerufen werden
        // und sicherstellen, dass die verwendeten Ressourcen (EntityManager) aufgeräumt werden.
        //nach jedem Test wird aufgeräumt
        dao.close();
        if(manager.isOpen()){
            manager.close();
        }
        if(factory.isOpen()){
            factory.close();
        }
    }



}
