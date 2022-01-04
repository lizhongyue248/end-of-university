package wiki.zyue.eou.model.entity;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import wiki.zyue.eou.base.BaseEntity;

/**
 * 2021/12/28 04:58:55
 * @author echo
 */
@Entity
@Document
public class User extends BaseEntity<User> implements UserDetails  {

  private String password;

  private String phone;

  private String email;

  private Boolean isExpired = false;

  private Boolean isLocked = false;

  private Boolean isCredentialsExpired = false;

  @Transient
  private Collection<String> roles = emptyList();

  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream().map(SimpleGrantedAuthority::new).toList();
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return getName();
  }

  public boolean isAccountNonExpired() {
    return !isExpired;
  }

  public boolean isAccountNonLocked() {
    return !isLocked;
  }

  public boolean isCredentialsNonExpired() {
    return !isCredentialsExpired;
  }

  public boolean isEnabled() {
    return getEnable();
  }

  public User setPassword(String password) {
    this.password = password;
    return this;
  }

  public Boolean getExpired() {
    return isExpired;
  }

  public User setExpired(Boolean expired) {
    isExpired = expired;
    return this;
  }

  public Boolean getLocked() {
    return isLocked;
  }

  public User setLocked(Boolean locked) {
    isLocked = locked;
    return this;
  }

  public Boolean getCredentialsExpired() {
    return isCredentialsExpired;
  }

  public User setCredentialsExpired(Boolean credentialsExpired) {
    isCredentialsExpired = credentialsExpired;
    return this;
  }

  public Collection<String> getRoles() {
    return roles;
  }

  public User setRoles(Collection<String> roles) {
    this.roles = roles;
    return this;
  }

  public String getPhone() {
    return phone;
  }

  public User setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public User setEmail(String email) {
    this.email = email;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User user)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(getUsername(), user.getUsername()) && Objects.equals(
        getPassword(), user.getPassword()) && Objects.equals(getPhone(), user.getPhone())
        && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(
        isExpired, user.isExpired) && Objects.equals(isLocked, user.isLocked)
        && Objects.equals(isCredentialsExpired, user.isCredentialsExpired)
        && Objects.equals(getRoles(), user.getRoles());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getUsername(), getPassword(), getPhone(), getEmail(),
        isExpired, isLocked, isCredentialsExpired, getRoles());
  }

  @Override
  public String toString() {
    return "User{" +
        "password='" + password + '\'' +
        ", phone='" + phone + '\'' +
        ", email='" + email + '\'' +
        ", isExpired=" + isExpired +
        ", isLocked=" + isLocked +
        ", isCredentialsExpired=" + isCredentialsExpired +
        ", roles=" + roles +
        "} " + super.toString();
  }
}
