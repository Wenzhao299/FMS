package VideoConference;


import com.github.sarxos.webcam.Webcam;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

//视频通话的UI类
public class UI extends JFrame{
    private Graphics g,g1;
    private JFrame JF;
    private JPanel JP1;
    private Webcam webcam;
    public boolean[] colsed = new boolean[1];

    JPanel p1 = new JPanel();
    JPanel p2 = new JPanel();
    JPanel p3 = new JPanel();
    Label lbName = new Label("对方用户名：");
    Button btnExit = new Button("退出视频");

    //创建客户端的UI
    public void CreateUI(Socket socket) throws IOException {
        JF = new JFrame("安全视频会议");   //创建一个窗体
        JF.setSize(800, 700);    //设置窗体大小
        JF.setLocation(560,120);
        JF.setVisible(true);  //设置可见


        JF.add(p1,"North");
        p1.setPreferredSize(new Dimension(800, 100));
        Border borUser = BorderFactory.createTitledBorder("视频信息");
        p1.setBorder(borUser);
        p1.add(lbName); //p1.add();  在这里加入对方用户名
        JF.add(p2,"Center");
        Border borPass = BorderFactory.createTitledBorder("视频窗口");
        p2.setBorder(borPass);

        p2.setPreferredSize(new Dimension(800, 500));
        JF.add(p3,"South");
        Border borLogin = BorderFactory.createTitledBorder("退出");
        p3.setBorder(borLogin);
        p3.setPreferredSize(new Dimension(800, 100));
        p3.add(btnExit);

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                webcam.close();
                try {
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                JF.dispose();
                System.exit(0);

            }
        });

        JF.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  //设置点击退出
        JF.setVisible(true);  //设置可见

        g=paint(p2,g);
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    socket.close();
                    JF.dispose();
                    // colsed[0] =false;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        Client cc=new Client(); //创建客户端对象
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if((cc!=null&&socket!=null)) {
                        setWebcam(cc, socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cc.Get_acception(socket,g,JP1);
            }
        }).start();
    }

    //主函数
    public static void main(String[] args) {
        Login_UI log=new Login_UI();
        log.Create_login_UI();
    }

    //获取摄像头权限并获得图片
    public void setWebcam(Client clientconn,Socket socket) throws IOException {
        // get default webcam and open it获取网络摄像头设置并打开
        webcam = Webcam.getDefault();
        webcam.open();

        while(true) {
            // get image获取图片
            BufferedImage image = webcam.getImage();

            //if(!image.equals(null)){
            //System.out.println(image.getWidth()+" "+image.getHeight());}
            //drawImage1(g1,image);
            if(image!=null){
                drawImage1(g,image);
                clientconn.send_secret_video(image,new DataOutputStream(socket.getOutputStream()));}
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }


    }

    public Graphics paint(JPanel JP1,Graphics g) {
        g=JP1.getGraphics();
        return g;
    }

    public void drawImage1(Graphics g1,BufferedImage image) {
        g1.drawImage(image,300,100, 400, 300, this.JP1);
    }
}

//登录界面的UI类
class Login_UI{
    //private String ip="192.168.43.138";
//        private String ip="192.168.31.172";
    private String ip = "127.0.0.1";

    private int port=9998;
    private boolean change=false;
    protected final JOptionPane JOptionPane = null;
    //private ArrayList<String> name=new ArrayList<>(); //用于存储用户名
    //private ArrayList<String> password=new ArrayList<>(); //用于存储密码的Hash

    JPanel p1 = new JPanel();
    JPanel p2 = new JPanel();
    JPanel p3 = new JPanel();
    Label lbUsername = new Label("Username");
    Label lbPassword = new Label("Password");
    TextField tfUsername = new TextField(25);
    TextField tfPassword = new TextField(25);
    Button btnLogin = new Button("Login");
    Button btnRegister = new Button("Register");

    /**
     *
     */
    JFrame jf;
    OutputStream out;  //输出流
    InputStream in;    //输入流
    Socket client;
    boolean lianjie;

