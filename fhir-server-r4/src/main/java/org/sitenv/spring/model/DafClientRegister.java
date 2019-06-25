package org.sitenv.spring.model;

import javax.persistence.*;


@Entity
@Table(name = "clients")
public class DafClientRegister {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    //@OneToOne(cascade=CascadeType.ALL)
    private Integer userId;

    @Column(name = "client_id")
    private String client_id;

    @Column(name = "client_secret")
    private String client_secret;

    @Column(name = "register_token")
    private String register_token;

    @Column(name = "name")
    private String name;

    @Column(name = "org_name")
    private String org_name;

    @Column(name = "contact_name")
    private String contact_name;

    @Column(name = "contact_mail")
    private String contact_mail;

    @Column(name = "scope")
    private String scope;

    @Column(name = "redirect_uri")
    private String redirect_uri;

    @Column(name = "status")
    private Boolean status;
    
    @Column(name = "launch_id")
    private String launchId;

    @Column(name = "launch_uri")
    private String launchUri;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getRegister_token() {
        return register_token;
    }

    public void setRegister_token(String register_token) {
        this.register_token = register_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getContact_mail() {
        return contact_mail;
    }

    public void setContact_mail(String contact_mail) {
        this.contact_mail = contact_mail;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

	public String getLaunchId() {
		return launchId;
	}

	public void setLaunchId(String launchId) {
		this.launchId = launchId;
	}

	public String getLaunchUri() {
		return launchUri;
	}

	public void setLaunchUri(String launchUri) {
		this.launchUri = launchUri;
	}

	

	

}
