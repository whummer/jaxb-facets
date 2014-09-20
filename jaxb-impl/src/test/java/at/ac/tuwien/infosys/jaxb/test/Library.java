package at.ac.tuwien.infosys.jaxb.test;

import java.util.List;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Library {
    @XmlElementWrapper(name = "loans")
    @XmlElement(name = "loan")
    private List<Loan> loans;

    @XmlElementWrapper(name = "loansWithISBN")
    @XmlElement(name = "loanWithISBN")
    private List<LoanWithISBN> loansWithIsbn;

    @XmlElementWrapper(name = "books")
    @XmlElement(name = "book")
    private List<Book> books;

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Book {
        @XmlID
        @XmlAttribute(required = true)
        @Pattern(regexp = ISB_REGEX)
        private String isbn;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Loan {
        @XmlIDREF
        @XmlAttribute(required = true)
        @Pattern(regexp = ISB_REGEX)
        private Book book;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class LoanWithISBN {
        @XmlIDREF
        @XmlAttribute(required = true, name = "isbn")
        @Pattern(regexp = ISB_REGEX)
        private Book book;
    }

    private static final String ISB_REGEX = "ISB-(\\d{3})-\\d-(\\d{2})-(\\d{6})-\\1";
}
