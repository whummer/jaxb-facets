package at.ac.tuwien.infosys.jaxb;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;

import at.ac.tuwien.infosys.jaxb.test.TestRequest;

@WebService(name = "PersonService")
@SchemaValidation(type = SchemaValidationType.BOTH)
@SuppressWarnings("all")
public interface PersonServiceNoNS {
    @WebMethod(operationName = "save")
    void save(@WebParam(name = "Person") Person data);
    
    void foo(@WebParam(name = "TestRequest") TestRequest r);
}
