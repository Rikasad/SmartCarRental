import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class Theme {
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color DARK    = new Color(17, 24, 39);
    public static final Color BG      = new Color(243, 244, 246);
    public static final Color DANGER  = new Color(220, 38, 38);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Font BASE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // Call once, before creating any window
    public static void init() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");   // used if the jar is present
        } catch (Exception e) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
        }
        UIManager.put("defaultFont", BASE);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
    }

    // Dark banner at the top of a window
    public static JPanel header(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(DARK);
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 22));
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(BASE);
        s.setForeground(new Color(156, 163, 175));
        p.add(t, BorderLayout.CENTER);
        p.add(s, BorderLayout.SOUTH);
        return p;
    }

    // Rounded coloured button
    public static JButton button(String text) { return button(text, PRIMARY); }

    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? bg.darker()
                        : getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(BOLD);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Taller rows, no grid lines, striped rows, dark header
    public static void styleTable(JTable t) {
        t.setRowHeight(28);
        t.setFont(BASE);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(new Color(219, 234, 254));
        t.setSelectionForeground(DARK);

        JTableHeader h = t.getTableHeader();
        h.setFont(BOLD);
        h.setBackground(DARK);
        h.setForeground(Color.WHITE);
        h.setPreferredSize(new Dimension(0, 32));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tb, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(tb, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                return comp;
            }
        });
    }

    // Colour the Status column: OVERDUE red, Active green, Returned grey
    public static void colorStatus(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tb, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(tb, v, sel, foc, r, c);
                String s = String.valueOf(v);
                setHorizontalAlignment(CENTER);
                comp.setFont(BOLD);
                if (!sel) comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                comp.setForeground(s.equals("OVERDUE") ? DANGER
                                 : s.equals("Active") ? SUCCESS : Color.GRAY);
                return comp;
            }
        });
    }
}