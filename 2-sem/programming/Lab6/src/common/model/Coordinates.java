package common.model;

import common.exception.IncorrectInputException;
import common.util.NumbParser;
import common.Interface.Validation;
import java.util.Objects;

public class Coordinates implements Validation {
    private Integer x;
    private Float y;

    public Integer getX() { return x; }
    public Float getY() { return y; }

    public Coordinates setX(String input) throws IncorrectInputException {
        try {
            this.x = NumbParser.parseInt(input);
            if (x <= -258) throw new ArithmeticException();
        } catch (ArithmeticException | NumberFormatException e) {
            throw new IncorrectInputException("целое число > -258");
        }
        return this;
    }

    public Coordinates setY(String input) throws IncorrectInputException {
        try {
            String normalized = input.replace(',', '.');
            if (!normalized.matches("^-?\\d+\\.\\d+$")) {
                throw new IncorrectInputException("число с плавающей точкой (дробная часть обязательна), не более 3 цифр после запятой");
            }
            String fractional = normalized.split("\\.")[1];
            if (fractional.length() > 3) {
                throw new IncorrectInputException("число с плавающей точкой, не более 3 цифр после запятой");
            }
            this.y = NumbParser.parseFloat(input);
        } catch (NumberFormatException e) {
            throw new IncorrectInputException("число с плавающей точкой (дробная часть обязательна), не более 3 цифр после запятой");
        }
        return this;
    }

    public Coordinates copy() {
        Coordinates copy = new Coordinates();
        copy.x = this.x;
        copy.y = this.y;
        return copy;
    }

    @Override
    public boolean validation() {
        return x != null && x > -258 && y != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "X: " + x + ", Y: " + y;
    }
}