package at.ac.tuwien.infosys.jaxb;

import javax.jws.WebService;

@WebService(endpointInterface = "at.ac.tuwien.infosys.jaxb.PersonService", 
serviceName = "PersonService", 
targetNamespace = "http://com.pellcorp/service/PersonService")
public class PersonServiceImpl implements PersonService {
    public void save(Person data) {
    }
}
