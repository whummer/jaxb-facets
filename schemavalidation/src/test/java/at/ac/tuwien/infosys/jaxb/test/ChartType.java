package at.ac.tuwien.infosys.jaxb.test;

import javax.xml.bind.annotation.Documentation;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@Documentation(value="Choose Chart Type")
@XmlEnum
public enum ChartType {
    @Documentation(value="Line Graph")
    @XmlEnumValue(value = "line")
    line, 
    
    @Documentation(value="Bar Graph")
    @XmlEnumValue(value = "bar")
    bar, 
    
    @Documentation(value="Pie Graph")
    @XmlEnumValue(value = "pie")
    pie
}
