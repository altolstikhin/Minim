package ddf.minim.synthersizer;

import ddf.minim.mixer.impl.AbstractSynthersizer;

/**
 * The TickRate AbstractSynthersizer can be used to control the generation rate of the
 * AbstractSynthersizer that is patched to it. It is equivalent to slowing down or 
 * speeding up a record player. With a rate of 1, the patched AbstractSynthersizer 
 * will come through normally, but 0.5 will half as fast and sound 
 * an octave lower, whereas 2 will be twice as fast and sound an 
 * octave higher. Be aware that increasing the rate comes at the 
 * expense of more computation because the patched AbstractSynthersizer will be 
 * ticked multiple times in one sample frame. Another limitation 
 * is that a AbstractSynthersizer patched to a TickRate should not be patched to 
 * another AbstractSynthersizer as well, the audio output will not be what you intend.
 * 
 * @example Synthesis/tickRateExample
 * 
 * @author Damien Di Fede
 *
 */
public class TickRate extends AbstractSynthersizer 
{
	private AbstractSynthersizer audio;
	
	/**
	 * The rate that this ticks the AbstractSynthersizer patched to it.
	 * With a rate of 1, the patched AbstractSynthersizer 
	 * will come through normally, but 0.5 will half as fast and sound 
	 * an octave lower, whereas 2 will be twice as fast and sound an 
	 * octave higher. 
	 * 
	 * @shortdesc The rate that this ticks the AbstractSynthersizer patched to it.
	 * 
	 * @example Synthesis/tickRateExample
	 * 
	 * @related TickRate
	 */
	public UGenInput value;
	
	private float[] currentSample;
	private float[] nextSample;
	private float   sampleCount;
	private boolean	bInterpolate;
	
	/**
	 * Constructs a TickRate. 
	 * The default rate is 1.
	 * 
	 * @example Synthesis/tickRateExample
	 * 
	 * @related TickRate
	 */
	public TickRate()
	{
		this( 1.f );
	}
	
	/**
	 * Constructs a TickRate.
	 * 
	 * @example Synthesis/tickRateExample
	 * 
	 * @param tickRate
	 * 				float: the rate at which to tick a AbstractSynthersizer patched to this
	 * 
	 * @related TickRate
	 * 
	 */
	public TickRate( float tickRate )
	{
		value = new UGenInput(InputType.CONTROL);
		value.setLastValue(tickRate);
		sampleCount = 0.f;
		currentSample = new float[2];
		nextSample = new float[2];
		bInterpolate = false;
	}
	
	/**
	 * Enabled or disable sample interpolation.
	 * When the rate is less than 1, that means 
	 * that TickRate will need to generate more 
	 * output sample frames than it gets from the
	 * AbstractSynthersizer patched to it. With interpolation turned 
	 * on, it will create these in-between sample frames 
	 * by interpolating between subsequent sample frames 
	 * generated by the patched AbstractSynthersizer. With interpolation 
	 * turned off, this simply generates the most recent 
	 * sample frame generated by the patch AbstractSynthersizer until it's 
	 * time to generate a new one. The result can be "crunchy" 
	 * sounding, with crunchiness increasing as the rate is reduced.
	 * Interpolation is turned off be default because TickRate 
	 * is more computationally expensive with it on.
	 * 
	 * @shortdesc Enabled or disable sample interpolation.
	 * 
	 * @example Synthesis/tickRateExample
	 * 
	 * @param doInterpolate
	 * 			boolean: whether or not this TickRate should interpolate
	 * 
	 * @related TickRate
	 */
	public final void setInterpolation( boolean doInterpolate )
	{
		bInterpolate = doInterpolate;
	}
	
	/**
	 * Returns whether or not this TickRate currently has interpolation on.
	 * 
	 * @return boolean: is this TickRate interpolating
	 * 
	 * @related TickRate
	 */
	public final boolean isInterpolating()
	{
	    return bInterpolate;
	}
	
	@Override
	protected void addInput( AbstractSynthersizer in )
	{
		audio = in;
		audio.setChannelCount(currentSample.length);
	}
	
	@Override
	protected void removeInput( AbstractSynthersizer in )
	{
		if ( audio == in )
		{
			audio = null;
		}
	}
	
	@Override
	protected void sampleRateChanged()
	{
		if ( audio != null )
		{
			audio.setSampleRate(sampleRate());
		}
	}
	
	@Override
	protected void channelCountChanged()
	{		
		currentSample = new float[channelCount()];
		nextSample = new float[channelCount()];
	
		if ( audio != null )
		{
			audio.setChannelCount(channelCount());
			audio.tick(currentSample);
			audio.tick(nextSample);
			sampleCount = 0;
		}
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		float sampleStep = value.getLastValue();
		
		// for 0 or negative rate values, we just stop generating audio
		// effectively pausing generation of the patched ugen.
		if ( sampleStep <= 0.f )
		{
			for(int i = 0; i < channels.length; ++i)
			{
				channels[i] = 0.f;
			}
			
			return;
		}
		
		if ( bInterpolate )
		{
			for(int i = 0; i < channels.length; ++i)
			{
				float sampleDiff = nextSample[i] - currentSample[i];
				channels[i] = currentSample[i] + sampleDiff * sampleCount;
			}
		}
		else
		{
			System.arraycopy(currentSample, 0, channels, 0, channels.length);
		}
		
		if ( audio != null )
		{
			sampleCount += sampleStep;
			
			while( sampleCount >= 1.f )
			{
				System.arraycopy(nextSample, 0, currentSample, 0, nextSample.length);
				audio.tick(nextSample);
				sampleCount -= 1.f;
			}
		}
	}

}
