package at.ac.tuwien.infosys.jaxb;

import javax.xml.bind.annotation.Facets;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(name = "Applicant")
@SuppressWarnings("all")
public class Applicant {
    private String firstName;
    private String lastName;
    private Gender gender;
    private Age age;

    public Applicant() {
    }

    @XmlElement(required = true, name = "age")
    public Age getAge() {
        return age;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    @XmlElement(required = true, name = "gender")
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @XmlElement(required = true, name = "firstName")
    @Facets(pattern = "[A-Z]+")
    @javax.xml.bind.annotation.MaxOccurs(value=2)
    @javax.xml.bind.annotation.MinOccurs(value=1)
    public String getFirstName() {
        return firstName;
    }

    @XmlElement(required = true, name = "lastName")
    @javax.xml.bind.annotation.MaxOccurs(value=3)
    @javax.xml.bind.annotation.MinOccurs(value=1)
    public String getLastName() {
        return lastName;
    }
}
