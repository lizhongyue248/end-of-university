package wiki.zyue.eou.model.entity;

import static java.util.Collections.emptyList;

import java.util.Collection;
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
public class User extends BaseEntity<User>  {

  private String username;

  private String password;

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
    return username;
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

  public User setUsername(String username) {
    this.username = username;
    return this;
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

  @Override
  public String toString() {
    return "User{" +
        "username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", isExpired=" + isExpired +
        ", isLocked=" + isLocked +
        ", isCredentialsExpired=" + isCredentialsExpired +
        ", roles=" + roles +
        '}';
  }
}
