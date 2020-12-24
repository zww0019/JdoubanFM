package team.ngup.douban;import com.alibaba.fastjson.JSONArray;import com.alibaba.fastjson.JSONObject;import com.tulskiy.keymaster.common.HotKey;import com.tulskiy.keymaster.common.HotKeyListener;import com.tulskiy.keymaster.common.Provider;import javafx.beans.binding.Bindings;import javafx.beans.property.DoubleProperty;import javafx.embed.swing.JFXPanel;import javafx.scene.Scene;import javafx.scene.layout.StackPane;import javafx.scene.media.Media;import javafx.scene.media.MediaPlayer;import javafx.scene.media.MediaView;import javafx.stage.Screen;import team.ngup.douban.common.COLLECT_EVENT_TYPE;import team.ngup.douban.request.DoubanRequest;import javax.swing.*;import java.awt.*;import java.awt.event.MouseAdapter;import java.awt.event.MouseEvent;import java.awt.event.WindowEvent;import java.awt.event.WindowListener;import java.io.IOException;import java.net.URISyntaxException;import java.net.URL;import java.util.List;import java.util.concurrent.atomic.AtomicBoolean;/** * @author zhangwenwu */public class Login {    private JPanel loginPanel;    private JButton loginBtn;    private JLabel loginStatus;    private static volatile AtomicBoolean isLogined = new AtomicBoolean(false);    public Login() {        loginBtn.addMouseListener(new MouseAdapter() {            @Override            public void mouseClicked(MouseEvent e) {                super.mouseClicked(e);                if (!isLogined.get()) {                    new LoginStatusRealized().execute();                }            }        });        //主类是Provider,获取当前平台（操作系统）的提供者        //Provider provider = Provider.getCurrentProvider(useSwingEventQueue);        Provider provider = Provider.getCurrentProvider(true);        //注册热键,键值是以字符串的形式传递的        provider.register(KeyStroke.getKeyStroke("meta RIGHT"), new HotKeyListener() {            @Override            public void onHotKey(HotKey arg0) {                System.out.println("meta right");                //doStart();//在这里编写热键响应代码                if (isLogined.get()) {                    boolean findSi = true;                    while (findSi) {                        try {                            Thread.sleep(2000);                            JSONArray songs = DoubanRequest.getSiRen().getJSONArray("song");                            if (songs.size() > 0) {                                getVideo(songs.getJSONObject(0).getString("url"));                                DoubanRequest.collect_event(COLLECT_EVENT_TYPE.NEXT);                                DoubanRequest.promo(songs.getJSONObject(0).getString("sid"));                                findSi = false;                            } else {                                System.out.println("暂无");                            }                        } catch (Exception e) {                            e.printStackTrace();                        }                    }                }            }        });        provider.register(KeyStroke.getKeyStroke("meta UP"), new HotKeyListener() {            @Override            public void onHotKey(HotKey arg0) {                System.out.println("meta up");                //doStart();//在这里编写热键响应代码                if (isLogined.get()) {                    try {                        DoubanRequest.collect_event(COLLECT_EVENT_TYPE.LIKE);                    } catch (Exception e) {                        e.printStackTrace();                    }                }            }        });        provider.register(KeyStroke.getKeyStroke("meta DOWN"), new HotKeyListener() {            @Override            public void onHotKey(HotKey arg0) {                System.out.println("meta down");                //doStart();//在这里编写热键响应代码                boolean findSi = true;                while (findSi) {                    try {                        Thread.sleep(2000);                        JSONArray songs = DoubanRequest.getSiRen().getJSONArray("song");                        if (songs.size() > 0) {                            getVideo(songs.getJSONObject(0).getString("url"));                            DoubanRequest.collect_event(COLLECT_EVENT_TYPE.DELETE);                            DoubanRequest.promo(songs.getJSONObject(0).getString("sid"));                            findSi = false;                        } else {                            System.out.println("暂无");                        }                    } catch (Exception e) {                        e.printStackTrace();                    }                }            }        });    }    public static void main(String[] args) throws IOException {        JFrame frame = new JFrame("Login");        frame.addWindowListener(new WindowListener() {            @Override            public void windowOpened(WindowEvent e) {                System.out.println("已打开窗口");            }            @Override            public void windowClosing(WindowEvent e) {                Provider provider = Provider.getCurrentProvider(true);                provider.reset();                provider.stop();                System.gc();            }            @Override            public void windowClosed(WindowEvent e) {            }            @Override            public void windowIconified(WindowEvent e) {            }            @Override            public void windowDeiconified(WindowEvent e) {            }            @Override            public void windowActivated(WindowEvent e) {            }            @Override            public void windowDeactivated(WindowEvent e) {            }        });        Login login = new Login();        frame.setContentPane(login.loginPanel);        frame.add(login.loginStatus);        frame.add(login.loginBtn);        login.loginStatus.setText("暂未登录");        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        frame.setAlwaysOnTop(!frame.isAlwaysOnTop());        //frame.        frame.pack();        frame.setVisible(true);        //login.getVideo();        new Thread(new Runnable() {            @Override            public void run() {                try {                    synchronized (login.isLogined) {                        login.isLogined.wait();                        // 播放私人兆赫                        // System.out.println(songs);                        boolean findSi=true;                        while(findSi) {                            Thread.sleep(2000);                            JSONArray songs = DoubanRequest.getSiRen().getJSONArray("song");                            if (songs.size() > 0) {                                login.getVideo(songs.getJSONObject(0).getString("url"));                                DoubanRequest.promo(songs.getJSONObject(0).getString("sid"));                                findSi = false;                            } else {                                System.out.println("暂无");                            }                        }                    }                } catch (InterruptedException | IOException | URISyntaxException e1) {                    e1.printStackTrace();                }            }        }).start();    }    private JFXPanel VFXPanel;    public void getVideo(String url) {        try {            if (VFXPanel == null) {                VFXPanel = new JFXPanel();            }            Media m = new Media(url);            MediaPlayer player = new MediaPlayer(m);            MediaView viewer = new MediaView(player);            StackPane root = new StackPane();            Scene scene = new Scene(root);            // center video position            javafx.geometry.Rectangle2D screen = Screen.getPrimary().getVisualBounds();            viewer.setX((screen.getWidth() - loginPanel.getWidth()) / 2);            viewer.setY((screen.getHeight() - loginPanel.getHeight()) / 2);/*        viewer.setX(0);        viewer.setY(0);*/            // resize video based on screen size            DoubleProperty width = viewer.fitWidthProperty();            DoubleProperty height = viewer.fitHeightProperty();            width.bind(Bindings.selectDouble(viewer.sceneProperty(), "width"));            height.bind(Bindings.selectDouble(viewer.sceneProperty(), "height"));            viewer.setPreserveRatio(true);            // add video to stackpane            root.getChildren().add(viewer);            VFXPanel.setScene(scene);            //System.out.println(player.getCurrentTime());            player.setOnStopped(new Runnable() {                @Override                public void run() {                }            });            player.play();            //loginPanel.setLayout(new BorderLayout());            //loginPanel.add(VFXPanel, BorderLayout.CENTER);        } catch (Exception e) {            e.printStackTrace();        }    }    class LoginStatusRealized extends SwingWorker<Void, JSONObject> {        @Override        protected Void doInBackground() throws Exception {            JSONObject response = DoubanRequest.getQr();            String picUrl = response.getString("img");            Desktop.getDesktop().browse(new URL(picUrl).toURI());            try {                while (!DoubanRequest.isLogined(response.getString("code"))) {                    System.out.println("还未扫码登录");                    Thread.sleep(2000);                }                // 获取用户信息                JSONObject userInfo = DoubanRequest.getUserInfo();                System.out.println(userInfo);                publish(userInfo);                synchronized (isLogined){                    isLogined.set(true);                    Thread.sleep(2000);                    isLogined.notify();                }            } catch (Exception e) {                e.printStackTrace();            }            /* }).start();*/            return null;        }        @Override        protected void process(List<JSONObject> chunks) {            loginStatus.setText(chunks.get(0).getString("name"));        }    }}