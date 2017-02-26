package ddf.minim;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;

import ddf.minim.audio.AudioPlayer;
import ddf.minim.impl.Minim;

public class MP3DecodingITest
{
	boolean   	running;
	String 		fileFolder;
	Minim		minim;
	
	public static void main(String[] args)
	{
		MP3DecodingITest decoding = new MP3DecodingITest();
		
		decoding.Start(args);
	}
	
	void Start(String[] args)
	{
		fileFolder = args[0];
		
		minim = new Minim(this);
		
		AudioPlayer player = minim.loadFile( args[1] );
		
		if ( player != null )
		{
			player.play();
		
			while( player.isPlaying() )
			{
				continue;
			}
		}
	}

	public String sketchPath( String fileName )
	{
		return Paths.get( fileFolder, fileName ).toString();
	}
	
	public InputStream createInput( String fileName )
	{
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(sketchPath(fileName));
		}
		catch( FileNotFoundException ex )
		{
			System.err.println( "Unable to find file " + fileName );
		}
		
		return stream;
	}
}
