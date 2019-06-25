package org.sitenv.spring.model;

import org.sitenv.spring.util.AESencryption;

import javax.persistence.*;


@Entity
@Table(name = "users")
public class DafUserRegister {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    @Column(name = "user_name", unique = true)
    private String user_name;

    @Column(name = "user_email", unique = true)
    private String user_email;

    @Column(name = "user_full_name")
    private String user_full_name;

    @Column(name = "user_password")
    private String user_password;
    
    
    @Column(name = "temp_password")
    private String tempPassword;
    
   
    @Column(name = "is_pass", nullable = false, columnDefinition="boolean default true")
    private boolean isPass;
	
	public boolean isPass() {
		return isPass;
	}

	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}

	public String getTempPassword() {
		return tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

    

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

   public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) throws Exception {
        this.user_password = AESencryption.encrypt(user_password);
    }

}
