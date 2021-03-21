package vn.siplab.medical.education.common.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.siplab.medical.education.common.security.DefaultUserPrincipal;

public abstract class AbstractBaseEnvService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  final protected Logger getLogger() {
    return logger;
  }

  @Autowired(required = false)
  private RoleHierarchy roleHierarchy;

  protected DefaultUserPrincipal getUserPrincipal() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return null;
    }

    return (DefaultUserPrincipal) authentication.getPrincipal();
  }

  protected String getUsername() {

    DefaultUserPrincipal userPrincipal = getUserPrincipal();

    return userPrincipal == null ? null : userPrincipal.getUsername();
  }

  protected Long getUserId() {
    DefaultUserPrincipal userPrincipal = getUserPrincipal();

    return userPrincipal == null ? null : userPrincipal.getId();
  }

  public final boolean hasAuthority(String authority) {
    return hasAnyAuthority(authority);
  }

  public final boolean hasAnyAuthority(String... authorities) {
    return hasAnyAuthorityName(null, authorities);
  }

  public final boolean hasRole(String role) {
    return hasAnyRole(role);
  }

  public final boolean hasAnyRole(String... roles) {
    return hasAnyAuthorityName("ROLE_", roles);
  }

  private boolean hasAnyAuthorityName(String prefix, String... roles) {
    Set<String> roleSet = getAuthoritySet();

    for (String role : roles) {
      String defaultedRole = getRoleWithDefaultPrefix(prefix, role);
      if (roleSet.contains(defaultedRole)) {
        return true;
      }
    }

    return false;
  }

  private Set<String> getAuthoritySet() {
    DefaultUserPrincipal user = getUserPrincipal();

    if (user == null) {
      return new HashSet<>();
    }

    Collection<? extends GrantedAuthority> userAuthorities = user.getAuthorities();

    if (roleHierarchy != null) {
      userAuthorities = roleHierarchy.getReachableGrantedAuthorities(userAuthorities);
    }

    return AuthorityUtils.authorityListToSet(userAuthorities);
  }

  private String getRoleWithDefaultPrefix(String defaultRolePrefix, String role) {
    if (role == null) {
      return null;
    }

    if (defaultRolePrefix == null || defaultRolePrefix.length() == 0) {
      return role;
    }

    if (role.startsWith(defaultRolePrefix)) {
      return role;
    }

    return defaultRolePrefix + role;
  }
}
