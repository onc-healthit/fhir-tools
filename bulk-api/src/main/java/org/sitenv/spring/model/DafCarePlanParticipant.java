package org.sitenv.spring.model;


import javax.persistence.*;

@Entity
@Table(name = "careplan_participant")
public class DafCarePlanParticipant {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //@OneToMany(cascade = CascadeType.ALL)
    //@JoinColumn(name="careteam")
    @Column(name = "careteam")
    private int careteam;

    @Column(name = "role_system")
    private String role_system;

    @Column(name = "role_code")
    private String role_code;

    @Column(name = "role_display")
    private String role_display;

    @Column(name = "member_reference")
    private String member_reference;

    @Column(name = "member_display")
    private String member_display;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCareteam() {
        return careteam;
    }

    public void setCareteam(int careteam) {
        this.careteam = careteam;
    }

    public String getRole_system() {
        return role_system;
    }

    public void setRole_system(String role_system) {
        this.role_system = role_system;
    }

    public String getRole_code() {
        return role_code;
    }

    public void setRole_code(String role_code) {
        this.role_code = role_code;
    }

    public String getRole_display() {
        return role_display;
    }

    public void setRole_display(String role_display) {
        this.role_display = role_display;
    }

    public String getMember_reference() {
        return member_reference;
    }

    public void setMember_reference(String member_reference) {
        this.member_reference = member_reference;
    }

    public String getMember_display() {
        return member_display;
    }

    public void setMember_display(String member_display) {
        this.member_display = member_display;
    }

}
