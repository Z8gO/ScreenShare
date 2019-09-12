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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Client {
  /**192.168.41.111:65534
   * 远程客户端，要连接到服务端 65535
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    JFrame jFrame = null;
    String input = "";
    DataInputStream dis= null;
    Socket serverSocket= null;
    try {
      Map<String, String> getenv = System.getenv();
      String userProfile = getenv.get("USERPROFILE");
      String tempFileName = userProfile.endsWith("\\") ? userProfile + "ScreenShare_temp.txt" : userProfile + "\\ScreenShare_temp.txt";
      System.out.println(tempFileName);
      File tempFile = new File(tempFileName);
      String readLine = "192.168.80.44:23333";  //默认端口23333
      if (tempFile.isFile()) {
        FileReader tempFileReader = new FileReader(tempFile);
        BufferedReader bufferReader = new BufferedReader(tempFileReader);
        String readStr = bufferReader.readLine();
        if (null != readStr) {
          readLine = readStr.trim();
        }
        bufferReader.close();
        tempFileReader.close();
      } else {
        tempFile.createNewFile();
      }
      
      boolean isNull = true;
      while (isNull) {
        input = JOptionPane.showInputDialog("请输入要连接的服务端(包括端口号)：(如127.0.0.1:23333)", readLine);
        if (null == input) {
          return;
        }
        if (!"".equals(input.trim())) {
          isNull = false;
        } else {
          JOptionPane.showMessageDialog(null, "您输入的服务端信息为空，请重新输入！", "输入为空", JOptionPane.INFORMATION_MESSAGE);
        }
      }

      System.out.println(input);
      FileWriter fw = new FileWriter(tempFile);
      BufferedWriter bufferedWriter = new BufferedWriter(fw);
      bufferedWriter.write(input);
      bufferedWriter.close();
      fw.close();

      //获取服务器主机
      String host = input.substring(0, input.indexOf(":"));
      //获取端口号
      String post = input.substring(input.indexOf(":") + 1);
      //连接服务器
      serverSocket = new Socket(host, Integer.parseInt(post));
      dis = new DataInputStream(serverSocket.getInputStream());
      
      jFrame = new JFrame();
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
        if (serverSocket.isClosed()) {
          jFrame.dispose();
          JOptionPane.showMessageDialog(null, "本地断开网络或服务端出现故障，请重新尝试连接服务端！", "断开连接", JOptionPane.INFORMATION_MESSAGE);
          System.exit(0);
        }
        int len = dis.readInt();
        if(0== len){
          jFrame.dispose();
          dis.close();
          JOptionPane.showMessageDialog(null, "服务端出现故障，请联系服务端！", "服务故障", JOptionPane.INFORMATION_MESSAGE);
          return;
        }
        
        byte[] imageData = new byte[len];
        dis.readFully(imageData);

        ImageIcon image = new ImageIcon(imageData);
        backImage.setIcon(image);
        //从新画制面板
        jFrame.repaint();
      }
    } catch (Exception e) {
      System.out.println("服务出现问题，原因：" + e.getMessage() + "，请重新启动");
      if (null != jFrame) {
        jFrame.dispose();
      }
      if(null !=dis){
        dis.close();
      }
      if(serverSocket !=null){
        serverSocket.close();
      }
      String message = e.getMessage();
      message = message.equals("Connection reset") ? "服务端主动断开了连接" : message.equals("Connection refused: connect") ? "无法连接:" + input : message;
      JOptionPane.showMessageDialog(null, "服务端出现故障:" + message + "，请联系服务端！", "服务故障", JOptionPane.INFORMATION_MESSAGE);
      return;
    }
  }
}
