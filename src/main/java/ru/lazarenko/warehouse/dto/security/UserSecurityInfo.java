package ru.lazarenko.warehouse.dto.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSecurityInfo {
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
}
