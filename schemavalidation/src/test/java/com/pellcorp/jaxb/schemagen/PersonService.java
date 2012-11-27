package com.pellcorp.jaxb.schemagen;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import at.ac.tuwien.infosys.jaxb.test.TestRequest;

import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;

@WebService(name = "PersonService", targetNamespace = "http://com.pellcorp/service/PersonService")
@SchemaValidation(type = SchemaValidationType.BOTH)
public interface PersonService {
    @WebMethod(operationName = "saveInheritEndpoint")
    void save(@WebParam(name = "Person") Person data);
    
    void foo(@WebParam(name = "TestRequest") TestRequest r);
}
