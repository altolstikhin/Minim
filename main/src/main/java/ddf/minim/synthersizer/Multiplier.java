package ddf.minim.synthersizer;

import ddf.minim.mixer.impl.AbstractSynthersizer;

/**
 * Multiplier is a AbstractSynthersizer that will simply multiply the incoming signal by whatever 
 * its amplitude input is currently generating, which could be constant if 
 * nothing is patched to it.
 * 
 * @example Synthesis/multiplierExample
 * 
 * @author Damien Di Fede
 * 
 * @related AbstractSynthersizer
 *
 */

public class Multiplier extends AbstractSynthersizer
{
	/**
	 * The audio input is where incoming audio should be patched, but you can simply patch to the 
	 * Multiplier itself.
	 * 
	 * @related Multiplier
	 * @related AbstractSynthersizer.UGenInput
	 */  
	public UGenInput audio;
	
	/**
	 * The amplitude input allows you to control the value being used for multiplying with another AbstractSynthersizer.
	 * 
	 * @related Multiplier
	 * @related AbstractSynthersizer.UGenInput
	 */
	public UGenInput amplitude;
	
	/**
	 * Construct a Multiplier with a fixed value of 1, which will mean incoming audio is not changed.
	 *
	 */
	public Multiplier()
	{
		this( 1f );
	}
	
	/**
	 * Construct a Multiplier with a fixed value.
	 * 
	 * @param value
	 * 			float: the amplitude for the Multiplier
	 */
	public Multiplier( float value )
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super AbstractSynthersizer has been constructed
		//audio = new UGenInput(InputType.AUDIO);
		audio = new UGenInput(InputType.AUDIO);
		amplitude = new UGenInput(InputType.CONTROL);
		amplitude.setLastValue( value );
	}
	
	/**
	 * Set the amplitude of this Multiplier.
	 * 
	 * @param value
	 * 			float: the new amplitude for the Multiplier
	 * 
	 * @example Synthesis/multiplierExample
	 * 
	 * @related amplitude
	 * @related Multiplier
	 * @related AbstractSynthersizer
	 */
	public void setValue( float value )
	{
		amplitude.setLastValue( value );
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		for(int i = 0; i < channelCount(); i++)
		{
			channels[i] = amplitude.getLastValue() * audio.getLastValues()[i];
		}
	} 
}