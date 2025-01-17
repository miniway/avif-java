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
public class avifCleanApertureBox extends Structure {
	public int widthN;
	public int widthD;
	public int heightN;
	public int heightD;
	public int horizOffN;
	public int horizOffD;
	public int vertOffN;
	public int vertOffD;
	public avifCleanApertureBox() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("widthN", "widthD", "heightN", "heightD", "horizOffN", "horizOffD", "vertOffN", "vertOffD");
	}
	public avifCleanApertureBox(int widthN, int widthD, int heightN, int heightD, int horizOffN, int horizOffD, int vertOffN, int vertOffD) {
		super();
		this.widthN = widthN;
		this.widthD = widthD;
		this.heightN = heightN;
		this.heightD = heightD;
		this.horizOffN = horizOffN;
		this.horizOffD = horizOffD;
		this.vertOffN = vertOffN;
		this.vertOffD = vertOffD;
	}
	public avifCleanApertureBox(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends avifCleanApertureBox implements Structure.ByReference {
		
	};
	public static class ByValue extends avifCleanApertureBox implements Structure.ByValue {
		
	};
}
