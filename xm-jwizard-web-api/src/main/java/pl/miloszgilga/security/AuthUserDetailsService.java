/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AuthUserDetailsService.java
 * Last modified: 07/04/2023, 15:32
 * Project name: jwizard-discord-bot
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.miloszgilga.security;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.jmpsl.security.SecurityUtil;
import org.jmpsl.security.user.AuthUser;
import org.jmpsl.security.user.SimpleGrantedRole;

import pl.miloszgilga.domain.member.MemberEntity;
import pl.miloszgilga.domain.member.IMemberRepository;

import static pl.miloszgilga.exception.AuthException.UserNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final IMemberRepository repository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public AuthUser<SimpleGrantedRole> loadUserByUsername(String discordId) throws UsernameNotFoundException {
        final MemberEntity member = repository.findByDiscordId(discordId).orElseThrow(() -> {
            log.error("Unable to load user with credentials data (discordId): {}", discordId);
            return new UserNotFoundException();
        });
        return SecurityUtil.fabricateUser(member);
    }
}
