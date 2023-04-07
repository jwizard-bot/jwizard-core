/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AuthUserDetailsService.java
 * Last modified: 07/04/2023, 14:51
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL COPIES OR
 * SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
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
