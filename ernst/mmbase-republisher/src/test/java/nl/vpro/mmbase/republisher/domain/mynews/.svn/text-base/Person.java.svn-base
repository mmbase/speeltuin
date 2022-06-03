package nl.vpro.mmbase.republisher.domain.mynews;

import java.util.List;

import nl.vpro.mmbase.vob.annotations.Entity;
import nl.vpro.mmbase.vob.annotations.Rel;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@Entity(builder = "people")
public class Person {
    private int number;
    
	public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    private String firstname;
	private String lastname;
	private String email;
	private String account;

	@Rel()
	private List<Image> images;

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}

}
