package com.pellcorp.server;

import javax.jws.WebService;

@WebService(endpointInterface = "com.pellcorp.server.PersonService", 
    serviceName = "PersonService", 
    targetNamespace = "http://com.pellcorp/service/PersonService")
public class PersonServiceImpl implements PersonService {
    @Override
    public void save(Person data) {
    }
}
