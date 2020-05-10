/*

Programming Assignment: 5- Java 2D Application
Author: Shashwat Shekhar
Date of Start: October 21, 2019
Date of Completion: October 26, 2019

*/


import java.util.ArrayList;
import java.awt.*;

import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JColorChooser;
import javax.swing.JCheckBox;
import javax.swing.JButton;


import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public final class Java2DApplication extends JFrame // Setting up the window frame
{

    static String color, shape;                     // Variable declarations
    
    
    static JCheckBox gradientBox;
    static JCheckBox dashBox;
    static JCheckBox fillBox;

    private final JTextField lineWidthJTextField;
    private final JTextField dashLengthJTextField;

    
    private final JButton clearJButton;
    private final JButton undoJButton;

    private final BorderLayout layout;
    private final JLabel statusBar;
    private final DrawPanel drawPanel;
    
    private final JComboBox shapes;
    private JButton changeFirstColorJButton;
    private JButton changeSecondColorJButton;
    

    Point start, end;


    public static void main(String[] args)              // Main function definition
    {
        Java2DApplication app = new Java2DApplication();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setSize(1000, 600);
        app.setVisible(true);
    }

    public Java2DApplication()                  //Constructor
    {

        super("Java 2D Drawings");
        layout = new BorderLayout();
        setLayout(layout);
        
        
        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
        
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);

        JPanel topLabels = new JPanel();
        JPanel bottomLabels = new JPanel();
        topPanel.add(topLabels, BorderLayout.NORTH);
        topPanel.add(bottomLabels, BorderLayout.SOUTH);

        JPanel cursorPanel = new JPanel();
        add(cursorPanel, BorderLayout.SOUTH);
        cursorPanel.setLayout(new BorderLayout());

        
        undoJButton = new JButton("Undo");
        topLabels.add(undoJButton);

        clearJButton = new JButton("Clear");
        topLabels.add(clearJButton);
        
          
        JLabel shapeChoiceJLabel = new JLabel("Shape: ");
        topLabels.add(shapeChoiceJLabel);

        shapes = new JComboBox(new String[]{"Line","Rectangle", "Oval"});       //Order of option display
        shapes.setSelectedItem(drawPanel.getShapeType());
        topLabels.add(shapes);

        shapes.addActionListener((actevent) -> 
        {
            JComboBox cb = (JComboBox) actevent.getSource();
            drawPanel.setShapeType((String) cb.getSelectedItem());
        });
        

        undoJButton.addActionListener(actevent -> drawPanel.undo());
        clearJButton.addActionListener(actevent -> drawPanel.clear());
        

        gradientBox = new JCheckBox("Use Gradient");
        gradientBox.setSelected(drawPanel.useGradient());
        bottomLabels.add(gradientBox);

        
        fillBox = new JCheckBox("Filled");
        fillBox.setSelected(drawPanel.isFilled());
        topLabels.add(fillBox);


        changeFirstColorJButton = new JButton("1st Color...");
        bottomLabels.add(changeFirstColorJButton, BorderLayout.SOUTH);
        changeFirstColorJButton.addActionListener(event -> 
        {
            Color colorOne = drawPanel.getColorOne();
            colorOne = JColorChooser.showDialog(this, "Choose a color", colorOne);
            if (colorOne != null) 
            {
                drawPanel.setColorOne(colorOne);
            }
            changeFirstColorJButton.setBackground(colorOne);
        });

        
        
        changeSecondColorJButton = new JButton("2nd Color...");
        bottomLabels.add(changeSecondColorJButton, BorderLayout.SOUTH);
        changeSecondColorJButton.addActionListener(event -> 
        {
            Color colorTwo = drawPanel.getColorTwo();
            colorTwo = JColorChooser.showDialog(this, "Choose a color", colorTwo);
            if (colorTwo != null) 
            {
                drawPanel.setColorTwo(colorTwo);
            }
            changeSecondColorJButton.setBackground(colorTwo);
        });
        
        
        JLabel lineJLabel = new JLabel("Line Width: ");
        bottomLabels.add(lineJLabel);
        lineJLabel.setFont(lineJLabel.getFont().deriveFont(12.0f));
        lineWidthJTextField = new JTextField(String.format("%d", drawPanel.getLineWidth()), 2);
        bottomLabels.add(lineWidthJTextField);
        
        
        JLabel dashJLabel = new JLabel("Dash Length: ");
        bottomLabels.add(dashJLabel);
        dashJLabel.setFont(dashJLabel.getFont().deriveFont(14.0f));
        dashLengthJTextField = new JTextField(String.format("%d", drawPanel.getDashLength()), 2);
        bottomLabels.add(dashLengthJTextField);
        

        dashBox = new JCheckBox("Dashed");
        bottomLabels.add(dashBox);
        fillBox.addActionListener(actevent -> drawPanel.setFilled(((JCheckBox) actevent.getSource()).isSelected()));
        fillBox.setSelected(false);
        gradientBox.addActionListener(actevent -> drawPanel.setUseGradient(((JCheckBox) actevent.getSource()).isSelected()));
        lineWidthJTextField.addActionListener(actevent -> drawPanel.setLineWidth(Integer.parseInt(((JTextField) actevent.getSource()).getText())));
        dashLengthJTextField.addActionListener(actevent -> drawPanel.setDashLength(Integer.parseInt(((JTextField) actevent.getSource()).getText())));
        dashBox.addActionListener(actevent -> drawPanel.setDashed(((JCheckBox) actevent.getSource()).isSelected()));



        statusBar = new JLabel("");
        cursorPanel.add(statusBar, BorderLayout.WEST);

    }

    private class DrawPanel extends JPanel 
    {

        private ArrayList<Shape> itemsDrawn; // Variable declarations
        private String shapeType;
        
        private int lineWidth;
        private int dashLength;
        
        

        private boolean isFilled;
        private boolean useGradient;
        
        
        private Color colorOne;
        private Color colorTwo;
  
        
        private boolean isDashed;

        
        
        public DrawPanel() 
        {
            itemsDrawn = new ArrayList<>();
            
            
            lineWidth = 10;
            dashLength = 15;
            
            shapeType = "Line";
            
            isFilled = false;
            useGradient = false;
            
            
            colorOne = Color.RED;
            colorTwo = Color.BLACK;

            isDashed = false;
            
            MouseHandler handler = new MouseHandler();
            addMouseListener(handler);
            addMouseMotionListener(handler);
        }
        
        

        @Override
        public void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            this.setBackground(Color.WHITE);

            Graphics2D g2d = (Graphics2D) g.create();
            
            for (Shape s : itemsDrawn) 
            {
                s.draw(g2d);
            }
            g2d.dispose();
        }

        
        
        private class MouseHandler implements MouseListener, MouseMotionListener //Adding actions in the event (Event Handling)
        {
            
            
            @Override
            public void mouseExited(MouseEvent event) 
            {
                statusBar.setText(String.format("(%d, %d)", event.getX(), event.getY()));
            }


            @Override
            public void mouseMoved(MouseEvent event) 
            {
                statusBar.setText(String.format("(%d, %d)", event.getX(), event.getY()));
            }



            @Override
            public void mousePressed(MouseEvent event) 
            {
                Point pnt1 = event.getPoint();   //Gradient Pattern
                Point pnt2 = new Point((int) pnt1.getX() + 15, (int) pnt1.getY() + 15); 
                
                
                Paint paint;
                if (useGradient())
                {
                    paint = new GradientPaint(pnt1, getColorOne(), pnt2, getColorTwo(), true);
                } 
                else 
                {
                    paint = getColorOne();
                }
                
                
                
                BasicStroke stroke;
                if (isDashed()) 
                {
                    stroke = new BasicStroke(getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{getDashLength()}, 0);
                } 
                else 
                {
                    stroke = new BasicStroke(getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
                
                
                
                Shape shape;
                
                
                
                switch (getShapeType().toUpperCase()) 
                {
      
                    
                    case "LINE":
                        shape = new MyLine(pnt1, pnt1, paint, stroke);
                        break;
                    case "RECTANGLE":
                        shape = new MyRectangle(pnt1, pnt1, isFilled(), paint, stroke);
                        break;                        
                    case "OVAL":
                        shape = new MyOval(pnt1, pnt1, isFilled(), paint, stroke);
                        break;
                        
                    default: 
                        shape = new MyLine(pnt1, pnt1, paint, stroke); //Setting the default value to a line
                }
                
                
                itemsDrawn.add(shape);
                
                repaint();

                statusBar.setText(String.format("(%d, %d)", event.getX(), event.getY()));
            }
            
            

            @Override
            public void mouseClicked(MouseEvent event) 
            {
                statusBar.setText(String.format("(%d, %d)", event.getX(), event.getY()));
            }
            
            
            @Override
            public void mouseReleased(MouseEvent event) 
            {
                repaint();
                statusBar.setText(String.format("(%d, %d)", event.getX(), event.getY()));
            }
            
            
            

            @Override
            public void mouseDragged(MouseEvent event) 
            {
                int last = itemsDrawn.size() - 1;
                if (last >= 0) {
                    Shape currentShape = itemsDrawn.get(last);
                    if (currentShape instanceof MyLine) {
                        ((MyLine) currentShape).setEndPoint(event.getPoint());
                    } else {
                        ((MyBoundedShape) currentShape).setHeightAndWidth(event.getPoint());
                    }
                }
                repaint();
                statusBar.setText(String.format("(%d, %d)", event.getX(), event.getY()));
            }

            @Override
            public void mouseEntered(MouseEvent event) 
            {
                statusBar.setText(String.format("(%d, %d)", event.getX(), event.getY()));
            }
            

        }
        
        
        public void clear()                 //Clear button functioning
        {
            itemsDrawn.clear();
            repaint();
        }
        

        public void undo()                  // Undo button functioning
        {
            if (itemsDrawn.size() > 0) 
            {
                itemsDrawn.remove(itemsDrawn.size() - 1);
            }
            repaint();
        }


        String getShapeType() 
        {
            return shapeType;
        }

        
        
        public void setShapeType(String shapeType) 
        {
            this.shapeType = shapeType;
        }

        
        
        boolean isFilled() 
        {
            return isFilled;
        }

        
        
        public void setFilled(boolean filled) 
        {
            isFilled = filled;
        }

        
        
        boolean useGradient() 
        {
            return useGradient;
        }

        
        
        public void setUseGradient(boolean useGradient) 
        {
            this.useGradient = useGradient;
        }

        
        
        Color getColorOne() 
        {
            return colorOne;
        }

        
        
        public void setColorOne(Color changeFirstColorJButton) 
        {
            this.colorOne = changeFirstColorJButton;
        }

        
        
        Color getColorTwo() 
        {
            return colorTwo;
        }

        
        
        public void setColorTwo(Color changeSecondColorJButton) 
        {
            this.colorTwo = changeSecondColorJButton;
        }

        
        
        int getLineWidth() 
        {
            return lineWidth;
        }

        
        int getDashLength() 
        {
            return dashLength;
        }

        
        public void setLineWidth(int lineWidth) 
        {
            this.lineWidth = lineWidth;
        }

       
        
        public void setDashLength(int dashLength) 
        {
            this.dashLength = dashLength;
        }

        
        
        public void setDashed(boolean dashed) 
        {
            isDashed = dashed;
        }
        
        
        boolean isDashed() 
        {
            return isDashed;
        }


        

    }


    public abstract class Shape  //Main class for Shapes
    {

        private Point startPoint;
        private BasicStroke stroke;
        private Paint paint;


        public Shape(Point pnt1, Paint paint, BasicStroke stroke) 
        {
            startPoint = pnt1;
            this.paint = paint;
            this.stroke = stroke;
        }

        public abstract void draw(Graphics2D g);

        public Point getStartPoint() 
        {
            return startPoint;
        }

        public void setStartPoint(Point p) 
        {
            if (p.getX() >= 0 && p.getY() >= 0) 
            {
                startPoint = p;
            } 
            else 
            {
                if (startPoint == null) 
                {
                    startPoint = new Point(0, 0); 
                }
            }
        }

        public Paint getPaint() 
        {
            return paint;
        }

        
        public void setPaint(Paint paint) 
        {
            this.paint = paint;
        }

        
        public BasicStroke getStroke() 
        {
            return stroke;
        }

        
        public void setStroke(BasicStroke stroke) 
        {
            this.stroke = stroke;
        }
    }
    
    
    

    public class MyLine extends Shape  //MyLine class that describes the features of line
    {

        private Point endPoint;

        public MyLine(Point p1, Point p2, Paint paint, BasicStroke stroke) 
        {
            super(p1, paint, stroke);
            if (p2.getX() >= 0 && p2.getY() >= 0) 
            {
                endPoint = p2;
            } 
            else 
            {
                if (endPoint == null) 
                {
                    endPoint = new Point(0, 0);
                }
            }
        }
        
        
        public void setEndPoint(Point p) 
        {
            if (p.getX() >= 0 && p.getY() >= 0) 
            {
                endPoint = p;
            } 
            else 
            {
                if (endPoint == null) 
                {
                    endPoint = new Point(0, 0);
                }
            }
        }

        
        
        public Point getEndPoint() 
        {
            return endPoint;
        }


        @Override
        public void draw(Graphics2D g)
        {
            g.setPaint(getPaint());
            g.setStroke(getStroke());
            Point p1 = getStartPoint();
            Point p2 = getEndPoint();
            g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
        }
        
        
    }

    public abstract class MyBoundedShape extends Shape 
    {

        private int wid;
        private int hei;
        private boolean isFilled;

        
        
        public MyBoundedShape(Point pnt1, Point pnt2, boolean isFilled, Paint paint, BasicStroke stroke) 
        {
            super(pnt1, paint, stroke);
            if (pnt2 != null) 
            {
                
                wid = (int) (pnt2.getX() - pnt1.getX()); //Logical expression for width
                hei = (int) (pnt2.getY() - pnt1.getY());//Logical expression for height
            }
            this.isFilled = isFilled;
        }
        
        
        public void setWidth(int wid) 
        {
            this.wid = wid;
        }

        
        
        public int getWidth() 
        {
            return wid;
        }

        

        
        public int getHeight() 
        {
            return hei;
        }

        
        public void setHeight(int hei) 
        {
            this.hei = hei;
        }

        public void setHeightAndWidth(Point p) 
        {
            if (p != null) 
            {
                
                setWidth((int) (p.getX() - getStartPoint().getX()));
                setHeight((int) (p.getY() - getStartPoint().getY()));
            }
        }

        public boolean isFilled() 
        {
            return isFilled;
        }


        public void setFilled(boolean filled) 
        {
            isFilled = filled;
        }


        protected int[] getDrawParameters() 
        {
            Point pnt1;
            int wi = getWidth();
            int he = getHeight();
            if (wi < 0 && he< 0) 
            {
                pnt1 = new Point((int) getStartPoint().getX() + wi, (int) getStartPoint().getY() + he);
                wi = wi* -1;
                he = he * -1;
            } else if (wi < 0) 
            {
                pnt1 = new Point((int) getStartPoint().getX() + wi, (int) getStartPoint().getY());
                wi = wi * -1;
            } else if (he < 0) 
            {
                pnt1 = new Point((int) getStartPoint().getX(), (int) getStartPoint().getY() + he);
                he = he * -1;
            } 
            else 
            {
                pnt1 = getStartPoint();
            }
            return new int[]{(int) pnt1.getX(), (int) pnt1.getY(), wi, he};
        }
    }
    
    
    
    public class MyOval extends MyBoundedShape //MyOval class that describes the features of oval
    {

        public MyOval(Point pnt1, Point pnt2, boolean isFilled, Paint paint, BasicStroke stroke) 
        {
            super(pnt1, pnt2, isFilled, paint, stroke);
        }

        @Override
        public void draw(Graphics2D g) 
        {
            g.setPaint(getPaint());
            g.setStroke(getStroke());
            
            
            int[] drawParam = getDrawParameters();
            
            if (isFilled()) 
            {
                g.fillOval(drawParam[0], drawParam[1], drawParam[2], drawParam[3]);
            } 
            else 
            {
                g.drawOval(drawParam[0], drawParam[1], drawParam[2], drawParam[3]);
            }
        }
    }
    

    
    public class MyRectangle extends MyBoundedShape //MyRectangle class that describes the features of rectangle
    {

        public MyRectangle(Point pnt1, Point pnt2, boolean isFilled, Paint paint, BasicStroke stroke) 
        {
            super(pnt1, pnt2, isFilled, paint, stroke);
        }

        @Override
        public void draw(Graphics2D g) 
        {
            g.setPaint(getPaint());
            g.setStroke(getStroke());
            int[] drawParam = getDrawParameters();
            if (isFilled()) 
            {
                g.fillRect(drawParam[0], drawParam[1], drawParam[2], drawParam[3]);
            } 
            else 
            {
                g.drawRect(drawParam[0], drawParam[1], drawParam[2], drawParam[3]);
            }
        }
    }

}
