package club.iamobject.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class LocationScreen {

  public static void main(String args[]) {
    try {
      JFrame jFrame = new JFrame();
      //显示窗体
      jFrame.setVisible(true);
      jFrame.setDefaultCloseOperation(1);
      jFrame.setAlwaysOnTop(true);
      //定义直接查询本机的操作系统
      Toolkit tk = Toolkit.getDefaultToolkit();
      Dimension dm = tk.getScreenSize();
      int width = (int) dm.getWidth();
      int height = (int) dm.getHeight();
      jFrame.setSize(width - 100, height - 50);
      //显示图像
      JLabel imageLable = new JLabel();
      jFrame.add(imageLable);

      Robot robot = new Robot();
      while (true) {
        //  显示屏幕从左上角开始600×600大小的区域
        Rectangle rec = new Rectangle(0, 0, (int) dm.getWidth() - 100, (int) dm.getHeight());
        BufferedImage bufimg = robot.createScreenCapture(rec);
        //定义组件显示图片
        imageLable.setIcon(new ImageIcon(bufimg));
        Thread.sleep(0);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}