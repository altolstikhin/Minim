package ddf.minim;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioFormat;

import ddf.minim.spi.AudioResource;

public enum Channels
{

    MONO("Center"),
    STEREO("Left", "Right");

    private static final Map<Integer, Channels> CHANNELS = new HashMap<>();
    static
    {
        for (int x = 0; x < Channels.values().length; x++)
        {
            CHANNELS.put(Channels.values()[x].no_of_channels, Channels.values()[x]);
        }
    }

    private final int no_of_channels;
    private final Set<String> channels;


    /**
     * @param channelNames
     */
    Channels(String... channelNames)
    {
        channels = new HashSet<>(Arrays.asList(channelNames));
        no_of_channels = channels.size();

    }


    /**
     * @param audioResource
     * @return
     */
    public static Channels channels(AudioResource audioResource)
    {
        return channels(audioResource.getFormat());
    }


    /**
     * @param audioFormat
     * @return
     */
    public static Channels channels(AudioFormat audioFormat)
    {
        return channels(audioFormat.getChannels());
    }


    /**
     * @param channelCount
     * @return
     */
    public static Channels channels(int channelCount)
    {
        return CHANNELS.get(channelCount);
    }
}
