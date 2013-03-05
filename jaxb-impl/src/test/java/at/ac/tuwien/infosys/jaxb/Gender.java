package at.ac.tuwien.infosys.jaxb;

import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@Documentation(value = "Gender Type")
@XmlEnum
@SuppressWarnings("all")
public enum Gender {
    @XmlEnumValue(value = "F")
    @Documentation(value = "Female")
    F,

    @XmlEnumValue(value = "M")
    @Documentation(value = "Male")
    M,

    @XmlEnumValue(value = "O")
    @Documentation(value = "Other")
    O
}
