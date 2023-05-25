/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IPlayerManager.java
 * Last modified: 04/04/2023, 17:41
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

package pl.miloszgilga.audioplayer;

import net.dv8tion.jda.api.entities.VoiceChannel;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import pl.miloszgilga.dto.TrackPosition;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.MemberRemovedTracksInfo;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

interface IPlayerManager {
    void loadAndPlay(CommandEventWrapper event, String trackUrl, boolean isUrlPattern);
    void pauseCurrentTrack(CommandEventWrapper event);
    void resumeCurrentTrack(CommandEventWrapper event);
    AudioTrackInfo skipCurrentTrack(CommandEventWrapper event);
    void shuffleQueue(CommandEventWrapper event);
    void repeatCurrentTrack(CommandEventWrapper event, int countOfRepeats);
    boolean toggleInfiniteLoopCurrentTrack(CommandEventWrapper event);
    void setPlayerVolume(CommandEventWrapper event, int volume);
    AudioTrack skipToTrackPos(CommandEventWrapper event, int position);
    MemberRemovedTracksInfo removeTracksFromMember(CommandEventWrapper event, String memberId);
    boolean toggleInfinitePlaylistLoop(CommandEventWrapper event);
    VoiceChannel moveToMemberCurrentVoiceChannel(CommandEventWrapper event);
    AudioTrack moveTrackToSelectedPosition(CommandEventWrapper event, TrackPosition position);
    int clearQueue(CommandEventWrapper event);
}
