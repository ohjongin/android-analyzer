This plugin collects data on available cameras and their capabilities.

**Plugin Name:** Camera Plugin

**Class Name:** `org.androidanalyzer.plugins.camera.CameraPlugin`

The following table defines the structure of data being inserted in the report, and the APIs or methods used to retrieve the value.

| **Node** | **Node** | **Node** | **Node** | **Node** | **Node** | **Value** | **Metric** | **Retrieval Method** |
|:---------|:---------|:---------|:---------|:---------|:---------|:----------|:-----------|:---------------------|
| Cameras |  |  |  |  |  |  |  |  |
|      | Camera-`<x>` |  |  |  |  |  |  |  |
|      |  | Location |  |  |  | _string_ (e.g. _front_, _back_ |  | Unknown, N/A |
|      |  | Resolution |  |  |  | _double_ | MP | API1: `Camera.Parameters.getPictureSize()`<br>API5: <code>Camera.Parameters.getSupportedPictureSizes()</code> <br>
<tr><td>      </td><td>  </td><td> Flash </td><td>  </td><td>  </td><td>  </td><td>  </td><td>  </td><td>  </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Supported </td><td>  </td><td>  </td><td> <i>boolean</i> (Y, N) </td><td>  </td><td> <code>Camera.Parameters.getSupportedFlashModes()</code> </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Capabilities </td><td>  </td><td>  </td><td>  </td><td>  </td><td>  </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td> Flash type </td><td>  </td><td>  </td><td>  </td><td> Unknown, N/A  </td></tr>
<tr><td>      </td><td>  </td><td> Image </td><td>  </td><td>  </td><td>  </td><td>  </td><td>  </td><td>  </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Self timer </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Unknown, N/A </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Focus modes </td><td>  </td><td>  </td><td> <i>string</i> (comma-separated list of values: <i>auto</i>, <i>edof</i>, <i>fixed</i>, <i>infinity</i>, <i>macro</i>)</td><td>  </td><td> <code>Camera.Parameters.getSupportedFocusModes()</code> </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Zoom </td><td>  </td><td>  </td><td> <i>boolean</i> (Y, N) </td><td>  </td><td> <code>Camera.Parameters.isZoomSupported()</code> </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Geotagging </td><td>  </td><td>  </td><td> <i>boolean</i> (Y, N) </td><td>  </td><td> Unknown, N/A </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Hand jitter reduction </td><td>  </td><td>  </td><td> <i>boolean</i> (Y, N) </td><td>  </td><td> Unknown, N/A </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Red eye reduction</td><td>  </td><td>  </td><td> <i>boolean</i> (Y, N) </td><td>  </td><td> Check if <code>Camera.Parameters.getSupportedFlashModes()</code> returns <i>red-eye</i></td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Face recognition </td><td>  </td><td>  </td><td> <i>boolean</i> (Y, N) </td><td>  </td><td> Unknown, N/A </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Dynamic range optimization </td><td>  </td><td>  </td><td> <i>boolean</i> (Y, N) </td><td>  </td><td> Unknown, N/A </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Formats </td><td>  </td><td>  </td><td> <i>string</i> (comma-separated list of values: <i>JPEG</i>, <i>NV16</i>, <i>NV21</i>, <i>RGB_565</i>, <i>YUY2</i>) </td><td>  </td><td> Map each value from <code>Camera.Parameters.getSupportedPictureFormats()</code> to <i>string</i> represantation </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Supported resolutions </td><td>  </td><td>  </td><td> <i>string</i> (comma-separated list of WxH values, e.g. <i>320x240</i>, <i>240x320</i>, etc.) </td><td>  </td><td> <code>Camera.Parameters.getSupportedPictureSizes()</code> </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Supported compression ratios </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Unknown, N/A </td></tr>
<tr><td>      </td><td>  </td><td> Video </td><td>  </td><td>  </td><td>  </td><td>  </td><td>  </td><td>  </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Self timer </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Unknown, N/A </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Flash / movie light </td><td>  </td><td>  </td><td> <i>boolean</i> (Y, N) </td><td>  </td><td> Check if <code>Camera.Parameters.getSupportedFlashModes()</code> returns <i>torch</i></td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Zoom </td><td>  </td><td>  </td><td> <i>boolean</i> (Y, N) </td><td>  </td><td> Unknown, N/A </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> White balance / brightness </td><td>  </td><td>  </td><td> <i>string</i> (comma-separated list of values: <i>auto</i>, <i>cloudy-daylight</i>, <i>daylight</i>, <i>fluorescent</i>, <i>incandescent</i>, <i>shade</i>, <i>twilight</i>, <i>warm-fluorescent</i>) </td><td>  </td><td> <code>Camera.Parameters.getSupportedWhiteBalance()</code> </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Color tone </td><td>  </td><td>  </td><td> <i>string</i> (comma-separated list of values: <i>none</i>, <i>posterize</i>, <i>sepia</i>, <i>solarize</i>, <i>whiteboard</i>) </td><td>  </td><td> <code>Camera.Parameters.getSupportedColorEffects()</code> </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Self-portrait video mode </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Unknown, N/A </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Long video capture </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Unknown, N/A </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Other capabilities</td><td>  </td><td>  </td><td>  </td><td>  </td><td> Unknown, N/A  </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Formats </td><td>  </td><td>  </td><td>  </td><td>  </td><td>  </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td> MPEG-4 </td><td>  </td><td> boolean (Y, N) </td><td>  </td><td> <code>android.os.SystemProperties.get("ro.media.enc.file.format")</code> </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td> 3GPP </td><td>  </td><td> boolean (Y, N) </td><td>  </td><td> <code>android.os.SystemProperties.get("ro.media.enc.file.format")</code> </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td> 3GP2 </td><td>  </td><td> boolean (Y, N) </td><td>  </td><td> <code>android.os.SystemProperties.get("ro.media.enc.file.format")</code> </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Video encoding </td><td>  </td><td>  </td><td>  </td><td>  </td><td>  </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td> <code>&lt;codec&gt;</code> </td><td>  </td><td>  </td><td>  </td><td> <code>android.os.SystemProperties.get("ro.media.enc.vid.codec")</code> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Supported resolutions </td><td> string (comma-separated list of WxH values, e.g. 320x240, 240x320, etc.) </td><td>  </td><td> <code>android.os.SystemProperties.get("ro.media.enc.vid.&lt;codec&gt;.width")</code><br><code>android.os.SystemProperties.get("ro.media.enc.vid.&lt;codec&gt;.height")</code> <br> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Minimum framerate </td><td> integer </td><td> fps </td><td> <code>android.os.SystemProperties.get("ro.media.enc.vid.&lt;codec&gt;.fps")</code> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Maximum framerate </td><td> integer </td><td> fps </td><td> <code>android.os.SystemProperties.get("ro.media.enc.vid.&lt;codec&gt;.fps")</code> <br> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Minimum bitrate </td><td> integer </td><td> bps </td><td> <code>android.os.SystemProperties.get("ro.media.enc.vid.&lt;codec&gt;.bps")</code> <br> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Maximum bitrate </td><td> integer </td><td> bps </td><td> <code>android.os.SystemProperties.get("ro.media.enc.vid.&lt;codec&gt;.bps")</code> <br> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td> Audio encoding </td><td>  </td><td>  </td><td>  </td><td>  </td><td>  </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td> <code>&lt;codec&gt;</code> </td><td>  </td><td>  </td><td>  </td><td> <code>android.os.SystemProperties.get("ro.media.enc.vid.codec")</code> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Minimum bitrate </td><td> integer </td><td> bps </td><td> <code>android.os.SystemProperties.get("ro.media.enc.vid.&lt;codec&gt;.bps")</code> <br> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Maximum bitrate </td><td> integer </td><td> bps </td><td> <code>android.os.SystemProperties.get("ro.media.enc.vid.&lt;codec&gt;.bps")</code> <br> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Minimum frequency </td><td> integer </td><td> Hz </td><td> <code>android.os.SystemProperties.get("ro.media.enc.aud.&lt;codec&gt;.hz")</code> <br> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Maximum frequency </td><td> integer </td><td> Hz </td><td> <code>android.os.SystemProperties.get("ro.media.enc.aud.&lt;codec&gt;.hz")</code> <br> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Minimum channels </td><td> integer </td><td> channels </td><td> <code>android.os.SystemProperties.get("ro.media.enc.aud.&lt;codec&gt;.ch")</code> or parse /system/build.prop </td></tr>
<tr><td>      </td><td>  </td><td>  </td><td>  </td><td>  </td><td> Maximum channels </td><td> integer </td><td> channels </td><td> <code>android.os.SystemProperties.get("ro.media.enc.aud.&lt;codec&gt;.ch")</code> <br> or parse /system/build.prop </td></tr></tbody></table>


Note: for each present camera a separate Camera-<code>&lt;x&gt;</code> node is created, <code>&lt;x&gt;</code> starting from 0. <br><br>

<code>&lt;codec&gt;</code> is the string representation of a codec name (e.g h264,m4v,h263,aac,amrnb etc. )<br>
)