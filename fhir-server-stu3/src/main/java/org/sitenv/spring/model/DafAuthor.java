package org.sitenv.spring.model;

import javax.persistence.*;

/**
 * Created by Prabhushankar.Byrapp on 8/11/2015.
 */

@Entity
@Table(name = "author")
public class DafAuthor {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
