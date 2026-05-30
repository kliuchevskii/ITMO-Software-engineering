package common.model;

import common.exception.IncorrectInputException;
import common.Interface.Validation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Person implements Validation {
    private LocalDateTime birthday;
    private Color eyeColor;
    private Color hairColor;
    private Country nationality;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public LocalDateTime getBirthday() { return birthday; }
    public Color getEyeColor() { return eyeColor; }
    public Color getHairColor() { return hairColor; }
    public Country getNationality() { return nationality; }

    public void setBirthday(String birthday) throws IncorrectInputException {
        if (birthday == null || birthday.isBlank() || birthday.equalsIgnoreCase("Null") || birthday.equalsIgnoreCase("Nl")) {
            this.birthday = null;
        } else {
            try {
                this.birthday = LocalDateTime.parse(birthday, formatter);
            } catch (DateTimeParseException e) {
                throw new IncorrectInputException("dd-MM-yyyy HH:mm:ss");
            }
        }
    }

    public void setEyeColor(String eyeColor) throws IncorrectInputException {
        this.eyeColor = Color.getColor(eyeColor);
    }

    public void setHairColor(String hairColor) throws IncorrectInputException {
        this.hairColor = Color.getColor(hairColor);
    }

    public void setNationality(String nationality) {
        if (nationality.equals("Null") || nationality.equals("Nl")) this.nationality = null;
        else {
            this.nationality = Country.getCountry(nationality);
        }
    }

    public Person copy() {
        Person copy = new Person();
        copy.birthday = this.birthday;
        copy.eyeColor = this.eyeColor;
        copy.hairColor = this.hairColor;
        copy.nationality = this.nationality;
        return copy;
    }

    @Override
    public boolean validation() {
        return this.eyeColor != null && this.hairColor != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(birthday, person.birthday) && eyeColor == person.eyeColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthday, eyeColor);
    }

    @Override
    public String toString() {
        return "Person:" +
                "\nBirthday: " + (birthday != null ? birthday.format(DateTimeFormatter.ISO_LOCAL_DATE) : "null") +
                "\nEye color: " + eyeColor +
                "\nHair color: " + hairColor +
                "\nNationality: " + nationality;
    }
}