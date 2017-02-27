package ddf.minim.synthersizer;

import ddf.minim.mixer.impl.AbstractSynthersizer;


/**
 * Just outputs a constant value.
 * 
 * @example Synthesis/constantExample
 * 
 * @author Anderson Mills
 * 
 * @related AbstractSynthersizer
 *
 */
public class Constant extends AbstractSynthersizer
{
	private float value;
	
	/**
	 * Empty constructor for Constant.
	 * Sets value to 1.0.
	 */
	public Constant()
	{
		this( 1.0f );
	}
	
	/**
	 * Constructor for Constant.
	 * Sets value to val.
	 * @param val
	 * 			float: the constant value this will output
	 */
	public Constant( float val )
	{
		super();
		value = val;
	}
	
	/**
	 * Sets the value of the Constant during execution.
	 * 
	 * @param val
	 * 			float: the constant value this will output
	 * 
	 * @example Synthesis/constantExample
	 * 
	 * @related Constant
	 */
	public void setConstant( float val )
	{
		value = val;
	}

	@Override
	protected void uGenerate( float[] channels ) 
	{
		for(int i = 0; i < channels.length; i++)
		{
			channels[ i ] = value;
		}
	} 
}