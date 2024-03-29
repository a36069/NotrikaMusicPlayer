/*
 * Copyright (c) 2019 mohamamd hussin abdi.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.abdipor.music1.model.smartplaylist;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.abdipor.music1.R;
import com.abdipor.music1.loaders.TopAndRecentlyPlayedTracksLoader;
import com.abdipor.music1.model.Song;
import com.abdipor.music1.providers.HistoryStore;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class HistoryPlaylist extends AbsSmartPlaylist {

    public static final Creator<HistoryPlaylist> CREATOR = new Creator<HistoryPlaylist>() {
        public HistoryPlaylist createFromParcel(Parcel source) {
            return new HistoryPlaylist(source);
        }

        public HistoryPlaylist[] newArray(int size) {
            return new HistoryPlaylist[size];
        }
    };

    public HistoryPlaylist(@NonNull Context context) {
        super(context.getString(R.string.history), R.drawable.ic_access_time_white_24dp);
    }

    protected HistoryPlaylist(Parcel in) {
        super(in);
    }

    @Override
    public void clear(@NonNull Context context) {
        HistoryStore.getInstance(context).clear();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NotNull @NonNull Context context) {
        return TopAndRecentlyPlayedTracksLoader.INSTANCE.getRecentlyPlayedTracks(context);
    }
}
