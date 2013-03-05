package at.ac.tuwien.infosys.jaxb;

import javax.jws.WebService;

import at.ac.tuwien.infosys.jaxb.test.TestRequest;

@WebService(endpointInterface = "at.ac.tuwien.infosys.jaxb.PersonServiceNoNS", 
serviceName = "PersonService")
@SuppressWarnings("all")
public class PersonServiceNoNSImpl implements PersonServiceNoNS {
    public void save(Person data) {
    }

    public void foo(TestRequest r) {
    }
}
