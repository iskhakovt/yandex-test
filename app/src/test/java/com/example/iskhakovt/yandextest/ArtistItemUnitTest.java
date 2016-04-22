/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.text.TextUtils;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class ArtistItemUnitTest {
    @Before
    public void mockAndroidFrameWorkClasses() {
        // mock TextUtils
        mockStatic(TextUtils.class);
    }

    @Test
    public void testArtistItemGetters() throws Exception {
        final ArtistItem artistItem =new ArtistItem(
                "name", Arrays.asList("A", "B", "C"), 10, 20, "link", "description", "small url", "big url"
        );

        assertEquals(artistItem.getName(), "name");
        assertEquals(artistItem.getGenres(), Arrays.asList("A", "B", "C"));
        when(artistItem.getGenreString("")).thenReturn("ABC");
        when(artistItem.getGenreString(", ")).thenReturn("A, B, C");
        assertEquals(artistItem.getAlbums(), 10);
        assertEquals(artistItem.getTracks(), 20);
        assertEquals(artistItem.getLink(), "link");
        assertEquals(artistItem.getDescription(), "description");
        assertEquals(artistItem.getSmallCoverUrl(), "small url");
        assertEquals(artistItem.getBigCoverUrl(), "big url");
        assertEquals(artistItem.hasGenre("A"), true);
        assertEquals(artistItem.hasGenre("C"), true);
        assertEquals(artistItem.hasGenre("AB"), false);
        assertEquals(artistItem.hasGenre(""), false);
    }
}
