//package com.example.audioutils.show.AudioFx;
//
//import android.media.audiofx.Visualizer;
//
///**
// * Title:<br>
// * Description: <br>
// *
// * @email ItZheng@ZoHo.com
// * Created by itzheng on 2018-3-23.
// */
//public class ddd {
//    public void test() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (mediaPlayer.isPlaying()) {
//
//                    try {
//                        Visualizer visualizer = new Visualizer(mediaPlayer.getAudioSessionId());     //mediaPlayer.getAudioSessionId()=1538 1540 1542...
//                        visualizer.setEnabled(false);
//                        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]); /** 64,128,256,512,1024 */  //前面若没有setEnabled(false)  会在此出异常
//                        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
//                            //采集波形数据
//                            @Override
//                            public void onWaveFormDataCapture(Visualizer visualizer, byte[] wave, int samplingRate) {
//
//                            }
//
//                            //采集FFT数据   bytes数组大小=
//                            @Override
//                            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
//                                //   直流      实部 虚部      频率范围 0-采样率/2 getSamplingRate()
//                                //    0    1    2    3    4    5       n-2       n-1        n=getCaptureSize()
//                                //    R0  Rn/2 R1   L1    R2   L2    R(n-1)/2  L(n-1)/2
//                                //   k次频率 = getSamplingRate() * k / (n/2)                   int getFft (byte[] fft)
//                                int frequencyCounts = bytes.length / 2 + 1; //  =513
//                                byte[] fft = new byte[frequencyCounts];    //  (n-2)/2+2 = n/2+1  容量 = getCaptureSize()/2+
//                                fft[0] = (byte) Math.abs(bytes[0]);
//                                for (int i = 1; i < frequencyCounts - 1; i++) {
//                                    fft[i] = (byte) Math.hypot(bytes[2 * i], bytes[2 * i + 1]);
//                                }
//                                fft[frequencyCounts - 1] = (byte) Math.abs(bytes[1]);
//                                int frequencyEach = samplingRate * 2 / visualizer.getCaptureSize();  //86132  samplingRate=44,100,000 mHz  getCaptureSize()=1024
//                                //fft[k]   频谱分为 getCaptureSize()/2份(不包括0直流)    k(Hz)=getSamplingRate()*k/(getCaptureSize()/2)    高度为8位字节
//                                for (int i = 0; i < 17; i++) {
//                                    //WaveHeight[i] = 5 * (int) fft[2*i*i];  // 2*16*16=512    0 2 8 18 ... 450 512
//                                    WaveHeight[i] = 5 * (int) fft[i];  // 2*16*16=512    0 2 8 18 ... 450 512   幅值与音量值非线性
//                                    //if (currentVolume<=0)
//                                    //{
//                                    //currentVolume = 1;
//                                    //}
//                                    //WaveHeight[i] = 30 * (int) fft[i] / currentVolume;  // 2*16*16=512    0 2 8 18 ... 450 512
//                                    if (WaveHeight[i] < 0) {
//                                        WaveHeight[i] = -WaveHeight[i];
//                                    }
//                                    if (WaveHeight[i] > 390) {
//                                        WaveHeight[i] = 390;
//                                    }
//                                }
//                            }
//                        }, Visualizer.getMaxCaptureRate() / 2, false, true); //rate(采样周期 mHz)  isWave  isFFT
//                        visualizer.setEnabled(true);
//                    } catch (Exception e) {
//                        e.printStackTrace();//这里没有WaveHeight异常
//                    }
//                }
//            }
//        }).start();
//
//    }
//        catch(
//    Exception e)
//
//    {
//        flagTest = 233;
//        e.printStackTrace();
//    }
//}
//}
