/*
 * 项目名:      ScreenShare
 * 文件名:      Client.java
 * 类名:        Client
 *
 *  iamobject.club
 * 
 */
package club.iamobject.bootstrap;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.DataInputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Client {
  /**
   * 远程客户端，要连接到服务端
   */
  public static void main(String[] args) {
    String input = JOptionPane.showInputDialog("请输入要连接的服务端(包括端口号)：(如127.0.0.1:10000)", "127.0.0.1:10000");
    JFrame jFrame = new JFrame();
    try {
      //获取服务器主机
      String host = input.substring(0, input.indexOf(":"));
      //获取端口号
      String post = input.substring(input.indexOf(":") + 1);
      //连接服务器
      @SuppressWarnings("resource")
      Socket client = new Socket(host, Integer.parseInt(post));
      DataInputStream dis = new DataInputStream(client.getInputStream());
      //创建面板
      jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      jFrame.setTitle("客户端");
      jFrame.setSize(1366, 768);

      //读取服务端分辨率
      double height = dis.readDouble();
      double width = dis.readDouble();

      Dimension dimensionServer = new Dimension((int) height, (int) width);

      //设置
      jFrame.setSize(dimensionServer);
      //将服务端的图片作为背景
      JLabel backImage = new JLabel();
      JPanel panel = new JPanel();

      //需要滚动条
      JScrollPane scrollPane = new JScrollPane(panel);
      panel.setLayout(new FlowLayout());

      panel.add(backImage);
      jFrame.add(scrollPane);
      jFrame.setAlwaysOnTop(true);
      jFrame.setVisible(true);
      while (true) {
        int len = dis.readInt();
        byte[] imageData = new byte[len];
        dis.readFully(imageData);

        ImageIcon image = new ImageIcon(imageData);
        backImage.setIcon(image);
        //从新画制面板
        jFrame.repaint();
      }
    } catch (Exception e) {
      System.out.println("服务出现问题，原因："+e.getMessage()+"，请重新启动");
      JOptionPane.showMessageDialog(null, "服务端出现故障，请联系服务端！","服务故障",  JOptionPane.INFORMATION_MESSAGE);
      System.exit(0);
    }
  }
}
