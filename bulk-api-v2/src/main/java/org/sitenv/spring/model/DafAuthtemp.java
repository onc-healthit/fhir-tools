package org.sitenv.spring.model;


import javax.persistence.*;

@Entity
@Table(name = "auth_temp")
public class DafAuthtemp {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "client_id")
    private String client_id;

    @Column(name = "client_secret")
    private String client_secret;

    @Column(name = "auth_code")
    private String authCode;

    @Column(name = "access_token")
    private String access_token;

    @Column(name = "aud")
    private String aud;

    @Column(name = "expiry")
    private String expiry;

    @Column(name = "scope")
    private String scope;

    @Column(name = "state")
    private String state;

    @Column(name = "redirect_uri")
    private String redirect_uri;

    @Column(name = "transaction_id")
    private String transaction_id;

    @Column(name = "refresh_token")
    private String refresh_token;

    @Column(name = "launch_patient_id")
    private Integer launchPatientId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Integer getLaunchPatientId() {
        return launchPatientId;
    }

    public void setLaunchPatientId(Integer launchPatientId) {
        this.launchPatientId = launchPatientId;
    }

}
