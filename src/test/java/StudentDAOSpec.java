import at.campus02.dbp2.jpa.Gender;
import at.campus02.dbp2.jpa.Student;
import at.campus02.dbp2.jpa.StudentDAO;
import at.campus02.dbp2.jpa.StudentDAOImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.text.html.parser.Entity;

import static org.hamcrest.CoreMatchers.*;  //der Teil, wird bei Verwendung dieser Klasse immmer automatisch zigefügt
import static org.junit.Assert.assertThat;

public class StudentDAOSpec {
    //Klasse für Tests, Tests sind Methoden der Klasse die ausgeführt werden
    //Strukturierung der tests in 3 Blöckke given(Vorraussetzungen die gebraucht werden um Test durchzuführen,
    // when (was getestet werden soll), then (überprüfen ob funktioniert)


   //wenn Test nicht berücksichtigt werden soll, dann kann mit der @Ignore Annotation der Test ausgelassen werden.
    @Test
    public void ensureThatToUpperCaseResultsInUpperCaseLetters() {
        //given
        String str1 = "string";

        //when
        //hier Test ob to Upper Case funktioniert hat

        String result = str1.toUpperCase();

        //then
        Assert.assertThat(result, is("STRING"));
    }

    @Test
    public void createNullAsStudentReturnsFalse(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("nameOfJpaPersistenceUnit");

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
       EntityManagerFactory factory = Persistence.createEntityManagerFactory("nameOfJpaPersistenceUnit");

        Student student = new Student();
        student.setLastName("Lastname");
        student.setFirstName("Firstname");
        student.setGender(Gender.FEMALE);
        StudentDAO dao = new StudentDAOImpl(factory);

        //when
        boolean result = dao.create(student);

        //then
        assertThat(result, is(true));
        //überprüfen, ob der Student in der Datenbank existiert

        EntityManager manager = factory.createEntityManager();
        Student fromDB = manager.find(Student.class, student.getId());
        assertThat(fromDB, is(student));

    }


}
