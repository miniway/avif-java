# avif java

Java AVIF decoder<br/>
wrapped [libavif](https://github.com/AOMediaCodec/libavif) by jna<br/>
based on https://github.com/AOMediaCodec/libavif/tree/main/android_jni

<img src="https://upload.wikimedia.org/wikipedia/commons/4/45/Avif-logo-rgb.svg" width="256"/>
<sub>© <a href="https://aomedia.org/av1/">AOM</a></sub>

## Install

 * install `libavif` e.g. `brew intall libavif`
 * https://jitpack.io/#miniway/avif-java
 * add `-Djna.library.path=/usr/local/lib` for jvm args or `-Djna.library.path=/Users/min/work/wef/avif-java/libs/Darwin/arm64/`

## Usage

```java
    BufferedImage image = ImageIO.read(Paths.get("/foo/bar.avif").toFile());
```


