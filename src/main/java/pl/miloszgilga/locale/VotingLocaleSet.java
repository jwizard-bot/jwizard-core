/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: VotingLocaleSet.java
 * Last modified: 04/04/2023, 18:45
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

package pl.miloszgilga.locale;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.miloszgilga.core.IEnumerableLocaleSet;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum VotingLocaleSet implements IEnumerableLocaleSet {

    ON_SUCCESS_VOTING                               ("jwizard.message.voting.VotingSuccess"),
    ON_FAILURE_VOTING                               ("jwizard.message.voting.VotingFailure"),
    ON_TIMEOUT_VOTING                               ("jwizard.message.voting.VotingTimeout"),
    VOTES_FOR_YES_NO_VOTING                         ("jwizard.message.voting.VotesForYesNo"),
    REQUIRED_TOTAL_VOTES_VOTING                     ("jwizard.message.voting.RequiredTotalVotes"),
    VOTES_RATIO_VOTING                              ("jwizard.message.voting.VotesRatio"),
    MAX_TIME_VOTING                                 ("jwizard.message.voting.MaxVotingTime"),
    TOO_FEW_POSITIVE_VOTES_VOTING                   ("jwizard.message.voting.TooFewPositiveVotes");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;
}
