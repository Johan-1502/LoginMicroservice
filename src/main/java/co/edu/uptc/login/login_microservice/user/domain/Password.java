package co.edu.uptc.login.login_microservice.user.domain;

import java.util.Objects;

import co.edu.uptc.login.login_microservice.shared.domain.ValueObject;

public class Password extends ValueObject {
    private final String value;

    private Password(String value) {
        this.value = value;
    }

    public static Password of(String value) {
        return new Password(value);
    }

    public String getValue() {
        return value;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Password that = (Password) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
