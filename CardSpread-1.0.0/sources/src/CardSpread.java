// $Id: CardSpread.java 1242 2008-11-01 23:27:04Z wlt $

// XXX

package wltruppel.java.app;

import wltruppel.java.util.ApplicationAppletWrapper;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 <b>
 <pre>
    &copy; 2007 Wagner Truppel (wagner@restlessbrain.com)

    This work is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

    This work is free software; you may redistribute it
    and/or modify it under the terms of the Creative Commons
    Attribution License.

    You should have received a copy of the Creative Commons
    Attribution License along with this work; if not, write to:

        Creative Commons
        559 Nathan Abbott Way
        Stanford, CA 94305
        USA.

    You may also read the Creative Commons Attribution License
    <a href="http://creativecommons.org/licenses/by/3.0/">here</a>.

    When crediting me (Wagner Truppel) for this work, please use one
    of the following two suggested formats:

    Uses "CardSpread" code by Wagner Truppel
    http://www.restlessbrain.com/wagner/

    or

    CardSpread code by Wagner Truppel
    http://www.restlessbrain.com/wagner/

    Where possible, a hyperlink to http://www.restlessbrain.com/wagner/
    would be appreciated.
 </pre>
 </b>

    @author   Wagner Truppel (wagner@restlessbrain.com)
    @version  (1.0.0) $Id: CardSpread.java 1242 2008-11-01 23:27:04Z wlt $
    @since    JDK1.2
 */

