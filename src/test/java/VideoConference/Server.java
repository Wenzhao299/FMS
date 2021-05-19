package VideoConference;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class Server {

    private int port=9998; //传输的端口
    private InputStream in; //输入流
    private OutputStream out;//输出流
    private DataOutputStream dataout;
    private DataInputStream datain;
    ArrayList<String> message;//存储客户端发来的信息，包括登录名和密码;
    ArrayList<Socket> socketarray=new ArrayList<>();//存储客户端socket信息

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Server ss=new Server();
        ss.initial();
    }

    //初始化，对端口开始监听
    public void initial() throws IOException, NoSuchAlgorithmException {
        //对端口开始监听
        ServerSocket server=new ServerSocket(port);
        System.out.println("开始侦听端口"+port);
        //阻塞
        while(true) {
            Socket client=server.accept();
            System.out.println("端口监听有连接"+port);
            invoke(client);
            socketarray.add(client);//将连接的socket对象加入队列
        }

    }

    //创建线程去处理新的连接
    public void invoke(final Socket client) throws IOException, NoSuchAlgorithmException {
       // datain =new DataInputStream(in);
        //dataout=new DataOutputStream(out);
        //final boolean[] ship = new boolean[1];


         new Thread(new Runnable() {
             @Override
             public void run() {
                 try {
                     message=new ArrayList<>();
                     in=client.getInputStream();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 try {
                     out=client.getOutputStream();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 datain =new DataInputStream(in);
                 dataout=new DataOutputStream(out);
                 final boolean[] ship = new boolean[1];
                // int count=0;
                 //处理登录和注册业务
                 while (true) {
                     //System.out.println(count++);
                     String str="";
                     try {
                         str = datain.readUTF();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }


                     System.out.println("客户端传来的数据是" + str);
                     message.add(str);
                     if(message.size()==3) break;
                 }

                 System.out.println("执行新的的语句");

                 if (message.get(0).equals("注册")) {

                     try {
                         if (postin(message)== true) {
                             dataout.writeUTF("true");
                             dataout.flush();
                             System.out.println("注册成功");
                             ship[0] =true;
                         }
                         else {
                             dataout.writeUTF("false");
                             dataout.flush();
                             delete_socket(socketarray, client);
                             System.out.println("注册失败");
                             ship[0] =false;
                         }
                     } catch (NoSuchAlgorithmException | IOException e) {
                         e.printStackTrace();
                     }

                 }

                 else if (message.get(0).equals("登录")) {

                     try {
                         if (login(message)==true) {
                             dataout.writeUTF("true");
                             dataout.flush();
                             System.out.println("登录成功");
                             ship[0] =true;
                         }
                         else {
                             delete_socket(socketarray, client);
                             dataout.writeUTF("false");
                             dataout.flush();
                             System.out.println("登录失败");

                             ship[0] =false;
                         }
                     } catch (NoSuchAlgorithmException | IOException e) {
                         e.printStackTrace();
                     }
                 }

                 else {
                     delete_socket(socketarray, client);
                     System.out.println("失败");
                     message.clear();

                 }


                 //处理客户端的视频流
                System.out.println("开始处理客户端的视频流");
                 System.out.println(socketarray.size());

                if(ship[0]==true ){
                     try {
                         while(true) {
                             if(socketarray.size()>1) {
                                 get_video(client, socketarray);
                             }
                             else{
                                 clear_video(client,socketarray);
                             }
                         }
                     } catch (IOException e) {
                         //e.printStackTrace();
                         //System.out.println("错误获取视频流");
                     }
                 }
             }

         }).start();

    }

    public boolean login(ArrayList<String> message) throws NoSuchAlgorithmException {  //登录
        Database db=new Database();
        String name=message.get(1);
        String password=message.get(2);
        if(db.IsCustomer(name)) {

            if (db.customer.get(name).equals(db.getMd5(password))) {
                return true;
            } else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    public boolean postin(ArrayList<String> message) throws NoSuchAlgorithmException { //注册
        Database db=new Database();
        String name=message.get(1);
        String password=db.getMd5(message.get(2));
        boolean result=db.AddCustomer(name,password);
        return result;
    }

    public boolean delete_socket(ArrayList<Socket> array,Socket socket) {
        boolean result = false;
        if (array.size() == 0)
            return false;
        else {
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).equals(socket)) {
                    array.remove(i);//将该socket对象删除
                    result = true;
                }
            }
            return result;
        }
    }

    public void get_video(Socket socket,ArrayList<Socket> array) throws IOException {  //得到客户端发送的加密数据流
        DataInputStream din=new DataInputStream(socket.getInputStream());
              String[][] video={};

                int width = din.readInt();
                int hight = din.readInt();
                if(width!=176||hight!=144) {
                    width = 176;
                    hight = 144;
                }
                //System.out.println("width"+width+"height"+hight);
                //int count=0;
                if(width>0&&hight>0) {
                    int count=0;
                    video = new String[width][hight];
                    //System.out.println("获取加密视频流");
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < hight; j++) {
                            video[i][j] = din.readUTF();
                            /*
                            count++;
                            if(count<10) {
                                System.out.println(video[i][j]);
                            }
                            */
                    }
                }

                for(int i=0;i<array.size();i++){
                    if(!socket.equals(array.get(i))){
                        Socket socketn=array.get(i);
                        int finalWidth = width;
                        int finalHight = hight;
                        String[][] finalVideo1=new String[finalWidth][finalHight] ;

                        for (int k = 0; k < finalWidth; k++) {
                            for (int m = 0; m < finalHight; m++) {
                                finalVideo1[k][m] = video[k][m];
                            }
                        }
                        try {
                            send_video(socketn, finalWidth, finalHight, finalVideo1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
    }

    public void send_video(Socket socketn,int width,int hight,String[][] video) throws IOException{//发送给其他用户端得到加密数据流
        //此socket是其余客户端的socket
            DataOutputStream dout=new DataOutputStream(socketn.getOutputStream());
            dout.writeInt(width);
            dout.writeInt(hight);
            for(int i=0;i<width;i++){
                for(int j=0;j<hight;j++){
                    if(video[i][j]!=null) {
                        dout.writeUTF(video[i][j]);
                        //System.out.println(video[i][j]);
                        dout.flush();
                    }
                }
            }
    }

    public void clear_video(Socket socket,ArrayList<Socket> array) throws IOException {  //得到客户端发送的加密数据流
        DataInputStream din = new DataInputStream(socket.getInputStream());
        String[][] video = {};

        int width = din.readInt();
        int hight = din.readInt();
        if (width != 176 || hight != 144) {
            width = 176;
            hight = 144;
        }

        video = new String[width][hight];
        //System.out.println("获取加密视频流");
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < hight; j++) {
                din.readUTF();
            }
        }

    }





}