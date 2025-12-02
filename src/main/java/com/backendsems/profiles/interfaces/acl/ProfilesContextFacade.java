package com.backendsems.profiles.interfaces.acl;

/**
 * ProfilesContextFacade
 */
public interface ProfilesContextFacade {
    /**
     * Create a new profile
     * @param firstName The first name
     * @param lastName The last name
     * @param email The email address
     * @param street The street address
     * @param number The street number
     * @param city The city
     * @param postalCode The postal code
     * @param country The country
     * @return The profile ID
     */
    Long createProfile(String firstName, String lastName, String email, String password, String phone, String address);

    /**
     * Fetch a profile ID by email
     * @param email The email address
     * @return The profile ID
     */
    Long fetchProfileIdByEmail(String email);
}