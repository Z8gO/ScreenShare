/*
 * 项目名:      ScreenShare
 * 文件名:      Server.java
 * 类名:        Server
 *
 * 
 * */
package club.iamobject.bootstrap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.sun.image.codec.jpeg.*;

/**
 * 分享端
 */
public class Server {

  public static void main(String[] args) {
    //设置连接的端口
    ServerSocket ss = null;
    String hostName = "";
    List<Thread>  threadList= new ArrayList<Thread>();
    try {
      String input = JOptionPane.showInputDialog("请输入要启用的端口号：(如：10000)", "10000");
      ss = new ServerSocket(Integer.parseInt(input));
      while (true) {
        for (int i = 0; i < threadList.size(); i++) {
          boolean alive = threadList.get(i).isAlive();
          if(!alive){
            threadList.remove(threadList.get(i));
          }
        }
        
        System.out.println("等待连接,当前连接数："+threadList.size());
        //连接成功
        Socket client = ss.accept();
        hostName = client.getInetAddress().getHostName();
        System.out.println("连接成功!");
        //向服务端输出流
        OutputStream os = client.getOutputStream();
        //输出流
        DataOutputStream dos = new DataOutputStream(os);
        //向客户端输出图像信息的线程，一个客户端一个线程
        ScreenThread screenThread = new ScreenThread(dos, client);
        System.out.println(screenThread.getName()+"准备启动。。。。");
        //线程
        screenThread.start();
        threadList.add(screenThread);
        Thread.sleep(1000);
      }
    } catch (IOException e) {
      if (ss != null) {
        try {
          ss.close();
          System.out.println("客户端：" + hostName + "断开了连接");
        } catch (IOException e1) {
          System.out.println("服务端关闭端口出现异常：" + e1.getMessage());
          JOptionPane.showMessageDialog(null, e1.getMessage(),"服务故障",  JOptionPane.INFORMATION_MESSAGE);
        }
      }

      System.out.println("服务端出现异常：" + e.getMessage());
      JOptionPane.showMessageDialog(null, e.getMessage(),"服务故障",  JOptionPane.INFORMATION_MESSAGE);
    } catch (InterruptedException e) {
      JOptionPane.showMessageDialog(null, e.getMessage(),"服务故障",  JOptionPane.INFORMATION_MESSAGE);
      e.printStackTrace();
    }
  }
}

// 线程
class ScreenThread extends Thread {
  // 
  private DataOutputStream dataOut;

  private Socket           client;
  
  public ScreenThread(DataOutputStream dataOut, Socket client) {
    this.dataOut = dataOut;
    this.client = client;
  }

  @Override
  public void run() {
    System.out.println("客户端地址：" + client.getInetAddress());
    System.out.println(client.toString());
    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension dm = tk.getScreenSize();
    try {
      // 
      dataOut.writeDouble(dm.getHeight());
      dataOut.writeDouble(dm.getWidth());
      dataOut.flush();
      // 
      Rectangle rec = new Rectangle(dm);
      Robot robot = new Robot();
      while (true) {
        //创建画板
        BufferedImage bufferedImage = robot.createScreenCapture(rec);
        
      //读取图片文件，得到BufferedImage对象
        
        //得到Graphics2D 对象
        Graphics2D g2d=(Graphics2D)bufferedImage.getGraphics();
        //设置颜色和画笔粗细
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        PointerInfo pinfo = MouseInfo.getPointerInfo();
        
        Point p = pinfo.getLocation();
        int mx = (int) p.getX();
        int my = (int) p.getY();
        
       // 绘制文字来显示鼠标
        //g2d.drawString("↖", mx, my);
        
        //通过画 十  字来显示鼠标
        //g2d.drawLine(mx-10, my, mx+10, my);
        //g2d.drawLine(mx, my-10, mx, my+10);
        
        //画鼠标
        //g2d.drawLine(mx, my, mx+10, my+20);
        //g2d.drawLine(mx, my, mx+20, my+6);
        //g2d.drawLine(mx+10, my+20, mx+20, my+6);
        
        
        //g2d.drawLine(mx+3, my+5, mx+10, my+20);
        //g2d.drawLine(mx+3, my+5, mx+20, my+8);
        //g2d.drawLine(mx-3, my+6, mx, my+5);
        //g2d.drawLine(mx-5, my+5, mx+1, my+5);
        
        //通过图片来显示鼠标
        BufferedImage bimg=ImageIO.read(getClass().getClassLoader().getResource("club/iamobject/img/mo.png"));
        g2d.drawImage(bimg, mx, my,  null );
        
        // 拿到输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
        encoder.encode(bufferedImage);
      
        //保存新图片
        //ImageIO.write(bimg, "JPG",new FileOutputStream("文件路径"));
        
        // 将数据存到数组
        byte[] data = baos.toByteArray();
        // 
        dataOut.writeInt(data.length);
        dataOut.write(data);
        dataOut.flush();
        Thread.sleep(20);
      }
    } catch (Exception e) {
      System.out.println("客户端关闭。。。。");
      //System.exit(0);
    }
  }
}
