package team.ngup.douban;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import team.ngup.douban.common.http.HttpClientResult;
import team.ngup.douban.common.http.HttpClientUtils;
import team.ngup.douban.request.DoubanRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhangwenwu
 */
public class Login {

    private JPanel loginPanel;
    private JButton loginBtn;
    private JLabel loginStatus;
    private static volatile AtomicBoolean isLogined = new AtomicBoolean(false);


    public Login() {
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (!isLogined.get()) {
                    /*try {*/
                        new LoginStatusRealized().execute();
                        /*JSONObject response = DoubanRequest.getQr();
                        String picUrl = response.getString("img");
                        Desktop.getDesktop().browse(new URL(picUrl).toURI());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (isLogined) {
                                    try {
                                        while (!DoubanRequest.isLogined(response.getString("code"))) {
                                            System.out.println("还未扫码登录");
                                            Thread.sleep(2000);
                                        }
                                        isLogined.set(true);
                                        isLogined.notifyAll();
                                    } catch (IOException | URISyntaxException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }).start();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (isLogined) {
                                    try {
                                        isLogined.wait();
                                        // 获取用户信息
                                        JSONObject userInfo = DoubanRequest.getUserInfo();
                                        System.out.println(userInfo);
                                        //loginStatus.setText(userInfo.getString("name"));
                                        // 利用EnventQueue类来更新Swing控件
                                        EventQueue.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginStatus.setText(userInfo.getString("name"));
                                            }
                                        });
                                    } catch (InterruptedException | IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                            }
                        }).start();*/

                    /*} catch (IOException | URISyntaxException e1) {
                        e1.printStackTrace();
                    }*/
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {

        JFrame frame = new JFrame("Login");
        Login login = new Login();
        frame.setContentPane(login.loginPanel);
        frame.add(login.loginStatus);
        login.loginStatus.setText("暂未登录");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (login.isLogined) {
                        login.isLogined.wait();
                        // 播放私人兆赫
                        // System.out.println(songs);
                        boolean findSi=true;
                        while(findSi) {
                            Thread.sleep(2000);
                            JSONArray songs = DoubanRequest.getSiRen().getJSONArray("song");
                            if (songs.size() > 0) {
                                login.getVideo(songs.getJSONObject(0).getString("url"));
                                findSi=false;
                            } else {
                                System.out.println("暂无");
                            }
                        }
                    }
                } catch (InterruptedException | IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    public void getVideo(String url) {
        final JFXPanel VFXPanel = new JFXPanel();

        Media m = new Media(url);
        MediaPlayer player = new MediaPlayer(m);
        MediaView viewer = new MediaView(player);

        StackPane root = new StackPane();
        Scene scene = new Scene(root);

        // center video position
        javafx.geometry.Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        viewer.setX((screen.getWidth() - loginPanel.getWidth()) / 2);
        viewer.setY((screen.getHeight() - loginPanel.getHeight()) / 2);
/*        viewer.setX(0);
        viewer.setY(0);*/
        // resize video based on screen size
        DoubleProperty width = viewer.fitWidthProperty();
        DoubleProperty height = viewer.fitHeightProperty();
        width.bind(Bindings.selectDouble(viewer.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(viewer.sceneProperty(), "height"));
        viewer.setPreserveRatio(true);

        // add video to stackpane
        root.getChildren().add(viewer);

        VFXPanel.setScene(scene);
        //System.out.println(player.getCurrentTime());
        player.play();
        loginPanel.setLayout(new BorderLayout());
        loginPanel.add(VFXPanel, BorderLayout.CENTER);
    }


    class LoginStatusRealized extends SwingWorker<Void, JSONObject> {

        @Override
        protected Void doInBackground() throws Exception {
            JSONObject response = DoubanRequest.getQr();
            String picUrl = response.getString("img");
            Desktop.getDesktop().browse(new URL(picUrl).toURI());
            try {
                while (!DoubanRequest.isLogined(response.getString("code"))) {
                    System.out.println("还未扫码登录");
                    Thread.sleep(2000);
                }
                // 获取用户信息
                JSONObject userInfo = DoubanRequest.getUserInfo();
                System.out.println(userInfo);
                publish(userInfo);
                synchronized (isLogined){
                    isLogined.set(true);
                    isLogined.notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            /* }).start();*/

            return null;
        }

        @Override
        protected void process(List<JSONObject> chunks) {
            loginStatus.setText(chunks.get(0).getString("name"));
        }
    }


}
