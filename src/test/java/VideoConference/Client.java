package VideoConference;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

import javax.swing.*;

public class Client {
    private InputStream in;  //输入输出流，数据输入输出流
    private OutputStream out;
    private DataInputStream datain;
    private DataOutputStream dataout;
    private  JPanel JP;
    
//    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//        Client cc=new Client();
//        cc.Get_acception(client, g, JP1);
//    }


    public void Get_acception(Socket client, Graphics g,JPanel JP1) {  //获取与服务器的连接
        try {
            //Socket client=new Socket("192.186.31.172",9998);//与服务器的9998建立连接
            in=client.getInputStream();
            out=client.getOutputStream();//得到服务端的输入输出流
            datain=new DataInputStream(in);
            dataout=new DataOutputStream(dataout);
            Graphics gg=g;
            JP=JP1;


            while(true){

                int[][] video=get_decrypt_video(datain);//解密视频流
                if(video.length>0) {
                    drawvideo(video, gg,JP);
                }
//                try {
////                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void send_secret_video(BufferedImage image,DataOutputStream dataout_){ //发送加密的视频流
        try{
            int width=image.getWidth();//获取图片的底
            int height=image.getHeight();//获取图片的高
            //System.out.println(width+"client"+height);
            if(width!=176||height!=144) {
                width = 176;
                height = 144;
            }
            dataout_.writeInt(width);  //发送图片大小
            dataout_.flush();
            dataout_.writeInt(height);
            dataout_.flush();

            for(int i=0;i<width;i++){
                for(int j=0;j<height;j++){
                    String str=RC4_Encryption.encryRC4String(image.getRGB(i,j)+"", "123","UTF-8");
                    //System.out.println(str);
                    dataout_.writeUTF(str);//加密视频流的每个像素
                    dataout_.flush();
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public int[][] get_decrypt_video(DataInputStream datain_) throws IOException {//将获得的视频流解密
        int width=datain_.readInt();
        int height=datain_.readInt();
        int video[][]={};
        if(width>0&&height>0) {
            if(width!=176 && height!=144){
                width=176;
                height=144;
            }
            video= new int[width][height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    String str=datain_.readUTF();
                    if(str!=null) {
                        String str1=RC4_Encryption.decryRC4(str, "123", "UTF-8");
                        video[i][j] = Integer.parseInt(str1); //视频解密

                    }

                }
            }
        }
        return video;
    }

    public void drawvideo(int[][] video, Graphics gg,JPanel JP){  //画出图像
        BufferedImage buffer = new BufferedImage(video.length,video[0].length,BufferedImage.TYPE_INT_RGB);
        //获取缓存画布
        Graphics bufferg = gg;

        for (int i=0;i<video.length;i++) {
            for (int j=0;j<video[0].length;j++) {
                Color color = new Color(video[i][j]);
                bufferg.setColor(color);
                gg.drawLine(i, j, i, j);
            }
        }

        //bufferg.drawImage(buffer,40 ,40 , this.JP);
    }

}
