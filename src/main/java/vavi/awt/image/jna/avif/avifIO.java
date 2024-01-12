package vavi.awt.image.jna.avif;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import vavi.awt.image.jna.avif.AvifLibrary.avifIODestroyFunc;
import vavi.awt.image.jna.avif.AvifLibrary.avifIOReadFunc;
import vavi.awt.image.jna.avif.AvifLibrary.avifIOWriteFunc;
/**
 * <i>native declaration : /usr/local/libavif/include/avif/avif.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class avifIO extends Structure {
	/** C type : avifIODestroyFunc */
	public avifIODestroyFunc destroy;
	/** C type : avifIOReadFunc */
	public avifIOReadFunc read;
	/** C type : avifIOWriteFunc */
	public avifIOWriteFunc write;
	public long sizeHint;
	/** C type : avifBool */
	public int persistent;
	/** C type : void* */
	public Pointer data;
	public avifIO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("destroy", "read", "write", "sizeHint", "persistent", "data");
	}
	/**
	 * @param destroy C type : avifIODestroyFunc<br>
	 * @param read C type : avifIOReadFunc<br>
	 * @param write C type : avifIOWriteFunc<br>
	 * @param persistent C type : avifBool<br>
	 * @param data C type : void*
	 */
	public avifIO(avifIODestroyFunc destroy, avifIOReadFunc read, avifIOWriteFunc write, long sizeHint, int persistent, Pointer data) {
		super();
		this.destroy = destroy;
		this.read = read;
		this.write = write;
		this.sizeHint = sizeHint;
		this.persistent = persistent;
		this.data = data;
	}
	public avifIO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends avifIO implements Structure.ByReference {
		
	};
	public static class ByValue extends avifIO implements Structure.ByValue {
		
	};
}
