package at.campus02.dbp2.jpa;

import java.util.List;

public interface StudentDAO {
    //DAO = Data Access Object

    boolean create(Student student);
    Student update(Student student);
    void delete(Student student);
    Student find(Integer id);

    List<Student> findAll();
    List<Student> findAllByLastname(String lastname);
    List<Student> findAllBornBefore(int year);
    List<Student> findAllByGender(Gender gender);

    void close();

}