public final
class CardSpread
extends ApplicationAppletWrapper
{
    private static final int W = 63;
    private static final int H = 85;

    private static final int MAX_EXTENSION_EW = 190;
    private static final int MAX_EXTENSION_N  = 175;
    private static final int MAX_EXTENSION_S  = 355;

    /* === */

    private static final int EAST_WEST = 0;
    private static final int     NORTH = 1;
    private static final int     SOUTH = 2;

    /* === */

    private static final int[] maxExtensions = new int[3];
    private static final String[] titles = new String[3];

    private final JSpinner[] prefOverlapPercSpinners =
        new JSpinner[3];

    private final JSpinner[] maxOverlapPercSpinners =
        new JSpinner[3];

    private final JLabel[] numCardsLabels =
        new JLabel[3];

    /* === */

    private final RenderPanel[] renderPanels =
        new RenderPanel[3];

    /* === */

    private final JSpinner numCardsSpinner =
        new JSpinner(new SpinnerNumberModel(1, 1, 25, 1));

    /* === */

    private final JButton randomizerBtn = new JButton("Randomize Cards");

    /* ========== */

    public static void
    main(String[] args)
    { (new CardSpread()).initApplication(); }

    /**
        This method is automatically called immediately before setting up
        the application/applet user interface. The default implementation
        does nothing. Applies to both applications and applets.
     */
    protected void
    preUIinit()
    {
        maxExtensions[EAST_WEST] = MAX_EXTENSION_EW;
        maxExtensions[NORTH]     = MAX_EXTENSION_N;
        maxExtensions[SOUTH]     = MAX_EXTENSION_S;

        for (int i = 0; i < 3; ++i)
        {
            numCardsLabels[i] = new JLabel(" ");

            prefOverlapPercSpinners[i] =
                new JSpinner(new SpinnerNumberModel(40, 1, 99, 1));

            maxOverlapPercSpinners[i] =
                new JSpinner(new SpinnerNumberModel(75, 1, 99, 1));

            renderPanels[i] =
                new RenderPanel(numCardsLabels[i], W, H,
                                maxExtensions[i], i == 2,
                                isRunningAsApplet());

            final int k = i;

            prefOverlapPercSpinners[i].addChangeListener(
                new ChangeListener()
                {
                    public final void
                    stateChanged(ChangeEvent evt)
                    {
                        JSpinner source = prefOverlapPercSpinners[k];
                        JSpinner target =  maxOverlapPercSpinners[k];

                        int po = ((Integer) source.getValue()).intValue();
                        int mo = ((Integer) target.getValue()).intValue();

                        if (po > mo)
                        { source.setValue(mo); }

                        int nc = ((Integer)
                            numCardsSpinner.getValue()).intValue();

                        renderPanels[k].updateState(po, mo, nc);
                    }
                });

             maxOverlapPercSpinners[i].addChangeListener(
                new ChangeListener()
                {
                    public final void
                    stateChanged(ChangeEvent evt)
                    {
                        JSpinner source =  maxOverlapPercSpinners[k];
                        JSpinner target = prefOverlapPercSpinners[k];

                        int mo = ((Integer) source.getValue()).intValue();
                        int po = ((Integer) target.getValue()).intValue();

                        if (mo < po)
                        { source.setValue(po); }

                        int nc = ((Integer)
                            numCardsSpinner.getValue()).intValue();

                        renderPanels[k].updateState(po, mo, nc);
                    }
                });
        }

        if (isRunningAsApplet())
        { randomizerBtn.setEnabled(false); }
        else
        {
            randomizerBtn.addActionListener(
                new ActionListener()
                {
                    public final void
                    actionPerformed(ActionEvent evt)
                    { renderPanels[2].randomizeCards(); }
                });
        }

        numCardsSpinner.addChangeListener(
                new ChangeListener()
                {
                    public final void
                    stateChanged(ChangeEvent evt)
                    {
                        for (int k = 0; k < 3; ++k)
                        {
                            JSpinner source =  maxOverlapPercSpinners[k];
                            JSpinner target = prefOverlapPercSpinners[k];

                            int mo = ((Integer) source.getValue()).intValue();
                            int po = ((Integer) target.getValue()).intValue();

                            int nc = ((Integer)
                                numCardsSpinner.getValue()).intValue();

                            renderPanels[k].updateState(po, mo, nc);
                        }
                    }
                });
    }

    // ============== SUPERCLASS METHODS ============== //

    /**
        Should return the application title to be used in the
        application frame's titlebar. Applies to applications only.
     */
    protected final String
    getApplicationTitle()
    { return "CardSpread"; }

    /**
        Should return <code>true</code> if the application frame is
        to have a menu allowing the user to dynamically change the
        application's look-and-feel. The default implementation
        returns <code>false</code>.
     */
    protected final boolean
    applicationFrameHasLAFmenu()
    { return true; }

    /**
        Should return the <code>Container</code> holding the
        application/applet's user interface. Applies to both
        applications and applets.
     */
    protected final Container
    buildUI()
    {
        Dimension hdim = new Dimension(10, 0);
        Dimension vdim = new Dimension(0, 10);

        titles[0] = "Pref Overlap %";
        titles[1] = "Max  Overlap %";
        titles[2] = "Number of Cards";

        JPanel[] ps1 = new JPanel[7];

        for (int i = 0; i < 6; i += 2)
        {
            ps1[i  ] = prepSpinner(titles[0], prefOverlapPercSpinners[i/2]);
            ps1[i+1] = prepSpinner(titles[1],  maxOverlapPercSpinners[i/2]);
        }

        ps1[6] = prepSpinner(titles[2], numCardsSpinner);

        titles[EAST_WEST] = " East/West ";
        titles[NORTH]     = " North ";
        titles[SOUTH]     = " South ";

        JPanel[] ps2 = new JPanel[3];

        for (int i = 0; i < 3; ++i)
        {
            ps2[i] = stackSpinners(ps1[2*i], ps1[2*i + 1], numCardsLabels[i]);

            JPanel p1 = new JPanel();
            p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
            p1.setAlignmentX(Component.CENTER_ALIGNMENT);
            p1.setAlignmentY(Component.CENTER_ALIGNMENT);

            p1.add(Box.createRigidArea(hdim));
            p1.add(Box.createRigidArea(hdim));
            p1.add(renderPanels[i]);
            p1.add(Box.createRigidArea(hdim));
            p1.add(Box.createRigidArea(hdim));
            p1.add(ps2[i]);
            p1.add(Box.createRigidArea(hdim));
            p1.add(Box.createRigidArea(hdim));

            JPanel p2 = new JPanel();
            p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
            p2.setAlignmentX(Component.CENTER_ALIGNMENT);
            p2.setAlignmentY(Component.CENTER_ALIGNMENT);

            p2.add(Box.createRigidArea(vdim));
            p2.add(p1);
            p2.add(Box.createRigidArea(vdim));
            p2.add(Box.createRigidArea(vdim));

            p2.setBorder(BorderFactory.createTitledBorder(titles[i]));

            Dimension d = p2.getPreferredSize();
            d.width += (2*hdim.width);
            p2.setMinimumSize(d);
            p2.setMaximumSize(d);
            p2.setPreferredSize(d);

            ps2[i] = p2;
        }

        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
        p3.setAlignmentX(Component.CENTER_ALIGNMENT);
        p3.setAlignmentY(Component.CENTER_ALIGNMENT);

        p3.add(Box.createRigidArea(vdim));
        for (int i = 0; i < 3; ++i)
        {
            p3.add(ps2[i]);
            p3.add(Box.createRigidArea(vdim));
        }

        JPanel p4 = new JPanel();
        p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
        p4.setAlignmentX(Component.CENTER_ALIGNMENT);
        p4.setAlignmentY(Component.CENTER_ALIGNMENT);
        p4.add(Box.createHorizontalGlue());
        p4.add(ps1[6]);
        p4.add(Box.createHorizontalGlue());
        p4.add(randomizerBtn);
        p4.add(Box.createHorizontalGlue());

        p3.add(p4);
        p3.add(Box.createRigidArea(vdim));
        p3.add(Box.createRigidArea(vdim));

        Dimension d = p3.getPreferredSize();
        d.width = ps2[0].getPreferredSize().width + 2*hdim.width;
        p3.setMinimumSize(d);
        p3.setMaximumSize(d);
        p3.setPreferredSize(d);

        numCardsSpinner.setValue(20);
        numCardsSpinner.setValue(1);

        // may be useful to know, sometimes...
        System.out.println(p3.getPreferredSize());

        return p3;
    }

    private JPanel
    prepSpinner(String name, JSpinner spinner)
    {
        JLabel label = new JLabel(name);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        spinner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel p = new JPanel();

        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.setAlignmentY(Component.CENTER_ALIGNMENT);

        p.add(label);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(spinner);

        Dimension d = label.getPreferredSize();
        int w = d.width;

        d = p.getPreferredSize();
        d.width = w;

        p.setMinimumSize(d);
        p.setMaximumSize(d);
        p.setPreferredSize(d);

        return p;
    }

    private JPanel
    stackSpinners(JPanel p1, JPanel p2, JLabel label)
    {
        Dimension vdim = new Dimension(0, 10);

        JPanel p = new JPanel();

        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.setAlignmentY(Component.CENTER_ALIGNMENT);

        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(p1);
        p.add(Box.createVerticalGlue());
        p.add(Box.createRigidArea(vdim));
        p.add(p2);
        p.add(Box.createVerticalGlue());
        p.add(Box.createRigidArea(vdim));
        p.add(label);
        p.add(Box.createVerticalGlue());

        Dimension d = p.getPreferredSize();
        d.width += 20;
        p.setMinimumSize(d);
        p.setMaximumSize(d);
        p.setPreferredSize(d);

        return p;
    }
}
