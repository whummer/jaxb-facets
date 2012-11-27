package com.pellcorp.jaxb.schemagen;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import at.ac.tuwien.infosys.jaxb.test.TestRequest;

@WebService(endpointInterface = "com.pellcorp.jaxb.schemagen.PersonService", 
    serviceName = "PersonService", 
    targetNamespace = "http://com.pellcorp/service/PersonService")
public class PersonServiceImpl implements PersonService {
    @Override
    public void save(Person data) {
    }

    @Override
    @SOAPBinding(parameterStyle=ParameterStyle.BARE)
    public void foo(TestRequest r) {
        // TODO Auto-generated method stub
        
    }
}
