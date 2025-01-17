package vavi.awt.image.jna.avif;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
/**
 * <i>native declaration : /usr/local/libavif/include/avif/avif.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class avifDiagnostics extends Structure {
	/** C type : char[256] */
	public byte[] error = new byte[256];
	public avifDiagnostics() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("error");
	}
	/** @param error C type : char[256] */
	public avifDiagnostics(byte error[]) {
		super();
		if ((error.length != this.error.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.error = error;
	}
	public avifDiagnostics(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends avifDiagnostics implements Structure.ByReference {
		
	};
	public static class ByValue extends avifDiagnostics implements Structure.ByValue {
		
	};
}
