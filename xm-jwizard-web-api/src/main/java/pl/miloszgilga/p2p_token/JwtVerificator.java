/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JwtVerificator.java
 * Last modified: 07/04/2023, 14:47
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

package pl.miloszgilga.p2p_token;

import lombok.RequiredArgsConstructor;

import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

import org.jmpsl.security.jwt.JwtService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
@RequiredArgsConstructor
public class JwtVerificator {

    private final JwtService jwtService;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String validateUserAndExtractEmail(String token) {
        final Optional<Claims> extractedClaims = jwtService.extractClaims(token);
        if (extractedClaims.isEmpty()) return StringUtils.EMPTY;
        final Claims claims = extractedClaims.get();
        return claims.get(Claims.ISSUER, String.class);
    }
}
