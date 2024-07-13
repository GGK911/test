// package videoTest;
//
// import lombok.SneakyThrows;
// import org.junit.jupiter.api.Test;
// import ws.schild.jave.Encoder;
// import ws.schild.jave.EncoderException;
// import ws.schild.jave.encode.AudioAttributes;
// import ws.schild.jave.encode.EncodingAttributes;
// import ws.schild.jave.encode.VideoAttributes;
//
// import java.io.File;
//
// /**
//  * @author TangHaoKai
//  * @version V1.0 2024/7/4 18:11
//  */
// public class test {
//     @Test
//     @SneakyThrows
//     public void test() {
//         File source = new File("path/to/your/input.mp4");
//         File target = new File("path/to/your/output_compressed.mp4");
//
//         // 设置音频属性
//         AudioAttributes audio = new AudioAttributes();
//         audio.setCodec("aac"); // 使用AAC音频编码
//         audio.setBitRate(128000); // 设置音频比特率为128kbps
//         audio.setChannels(2);
//         audio.setSamplingRate(44100);
//
//         // 设置视频属性
//         VideoAttributes video = new VideoAttributes();
//         video.setCodec("libx264"); // 使用H.264视频编码
//         video.setBitRate(1000000); // 设置视频比特率为1000kbps
//         video.setFrameRate(30); // 设置帧率为30fps
//
//         // 设置编码属性
//         EncodingAttributes attrs = new EncodingAttributes();
//         // attrs.setFormat("mp4");
//         attrs.setAudioAttributes(audio);
//         attrs.setVideoAttributes(video);
//
//         // 创建编码器
//         Encoder encoder = new Encoder();
//
//         try {
//             encoder.encode();
//             System.out.println("视频压缩成功: " + target.getAbsolutePath());
//         } catch (EncoderException e) {
//             e.printStackTrace();
//             System.err.println("视频压缩失败");
//         }
//     }
// }
