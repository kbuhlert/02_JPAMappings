package at.campus02.dbp2.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {

    private EntityManager manager;

    public StudentDAOImpl(EntityManagerFactory factory){
        manager = factory.createEntityManager();
    }

    @Override
    public boolean create(Student student) {
        if(student == null)
            return false;

        if ((student.getId()) != null){
                return false;
            }           //-> Überprüfung der Randfälle

        manager.getTransaction().begin();
        manager.persist(student);   //persist = in Datenbank eintragen
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public Student update(Student student) {
        //abfrage ob Student = null ist (Objekt/Parameter nicht existiert) oder ob keine passende ID gefunden werden kann
        if(student == null || find(student.getId()) == null){
            return null;
        }
        manager.getTransaction().begin();
        Student updated = manager.merge(student);   //Objekt wird aus DB ausgelesen und ist somit detached. Mit merge wird Objekt wieder mit Datenbank verbunden, ansonsten wäre es keine gemanagte Entity
        manager.getTransaction().commit();
        return updated;
    }

    @Override
    public void delete(Student student) {

        if(student == null || find(student.getId()) == null){
            return;
        }
    manager.getTransaction().begin();
    Student removed = manager.merge(student);
    manager.remove(removed);
    manager.getTransaction().commit();
    }

    @Override
    public Student find(Integer id) {
        if(id==null)
            return null;
        return manager.find(Student.class, id);
    }

    @Override
    public List<Student> findAll() {
        String querystring = "SELECT s FROM Student s"; //typed query vom Typ student, erzeugt mit Querystring
        TypedQuery<Student> query = manager.createQuery(querystring, Student.class);    //wir müssen sagen welche Klasse wir zurück haben möchten
        return query.getResultList();
    }

    @Override
    public List<Student> findAllByLastname(String lastname) {   //brauchen Query mit Parameter (lastname), wir verwenden named Parameter (:Variablenname)
        if (lastname == null)
            return findAll();
        String querystring = "SELECT s FROM Student s WHERE upper(s.lastName) = upper(:lastname)";   //mach mit upper aus allen Studenten Studenten mit Großbuchstaben ->Vergleichbarkeit der Einträge
        TypedQuery<Student> query = manager.createQuery(querystring, Student.class);
        query.setParameter("lastname", lastname);
        return query.getResultList();
    }

    @Override
    public List<Student> findAllBornBefore(int year) {
        LocalDate firstDayOfYear = LocalDate.of(year, Month.JANUARY, 1);
        String querystring = "SELECT s FROM Student s WHERE s.birthday < :firstDayOfYear";
        TypedQuery<Student> query = manager.createQuery(querystring, Student.class);
        query.setParameter("firstDayOfYear", firstDayOfYear);

        return query.getResultList();
    }

    @Override
    public List<Student> findAllByGender(Gender gender) {
        if (gender == null)
            return Collections.EMPTY_LIST;

       // String querystring = "SELECT s FROM Student s WHERE s.gender = :gender";
        // TypedQuery<Student> query = manager.createQuery(querystring, Student.class);

        //Alternativer Query mit dem NamedQuery aus der Klasse Student:
        TypedQuery<Student> query = manager.createNamedQuery("Student.findAllByGender", Student.class);
        query.setParameter("gender", gender);
        return query.getResultList();
    }

    @Override
    public void close() {
        if(manager!= null && manager.isOpen())
            manager.close();
    }
}
