import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Random;
import java.util.ArrayList;

public class MyProgram {
    private static boolean isDPressed = false;
    private static boolean isAPressed = false;
    private static boolean isWPressed = false;
    private static boolean isSPressed = false;
    private static boolean isSpacePressed = false;
    private static double posx = 15;
    private static double posy = 350;
    private static int stage = 0;
    private static int lastStage = -1;
    private static boolean spaceHandled = false;
    private static double facingDirection = 1;

    private static Color[] skyColors = {
        new Color(210, 105, 30),
        new Color(184, 134, 11),
        new Color(139, 69, 19),
        new Color(160, 82, 45),
        new Color(205, 92, 92)
    };

    private static int[] treeX = new int[4];
    private static Color[] treeLeafColors = new Color[4];
    private static int[] cloudX = new int[5];
    private static int[] cloudY = new int[5];
    private static int[] cloudSize = new int[5];

    static class Scarecrow {
        double x, y;
        boolean alive = true;
        Scarecrow(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Projectile {
        double x, y, vx;
        boolean active = true;
        Projectile(double x, double y, double vx) {
            this.x = x;
            this.y = y;
            this.vx = vx;
        }
    }

    static class StagePumpkin {
        double x, y, width, height;
        StagePumpkin(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private static ArrayList<Scarecrow> scarecrows = new ArrayList<>();
    private static ArrayList<Projectile> projectiles = new ArrayList<>();
    private static ArrayList<StagePumpkin> stagePumpkins = new ArrayList<>();

    private static void randomizeStageAssets() {
        Random rand = new Random(stage);
        for (int i = 0; i < treeX.length; i++) {
            treeX[i] = 100 + i * 350 + rand.nextInt(150);
            int colorChoice = rand.nextInt(3);
            if (colorChoice == 0) treeLeafColors[i] = new Color(255, 140, 0);
            else if (colorChoice == 1) treeLeafColors[i] = new Color(178, 34, 34);
            else treeLeafColors[i] = new Color(218, 165, 32);
        }
        for (int i = 0; i < cloudX.length; i++) {
            cloudX[i] = rand.nextInt(1300);
            cloudY[i] = 50 + rand.nextInt(200);
            cloudSize[i] = 60 + rand.nextInt(60);
        }

        scarecrows.clear();
        projectiles.clear();
        stagePumpkins.clear();

        if (stage > 0) {
            int scarecrowCount = 1 + rand.nextInt(3);
            for (int i = 0; i < scarecrowCount; i++) {
                scarecrows.add(new Scarecrow(400 + rand.nextInt(700), 350));
            }
        }

        int pumpkinCount = rand.nextInt(3);
        for (int i = 0; i < pumpkinCount; i++) {
            double pWidth = 60;
            double pHeight = 40;
            double pY = 450 - pHeight;
            stagePumpkins.add(new StagePumpkin(200 + rand.nextInt(800), pY, pWidth, pHeight));
        }
    }

    private static void restartGame() {
        stage = 0;
        lastStage = -1;
        posx = 15;
        posy = 350;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setSize(1440, 900);
        f.setLayout(null);

        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (stage != lastStage) {
                    randomizeStageAssets();
                    lastStage = stage;
                }

                g.setColor(skyColors[stage % skyColors.length]);
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(new Color(255, 250, 240, 180));
                for (int i = 0; i < cloudX.length; i++) {
                    g.fillOval(cloudX[i], cloudY[i], cloudSize[i], cloudSize[i] / 2);
                    g.fillOval(cloudX[i] + 20, cloudY[i] - 10, cloudSize[i] * 3 / 4, cloudSize[i] / 2);
                }

                for (int i = 0; i < treeX.length; i++) {
                    g.setColor(new Color(110, 60, 20));
                    g.fillRect(treeX[i] + 25, 300, 30, 150);
                    g.setColor(treeLeafColors[i]);
                    g.fillOval(treeX[i], 200, 80, 80);
                    g.fillOval(treeX[i] - 20, 240, 70, 70);
                    g.fillOval(treeX[i] + 30, 240, 70, 70);
                }

                g.setColor(new Color(139, 90, 43));
                g.fillRect(0, 450, getWidth(), getHeight() - 450);
                g.setColor(new Color(34, 139, 34));
                g.fillRect(0, 450, getWidth(), 20);

                for (StagePumpkin p : stagePumpkins) {
                    g.setColor(new Color(255, 140, 0));
                    g.fillOval((int) p.x, (int) p.y, (int) p.width, (int) p.height);
                    g.setColor(new Color(0, 100, 0));
                    g.fillRect((int) p.x + (int) p.width / 2 - 3, (int) p.y - 8, 6, 10);
                }

                for (Scarecrow s : scarecrows) {
                    if (s.alive) {
                        g.setColor(new Color(101, 67, 33));
                        g.fillRect((int) s.x + 20, (int) s.y + 20, 10, 80);
                        g.fillRect((int) s.x, (int) s.y + 35, 50, 8);
                        g.setColor(new Color(240, 230, 140));
                        g.fillOval((int) s.x + 10, (int) s.y, 30, 30);
                        g.setColor(Color.BLUE);
                        g.fillRect((int) s.x + 10, (int) s.y + 30, 30, 40);
                    }
                }

                for (Projectile p : projectiles) {
                    if (p.active) {
                        g.setColor(new Color(255, 140, 0));
                        g.fillOval((int) p.x, (int) p.y, 20, 20);
                    }
                }

                g.setColor(Color.BLACK);
                g.drawOval((int) posx + 15, (int) posy, 20, 20);
                g.drawLine((int) posx + 25, (int) posy + 20, (int) posx + 25, (int) posy + 60);

                int animFrame = (int) (System.currentTimeMillis() / 120) % 2;
                if (isDPressed || isAPressed) {
                    if (animFrame == 0) {
                        g.drawLine((int) posx + 25, (int) posy + 60, (int) posx + 10, (int) posy + 100);
                        g.drawLine((int) posx + 25, (int) posy + 60, (int) posx + 40, (int) posy + 100);
                    } else {
                        g.drawLine((int) posx + 25, (int) posy + 60, (int) posx + 20, (int) posy + 100);
                        g.drawLine((int) posx + 25, (int) posy + 60, (int) posx + 30, (int) posy + 100);
                    }
                } else {
                    g.drawLine((int) posx + 25, (int) posy + 60, (int) posx + 15, (int) posy + 100);
                    g.drawLine((int) posx + 25, (int) posy + 60, (int) posx + 35, (int) posy + 100);
                }

                g.drawLine((int) posx + 25, (int) posy + 35, (int) posx + 5, (int) posy + 50);
                g.drawLine((int) posx + 25, (int) posy + 35, (int) posx + 45, (int) posy + 50);
            }
        };
        canvas.setBounds(0, 0, 1440, 2560);
        canvas.setOpaque(true);
        f.add(canvas);

        f.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'd' || e.getKeyCode() == KeyEvent.VK_D) {
                    isDPressed = true;
                    facingDirection = 1;
                }
                if (e.getKeyChar() == 'a' || e.getKeyCode() == KeyEvent.VK_A) {
                    isAPressed = true;
                    facingDirection = -1;
                }
                if (e.getKeyChar() == 'w' || e.getKeyCode() == KeyEvent.VK_W) {
                    isWPressed = true;
                }
                if (e.getKeyChar() == 's' || e.getKeyCode() == KeyEvent.VK_S) {
                    isSPressed = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    isSpacePressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == 'd' || e.getKeyCode() == KeyEvent.VK_D) {
                    isDPressed = false;
                }
                if (e.getKeyChar() == 'a' || e.getKeyCode() == KeyEvent.VK_A) {
                    isAPressed = false;
                }
                if (e.getKeyChar() == 'w' || e.getKeyCode() == KeyEvent.VK_W) {
                    isWPressed = false;
                }
                if (e.getKeyChar() == 's' || e.getKeyCode() == KeyEvent.VK_S) {
                    isSPressed = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    isSpacePressed = false;
                    spaceHandled = false;
                }
            }
        });

        f.setVisible(true);
        f.setFocusable(true);
        f.requestFocusInWindow();

        double velx = 0;
        double vely = 0;
        double gravity = 0.5;
        boolean start = true;

        while (start) {
            if (isDPressed) {
                if (velx <= 2) {
                    velx += 0.05;
                }
            } else if (isAPressed) {
                if (velx >= -2) {
                    velx -= 0.05;
                }
            } else {
                if (velx > 0) {
                    velx -= 0.05;
                    if (velx < 0) velx = 0;
                } else if (velx < 0) {
                    velx += 0.05;
                    if (velx > 0) velx = 0;
                }
            }

            if (isSpacePressed && !spaceHandled) {
                projectiles.add(new Projectile(posx + 25, posy + 30, facingDirection * 8));
                spaceHandled = true;
            }

            for (Projectile p : projectiles) {
                if (p.active) {
                    p.x += p.vx;
                    if (p.x < 0 || p.x > 1440) {
                        p.active = false;
                    }
                    for (Scarecrow s : scarecrows) {
                        if (s.alive && p.x >= s.x && p.x <= s.x + 50 && p.y >= s.y && p.y <= s.y + 100) {
                            s.alive = false;
                            p.active = false;
                        }
                    }
                }
            }

            posx += velx;

            for (StagePumpkin p : stagePumpkins) {
                if (posy + 100 > p.y && posy < p.y + p.height) {
                    if (velx > 0 && posx + 50 > p.x && posx < p.x) {
                        posx = p.x - 50;
                        velx = 0;
                    } else if (velx < 0 && posx < p.x + p.width && posx + 50 > p.x + p.width) {
                        posx = p.x + p.width;
                        velx = 0;
                    }
                }
            }

            double groundY = 350;

            for (StagePumpkin p : stagePumpkins) {
                if (posx + 50 > p.x && posx < p.x + p.width) {
                    double pumpkinTop = p.y - 100;
                    if (posy <= pumpkinTop + 10 && posy + vely >= pumpkinTop) {
                        if (pumpkinTop < groundY) {
                            groundY = pumpkinTop;
                        }
                    }
                }
            }

            if (isWPressed && (posy >= groundY)) {
                vely = -10;
            }

            vely += gravity;
            posy += vely;

            if (posy > groundY) {
                posy = groundY;
                vely = 0;
            }

            for (Scarecrow s : scarecrows) {
                if (s.alive) {
                    if (posx + 35 >= s.x && posx + 15 <= s.x + 50 && posy + 100 >= s.y && posy <= s.y + 100) {
                        restartGame();
                        break;
                    }
                }
            }

            canvas.repaint();

            if (posx >= 1300) {
                stage += 1;
                posx = 15;
            }

            try {
                Thread.sleep(20);
            } catch (Exception e) {}
        }
    }
}