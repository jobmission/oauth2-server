package com.revengemission.sso.oauth2.server.service.impl;

import com.revengemission.sso.oauth2.server.persistence.entity.RoleEntity;
import com.revengemission.sso.oauth2.server.persistence.entity.UserAccountEntity;
import com.revengemission.sso.oauth2.server.persistence.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccountEntity userAccountEntity = userAccountRepository.findByUsername(username);
        if (userAccountEntity != null) {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            if (userAccountEntity.getRoles() != null && userAccountEntity.getRoles().size() > 0) {
                for (RoleEntity temp : userAccountEntity.getRoles()) {
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(temp.getRoleName());
                    grantedAuthorities.add(grantedAuthority);
                }
            }
            return new User(userAccountEntity.getUsername(), userAccountEntity.getPassword(),
                userAccountEntity.getRecordStatus() >= 0, true, true, userAccountEntity.getRecordStatus() != -2, grantedAuthorities);
        } else {
            throw new UsernameNotFoundException(username + " not found!");
        }
    }
}
