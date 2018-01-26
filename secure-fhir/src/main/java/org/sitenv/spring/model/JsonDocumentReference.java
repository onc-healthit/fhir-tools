package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;

/**
 * Created by Prabhushankar.Byrapp on 8/19/2015.
 */
@Entity
@Table(name = "document_reference_json")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class JsonDocumentReference {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "master_dentifier")
    @Type(type = "StringJsonObject")
    private String masterIdentifier;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMasterIdentifier() {
        return masterIdentifier;
    }

    public void setMasterIdentifier(String masterIdentifier) {
        this.masterIdentifier = masterIdentifier;
    }
}
