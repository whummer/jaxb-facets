package at.ac.tuwien.infosys.jaxb;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name = "PersonService", targetNamespace = "http://com.pellcorp/service/PersonService")
public interface PersonService {
    @WebMethod(operationName = "saveInheritEndpoint")
    void save(@WebParam(name = "Person") Person data);
}
