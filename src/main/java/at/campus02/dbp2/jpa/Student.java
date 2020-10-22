package at.campus02.dbp2.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.time.LocalDate;
import java.util.Objects;

@Entity //Annotation, dass diese Klasse als Entität gemappt werden soll
@NamedQuery(name = "Student.findAllByGender", query = "select s from  Student s where s.gender = :gender")
public class Student {
        // automatisch generierte technische ID, PrimaryKey

        @Id
        @GeneratedValue //Vergibt Autoincrement als PrimaryKey
        private Integer id;     //Integer statt int, int würde mit 0 initialisiert werden, da 0 nicht null sein kann, dass könnte zum falschen Eintrag in DB führen
        private String firstName;
        private String lastName;
        private LocalDate birthday;
        // enum mit 2 Werten: MALE und FEMALE
        private Gender gender;

        public Integer getId() {
                return id;
        }
        //kein Setter für ID, diese soll nur aus Datenbank kommen

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

        public LocalDate getBirthday() {
                return birthday;
        }

        public void setBirthday(LocalDate birthday) {
                this.birthday = birthday;
        }

        public Gender getGender() {
                return gender;
        }

        public void setGender(Gender gender) {
                this.gender = gender;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;     //zuerst vergleich der Referenzen, sind die gleich muss nicht weiter geschaut werden
                if (o == null || getClass() != o.getClass()) return false;      //Überprüfung der Klassen, nur wenn die passen kann im nächsten Schritt gecastet werden
                Student student = (Student) o;          //Da Methode bereits existiert (vorgeschrieben ist) und nur überschriebene wird, wird ein allgemeines Objekt reingegeben, wir brauchen aber ein Objekt unserer Klasse (Studen, deshalb wird hier gecastet
                return Objects.equals(id, student.id) &&
                        Objects.equals(firstName, student.firstName) &&
                        Objects.equals(lastName, student.lastName) &&
                        Objects.equals(birthday, student.birthday) &&
                        gender == student.gender;
        }

        @Override
        public int hashCode() {
                return Objects.hash(id, firstName, lastName, birthday, gender);
        }
}
