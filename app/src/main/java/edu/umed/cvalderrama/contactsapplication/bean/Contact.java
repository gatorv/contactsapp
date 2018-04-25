package edu.umed.cvalderrama.contactsapplication.bean;

/**
 * Contact Bean
 */
public class Contact {
    // Properties
    private long id;
    private String firstName;
    private String lastName;
    private String telephone;
    private String photoUri;

    /**
     * @return The contact Id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the Contact Id
     *
     * @param id The contact Id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return Return the contact first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the contact first name
     *
     * @param firstName The contact first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The contact last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the contact last name
     *
     * @param lastName The contact last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The contact telephone
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * @param telephone The contact Telephone
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * @return The contact Phone Uri
     */
    public String getPhotoUri() {
        return photoUri;
    }

    /**
     * Set the Contact Phone Uri
     * @param photoUri The phone Uri
     */
    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
