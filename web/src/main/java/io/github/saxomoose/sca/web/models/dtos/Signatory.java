package io.github.saxomoose.sca.web.models.dtos;

import java.time.LocalDate;

public class Signatory {
    private final String firstName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private final String email;
    private final String nationalRegistrationNumber;

    public Signatory(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String email,
        String nationalRegistrationNumber) 
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.nationalRegistrationNumber = nationalRegistrationNumber;
    }

    public String firstName() 
    {
        return firstName;
    }

    public String lastName() 
    {
        return lastName;
    }

    public LocalDate dateOfBirth()
    {
        return dateOfBirth;
    }

    public String email() 
    {
        return email;
    }

    public String nationalRegistrationNumber() 
    {
        return nationalRegistrationNumber;
    }
}
