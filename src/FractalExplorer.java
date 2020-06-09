import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.filechooser.*;
import javax.imageio.ImageIO;
public class FractalExplorer
{
    private int m_DisplaySize;
    private FractalGenerator m_Gener;
    private Rectangle2D.Double  m_Range;
    private JImageDisplay m_Display;
    private JButton m_ResetButt;
    private JComboBox m_Switch;
    private JButton m_SaveButt;
    private JFrame m_Frm;
    private int m_RowsRemaining;
    private class actionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            if (actionEvent.getSource() == m_Switch)
            { m_Gener = (FractalGenerator) m_Switch.getSelectedItem();
                m_Gener.getInitialRange(m_Range);
                drawFractal(); }
            else if (actionEvent.getSource() == m_ResetButt)
            { m_Gener.getInitialRange(m_Range);
                drawFractal(); }
            else if (actionEvent.getSource() == m_SaveButt)
            { JFileChooser chooser = new JFileChooser();
                FileFilter filter = new FileNameExtensionFilter("PNG pictures", "png");
                chooser.setFileFilter(filter);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    try
                    { ImageIO.write(m_Display.picture, "png", chooser.getSelectedFile()); }
                    catch (IOException e)
                    { JOptionPane.showMessageDialog(m_Frm, e.getMessage(), "Cannot Save picture",
                                JOptionPane.ERROR_MESSAGE); }
                }
            }
        }
    }
    private class MouseListener extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (m_RowsRemaining != 0) return;
            int x = e.getX();
            int y = e.getY();

            double xCoord = m_Gener.getCoord(m_Range.x, m_Range.x + m_Range.width, m_DisplaySize,x);
            double yCoord = m_Gener.getCoord(m_Range.y, m_Range.y + m_Range.height, m_DisplaySize,y);
            m_Gener.recenterAndZoomRange(m_Range, xCoord, yCoord, 0.5);
            drawFractal();
        }
    }
    public FractalExplorer(int ScreenSize)
    { m_DisplaySize = ScreenSize;
        m_Range = new Rectangle2D.Double();
        m_Gener = new Mandelbrot();
        m_Gener.getInitialRange(m_Range); }
    private class FractalWorker extends SwingWorker<Object, Object>
    {
        private int m_Ycoord;
        private int[] m_Xcoords;
        private FractalWorker(int yCoord)
        { m_Ycoord = yCoord; }
        @Override
        protected Object doInBackground() throws Exception
        { m_Xcoords = new int[m_DisplaySize];
            double yCoord = FractalGenerator.getCoord
                    (m_Range.y, m_Range.y + m_Range.height, m_DisplaySize, m_Ycoord);
            for (int x = 0; x < m_DisplaySize; x++)
            {
                double xCoord = FractalGenerator.getCoord
                        (m_Range.x, m_Range.x + m_Range.width, m_DisplaySize, x);
                int IterNum = m_Gener.numIterations(xCoord, yCoord);
                if (IterNum == -1) m_Xcoords[x] = 0;
                else
                    {
                    float hue = 0.7f + (float) IterNum / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    m_Xcoords[x] = rgbColor;
                }
            }
            return null;
        }
        @Override
        protected void done()
        {
            for (int x = 0; x <m_DisplaySize; x++)
            {
                m_Display.drawPixel(x, m_Ycoord, m_Xcoords[x]);
            }
            m_Display.repaint(0, m_Ycoord, m_DisplaySize, 1);
            m_RowsRemaining--;
            if (m_RowsRemaining == 0)
            {
                enableUI(true);
            }
        }
    }
    public void createAndShowGUI()
    {
        JPanel panel = new JPanel();
        m_Switch = new JComboBox();
        m_Switch.addItem(new Mandelbrot());
        m_Switch.addItem(new Tricorn());
        m_Switch.addItem(new BurningShip());
        m_Switch.addActionListener(new actionListener());
        JLabel label = new JLabel("Fractal type:");
        panel.add(label);
        panel.add(m_Switch);
        m_Display = new JImageDisplay(m_DisplaySize, m_DisplaySize);
        m_Display.addMouseListener(new MouseListener());
        m_ResetButt = new JButton("Reset picture");
        m_ResetButt.addActionListener(new actionListener());
        m_SaveButt = new JButton("Save picture");
        m_SaveButt.addActionListener(new actionListener());
        JPanel panel2 = new JPanel();
        panel2.add(m_ResetButt);
        panel2.add(m_SaveButt);
        m_Frm = new JFrame();
        m_Frm.getContentPane().add(panel, BorderLayout.NORTH);
        m_Frm.getContentPane().add(m_Display, BorderLayout.CENTER);
        m_Frm.getContentPane().add(panel2, BorderLayout.SOUTH);
        m_Frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        m_Frm.pack();
        m_Frm.setVisible(true);
        m_Frm.setResizable(true);
    }
    void enableUI(boolean val)
    {
        m_ResetButt.setEnabled(val);
        m_SaveButt.setEnabled(val);
        m_Switch.setEnabled(val);
    }
    private void drawFractal()
    {
        enableUI(false);
        m_RowsRemaining = m_DisplaySize;
        for (int y = 0; y < m_DisplaySize; y++)
        {
            FractalWorker worker = new FractalWorker(y);
            worker.execute();
        }
    }
    public static void main(String args[])
    {
        FractalExplorer explorer = new FractalExplorer(1000);
        explorer.createAndShowGUI();
        explorer.drawFractal();
    }
}