    Socket Create_connection(String str) throws IOException {
        //发起连接请求
        client=new Socket(ip,port);
        in = client.getInputStream();    //接受
        out = client.getOutputStream();  //发送

        System.out.println("Socket连接成功");

        DataInputStream datain=new DataInputStream(in);
        DataOutputStream dataout=new DataOutputStream(out);


        dataout.writeUTF(str);
        dataout.flush();
        if(!"".equals(tfUsername.getText()) && !"".equals(tfPassword.getText())) {
            System.out.println(tfUsername.getText()+" "+tfPassword.getText());
            dataout.writeUTF(tfUsername.getText());  //发送用户名
            dataout.flush();
            dataout.writeUTF(tfPassword.getText());
            dataout.flush();
        }//发送密码
        dataout.flush();

        //dataout.close(); //关闭流
        //out.close();

        String result="";
        while(true) {
            result = datain.readUTF();
            if(!result.equals(""))
                break;
        }
        //datain.close();
        //in.close();

        //client.close();//关闭socket连接
        System.out.println(result);

        if(result.equals("true"))
            lianjie=true;
        else {
            lianjie=false;
        }
        return client;

    }

    void Create_login_UI(){
        jf = new JFrame();// 新建窗口容器

        jf.add(p1,"North");
        p1.setPreferredSize(new Dimension(400, 65));
        Border borUser = BorderFactory.createTitledBorder("用户名");
        p1.setBorder(borUser);
        p1.add(lbUsername); p1.add(tfUsername);
        jf.add(p2,"Center");
        Border borPass = BorderFactory.createTitledBorder("密码");
        p2.setBorder(borPass);
        p2.add(lbPassword); p2.add(tfPassword);
        p2.setPreferredSize(new Dimension(400, 65));
        jf.add(p3,"South");
        Border borLogin = BorderFactory.createTitledBorder("登录/注册");
        p3.setBorder(borLogin);
        p3.setPreferredSize(new Dimension(400, 65));
        p3.add(btnLogin); p3.add(btnRegister);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if ("".equals(tfUsername.getText()) || "".equals(tfPassword.getText())) {
                    //javax.swing.JOptionPane.showMessageDialog(jf, "注册成功", "正确", javax.swing.JOptionPane.WARNING_MESSAGE);
                    javax.swing.JOptionPane.showMessageDialog(jf, "用户名或密码不能为空", "错误", javax.swing.JOptionPane.WARNING_MESSAGE);
                }
                if(!"".equals(tfUsername.getText()) && !"".equals(tfPassword.getText())){

                    try {
                        client = Create_connection("注册");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    if (lianjie == false) {
                        javax.swing.JOptionPane.showMessageDialog(jf, "注册失败", "错误", javax.swing.JOptionPane.WARNING_MESSAGE);
                    }

                    if (lianjie == true) {
                        javax.swing.JOptionPane.showConfirmDialog(jf, "注册成功", "正确", javax.swing.JOptionPane.WARNING_MESSAGE);
                        //登录成功，创建客户端的UI,准备视频通话

                        //System.exit(0);
                        //jf.dispose();
                        jf.setVisible(false);
                        UI ui = new UI();
                        try {
                            ui.CreateUI(client);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        //
                    }
                }
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("".equals(tfUsername.getText()) || "".equals(tfPassword.getText())) {
                    //javax.swing.JOptionPane.showMessageDialog(jf, "注册成功", "正确", javax.swing.JOptionPane.WARNING_MESSAGE);
                    javax.swing.JOptionPane.showMessageDialog(jf, "用户名或密码不能为空", "错误", javax.swing.JOptionPane.WARNING_MESSAGE);
                }
                if(!"".equals(tfUsername.getText()) && !"".equals(tfPassword.getText())){
                    try {
                        client = Create_connection("登录");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    if (lianjie == false) {
                        javax.swing.JOptionPane.showMessageDialog(jf, "登录失败", "错误", javax.swing.JOptionPane.WARNING_MESSAGE);
                    }

                    if (lianjie == true) {
                        javax.swing.JOptionPane.showConfirmDialog(jf, "登录成功", "正确", javax.swing.JOptionPane.WARNING_MESSAGE);
                        //登录成功，创建客户端的UI,准备视频通话
                        //System.exit(0);
                        //jf.dispose();
                        jf.setVisible(false);
                        UI ui = new UI();
                        try {
                            ui.CreateUI(client);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }
        });
        jf.setTitle("Login");
        jf.setSize(400, 250);// 设置大小
        jf.setLocation(760, 300);// 设置坐标
        jf.setVisible(true);
        //frameLogin.setAlwaysOnTop(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置点击退出

    }

}



class BackgroundPanel extends JPanel { // 声明类为JPanel添加背景照片

    private static final long serialVersionUID = -6352788025440244338L;

    private Image image = null;

    public BackgroundPanel(Image image) {
        this.image = image;
    }

    // 固定背景图片，允许这个JPanel可以在图片上添加其他组件
    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}
