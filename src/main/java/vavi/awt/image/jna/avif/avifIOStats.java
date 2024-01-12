package vavi.awt.image.jna.avif;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
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
public class avifIOStats extends Structure {
	public NativeSize colorOBUSize;
	public NativeSize alphaOBUSize;
	public avifIOStats() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("colorOBUSize", "alphaOBUSize");
	}
	public avifIOStats(NativeSize colorOBUSize, NativeSize alphaOBUSize) {
		super();
		this.colorOBUSize = colorOBUSize;
		this.alphaOBUSize = alphaOBUSize;
	}
	public avifIOStats(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends avifIOStats implements Structure.ByReference {
		
	};
	public static class ByValue extends avifIOStats implements Structure.ByValue {
		
	};
}
