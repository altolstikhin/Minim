package ddf.minim.synthersizer;

import ddf.minim.mixer.impl.AbstractSynthersizer;

/**
 * <p>
 * The Bypass AbstractSynthersizer allows you to wrap another AbstractSynthersizer and then insert that AbstractSynthersizer into your
 * signal chain using Bypass in its place. You can then dynamically route the 
 * audio through the wrapped AbstractSynthersizer or simply allow incoming audio to pass through unaffected. 
 * Using a Bypass AbstractSynthersizer allows you to avoid concurrency issues caused by patching and unpatching 
 * during runtime from a Thread other than the audio one.
 * </p>
 * <p>
 * Your usage of Bypass might look something like this:
 * </p>
 * <pre>
 * Bypass&lt;GranulateSteady&gt; granulate = new Bypass( new GranulateSteady() );
 * filePlayer.patch( granulate ).patch( mainOut );
 * </pre>
 * <p>
 * If you needed to patch something else to one of the inputs of the GranulateSteady,
 * you'd use the <code>ugen</code> method of Bypass to retrieve the wrapped AbstractSynthersizer
 * and operate on it:
 * </p>
 * <pre>
 * grainLenLine.patch( granulate.ugen().grainLen );
 * </pre>
 * <p>
 * Now, calling the <code>activate</code> method will <em>bypass</em> the granulate effect 
 * so that the Bypass object outputs the audio that is coming into it. Calling the 
 * <code>deactivate</code> method will route the audio through the wrapped effect. The 
 * <code>isActive</code> method indicates whether or not the wrapped effect is currently 
 * being bypassed.
 * </p>
 * 
 * @author Damien Di Fede
 *
 * @param <T> The type of AbstractSynthersizer being wrapped, like GranulateSteady.
 * 
 * @related AbstractSynthersizer
 * 
 * @example Synthesis/bypassExample
 */

public class Bypass<T extends AbstractSynthersizer> extends AbstractSynthersizer 
{
	private T mUGen;
	// do NOT allow people to patch directly to this!
	private UGenInput audio;
	
	private boolean mActive;
	
	/**
	 * Construct a Bypass AbstractSynthersizer that wraps a AbstractSynthersizer of type T.
	 * 
	 * @param ugen
	 * 			the AbstractSynthersizer that this can bypass
	 */
	public Bypass( T ugen )
	{
		mUGen 	= ugen;
		audio 	= addAudio();
		mActive = false;
	}
	
	/**
	 * Retrieve the AbstractSynthersizer that this Bypass is wrapping.
	 * 
	 * @return the wrapped AbstractSynthersizer, cast to the class this Bypass was constructed with.
	 * 
	 * @example Synthesis/bypassExample
	 * 
	 * @related Bypass
	 */
	public T ugen() 
	{
		return mUGen;
	}
	
	@Override
	protected void sampleRateChanged()
	{
		mUGen.setSampleRate( sampleRate() );
	}
	
	@Override
	protected void addInput( AbstractSynthersizer input )
	{
		audio.setIncomingUGen( input );
		input.patch( mUGen );
	}
	
	@Override
	protected void removeInput( AbstractSynthersizer input )
	{
		if ( audio.getIncomingUGen() == input )
		{
			audio.setIncomingUGen(null);
			input.unpatch( mUGen );
		}
	}
	
	public void setChannelCount( int channelCount )
	{
	  // this will set our audio input properly
	  super.setChannelCount(channelCount);
	  
	  // but we also need to let our wrapped AbstractSynthersizer know
	  mUGen.setChannelCount(channelCount);
	}
	
	/**
	 * Activate the bypass functionality. In other words, the wrapped AbstractSynthersizer will NOT
	 * have an effect on the AbstractSynthersizer patched to this Bypass.
	 * 
	 * @example Synthesis/bypassExample
	 * 
	 * @related Bypass
	 */
	public void activate()
	{
		mActive = true;
	}
	
	/**
	 * Deactivate the bypass functionality. In other words, the wrapped AbstractSynthersizer WILL 
	 * have an effect on the AbstractSynthersizer patched to this Bypass, as if it was in the 
	 * signal chain in place of this Bypass.
	 * 
	 * @example Synthesis/bypassExample
	 * 
	 * @related Bypass
	 */
	public void deactivate()
	{
		mActive = false;
	}
	
	/**
	 * Find out if this Bypass is active or not.
	 * 
	 * @return true if the bypass functionality is on.
	 * 
	 * @example Synthesis/bypassExample
	 * 
	 * @related Bypass
	 */
	public boolean isActive()
	{
		return mActive;
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		mUGen.tick(channels);
		
		// but stomp the result if we are active
		if ( mActive )
		{
			System.arraycopy(audio.getLastValues(), 0, channels, 0, channels.length);
		}
	}

}
