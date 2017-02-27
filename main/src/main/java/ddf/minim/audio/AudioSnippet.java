/*
 * Copyright (c) 2007 - 2008 by Damien Di Fede <ddf@compartmental.net>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as published
 * by the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Library General Public License for more details.
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package ddf.minim.audio;

import ddf.minim.mixer.Playable;
import ddf.minim.mixer.impl.Controller;
import ddf.minim.spi.AudioRecording;

/**
 * <code>AudioSnippet</code> is a simple wrapper around a JavaSound
 * <code>Clip</code> (It isn't called AudioClip because that's an interface
 * defined in the package java.applet). It provides almost the exact same
 * functionality, the main difference being that length, position, and cue are
 * expressed in milliseconds instead of microseconds. You can obtain an
 * <code>AudioSnippet</code> by using {@link Minim#loadSnippet(String)}. One
 * of the limitations of <code>AudioSnippet</code> is that you do not have
 * access to the audio samples as they are played. However, you are spared all
 * of the overhead associated with making samples available. An
 * <code>AudioSnippet</code> is a good choice if all you need to do is play a
 * short sound at some point. If your aim is to repeatedly trigger a sound, you
 * should use an {@link AudioSample} instead.
 * 
 * @author Damien Di Fede
 */

/** @deprecated */
@Deprecated
public class AudioSnippet
        extends Controller
        implements Playable
{
    private AudioRecording recording;


    public AudioSnippet(AudioRecording rec)
    {
        super(rec.getControls());
        rec.open();
        recording = rec;
    }


    @Override
    public void play()
    {
        recording.play();
    }


    @Override
    public void play(int millis)
    {
        cue(millis);
        play();
    }


    @Override
    public void pause()
    {
        recording.pause();
    }


    @Override
    public void rewind()
    {
        cue(0);
    }


    @Override
    public void loop()
    {
        // recording.loop(Minim.LOOP_CONTINUOUSLY);
        recording.loop(-1);
    }


    @Override
    public void loop(int n)
    {
        recording.loop(n);
    }


    @Override
    public int loopCount()
    {
        return recording.getLoopCount();
    }


    @Override
    public int length()
    {
        return recording.getMillisecondLength();
    }


    @Override
    public int position()
    {
        return recording.getMillisecondPosition();
    }


    @Override
    public void cue(int millis)
    {
        if (millis < 0)
            millis = 0;
        if (millis > length())
            millis = length();
        recording.setMillisecondPosition(millis);
    }


    @Override
    public void skip(int millis)
    {
        int pos = position() + millis;
        if (pos < 0)
            pos = 0;
        else if (pos > length())
            pos = length();
        recording.setMillisecondPosition(pos);
    }


    @Override
    public boolean isLooping()
    {
        return recording.getLoopCount() != 0;
    }


    @Override
    public boolean isPlaying()
    {
        return recording.isPlaying();
    }


    /**
     * Closes the snippet so that any resources it is using can be released. This
     * should be called when you are finished using this snippet.
     */
    public void close()
    {
        recording.close();
    }


    @Override
    public AudioMetaData getMetaData()
    {
        return recording.getMetaData();
    }


    @Override
    public void setLoopPoints(int start, int stop)
    {
        recording.setLoopPoints(start, stop);
    }
}
