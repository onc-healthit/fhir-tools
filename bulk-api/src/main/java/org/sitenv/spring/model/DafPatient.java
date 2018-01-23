package org.sitenv.spring.model;

import javax.persistence.*;

/**
 * Created by Prabhushankar.Byrapp on 8/11/2015.
 */

@Entity
@Table(name = "patient")
public class DafPatient {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "identifier_system")
    private String identifierSystem;

    @Column(name = "identifier_value")
    private String identifierValue;

    @Column(name = "name")
    private String name;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifierSystem() {
        return identifierSystem;
    }

    public void setIdentifierSystem(String identifierSystem) {
        this.identifierSystem = identifierSystem;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
