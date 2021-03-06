import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class WorldPanel extends JPanel {
    public Man man;
    public ArrayList<Road> roads = new ArrayList<>();
    public ArrayList<Forest> forests = new ArrayList<>();
    public ArrayList<River> rivers = new ArrayList<>();
    public ArrayList<WaterLilyRiver> waterLilyRivers = new ArrayList<>();
    public int level;
    public int roadHeight = 30;
    public int numberOfDeath = 0;
    public boolean needNewLevel = false;
    public InventarPanel inventarPanel = new InventarPanel();
    public int otstup = 150;
    public int n = 20;
    public boolean isGod = false;
    public boolean isContour = false;

    public WorldPanel(int level) throws IOException {
        this.level = level;
        man = new Man();
        start();
    }

    public void start() {
        roads.clear();
        forests.clear();
        rivers.clear();
        waterLilyRivers.clear();
        man.start();

        needNewLevel = false;
        waterLilyRivers.add(new WaterLilyRiver(level, otstup, roadHeight));
        for (int i = 1; i < n; i = i + 1) {
            double d1 = Math.random();
            if (d1 > 0.66) {
                forests.add(new Forest(level, otstup + i * roadHeight, roadHeight));
                roads.add(new Road(level, otstup + (i + 1) * roadHeight, roadHeight));
                roads.add(new Road(level, otstup + (i + 2) * roadHeight, roadHeight));
            } else if (d1 < 0.33) {
                roads.add(new Road(level, otstup + i * roadHeight, roadHeight));
                roads.add(new Road(level, otstup + (i + 1) * roadHeight, roadHeight));
                forests.add(new Forest(level, otstup + (i + 2) * roadHeight, roadHeight));
            } else {
                roads.add(new Road(level, otstup + i * roadHeight, roadHeight));
                forests.add(new Forest(level, otstup + (i + 1) * roadHeight, roadHeight));
                roads.add(new Road(level, otstup + (i + 2) * roadHeight, roadHeight));
            }
            if (Math.random() > 0.5) {
                rivers.add(new River(level, otstup + (i + 3) * roadHeight, roadHeight));
                i = i + 1;
            } else if (Math.random() > 0.3) {
                waterLilyRivers.add(new WaterLilyRiver(level, otstup + (i + 3) * roadHeight, roadHeight));
                i = i + 1;
            }
            forests.add(new Forest(level, otstup + (i + 3) * roadHeight, roadHeight));
            i = i + 3;
        }
        int t = roads.size() + forests.size() + rivers.size();

        while (t < n + 3) {
            forests.add(new Forest(level, otstup + t * roadHeight, roadHeight));
            t = t + 1;
        }
        t = roads.size() + forests.size() + rivers.size();
        rivers.add(new River(level, otstup + t * roadHeight, roadHeight));
    }

    public void CheckKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            if ((e.getKeyCode() == KeyEvent.VK_ESCAPE) || (e.getKeyCode() == KeyEvent.VK_Q)) {
                inventarPanel.isOn = !inventarPanel.isOn;  //?????????????? ????????????????
            } else {
                man.CheckKeyEvent(e);
            }
            if (e.getKeyCode() == KeyEvent.VK_G) {
                isGod = !isGod;  //?????????? ????????
            }
            if (e.getKeyCode() == KeyEvent.VK_C){
                isContour = !isContour;  //????????????
            }
        }
    }

    public void draw(Graphics2D g2d, Painter p) {
        p.drawWorld(g2d, level, this);
        for (int i = 0; i < forests.size(); i = i + 1) {
            forests.get(i).draw(g2d, p, isContour);
        }
        for (int i = 0; i < roads.size(); i = i + 1) {
            roads.get(i).draw(g2d, p, isContour);
        }
        if (rivers != null) {
            for (int i = 0; i < rivers.size(); i = i + 1) {
                rivers.get(i).draw(g2d, p, isContour);
            }
        }
        if (waterLilyRivers != null) {
            for (int i = 0; i < waterLilyRivers.size(); i = i + 1) {
                waterLilyRivers.get(i).draw(g2d, p, isContour);
            }
        }
        man.draw(g2d, p, isContour, isGod);
        g2d.setColor(Color.BLACK);
          if (inventarPanel.isOn == true) {
            inventarPanel.draw(g2d, level);
        }
    }

    public void updateState(long dt) {
        man.lilySpeed = 0;
        if (inventarPanel.isOn == false) {
            for (int i = 0; i < roads.size(); i++) {
                roads.get(i).updateState(dt);
            }
            for (int i = 0; i <waterLilyRivers.size(); i++) {
                waterLilyRivers.get(i).updateState(dt);
            }

            for (int i = 0; i < roads.size(); i = i + 1) {
                roads.get(i).updateState(dt);
                if (man.checkCollisionRoad(roads.get(i))) {
                    if (isGod == false) {
                        man.start();
                        numberOfDeath = numberOfDeath + 1;
                    }
                }
            }
            for (int i = 0; i < rivers.size(); i = i + 1) {
                if ((man.y >= rivers.get(i).y) && (man.y <= rivers.get(i).y + 10)) {
                    if (man.checkCollisionRiver(rivers.get(i))) {
                        if (isGod == false) {
                            man.start();
                            numberOfDeath = numberOfDeath + 1;
                        }
                    }
                }
            }
            for (int i = 0; i < waterLilyRivers.size(); i = i + 1) {
                if ((man.y >= waterLilyRivers.get(i).y) && (man.y <= waterLilyRivers.get(i).y + 10)) {
                    if (man.checkCollisionWaterLilyRiver(waterLilyRivers.get(i))) {
                        man.lilySpeed = waterLilyRivers.get(i).speed;
                    } else {
                        if (isGod == false) {
                            man.start();
                            numberOfDeath = numberOfDeath + 1;
                        }
                    }
                }
            }
            if (man.y < 85) {
                needNewLevel = true;
            }
            man.x = man.x + man.lilySpeed * dt;  //?????????????????? ???????????? ?? ????????????
            man.x = man.x + 2 * 600;
            man.x = man.x % 600;
        }
    }
}
