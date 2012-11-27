package at.ac.tuwien.infosys.jaxb;

import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@Documentation(value="Gender Type")
@XmlEnum
public enum Gender {
    @Documentation(value="Female")
    @XmlEnumValue(value = "F")
    F, 
    
    @Documentation(value="Male")
    @XmlEnumValue(value = "M")
    M, 
    
    @Documentation(value="Other")
    @XmlEnumValue(value = "O")
    O
}
