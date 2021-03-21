package vn.siplab.medical.education.common.security;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Setter
@Getter
public class DefaultUserPrincipal implements UserDetails, Serializable {

  private Long id;

  private String username;

  private String fullName;

  private String email;

  private String password;

  private boolean isEnabled;

  private List<String> privileges;

  private Long exp = null;

  private Long iat = null;

  private String jti = null;

  private Map<String, Object> additionalInformation;

  public DefaultUserPrincipal() {
    this.privileges = new ArrayList<>();
  }

  @Override
  public List<GrantedAuthority> getAuthorities() {
    if (privileges != null) {
      return AuthorityUtils.createAuthorityList(privileges.toArray(new String[0]));
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DefaultUserPrincipal that = (DefaultUserPrincipal) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
