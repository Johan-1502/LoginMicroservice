package co.edu.uptc.login.login_microservice.user.domain;

import co.edu.uptc.login.login_microservice.shared.domain.ValueObject;

public class UserId extends ValueObject{
    private final Long value;

    public UserId(Long value) { this.value = value; }

    public static UserId of(Long value) {
        return value == null ? null : new UserId(value);
    }

    public Long value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        UserId other = (UserId) o;
        return value != null && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
