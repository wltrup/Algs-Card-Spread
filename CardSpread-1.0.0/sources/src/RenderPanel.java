// $Id: RenderPanel.java 1242 2008-11-01 23:27:04Z wlt $

// XXX

package wltruppel.java.app;

import wltruppel.java.util.ApplicationAppletWrapper;

import java.awt.*;
import javax.swing.*;

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
    @version  (1.0.0) $Id: RenderPanel.java 1242 2008-11-01 23:27:04Z wlt $
    @since    JDK1.2
 */

public final
class RenderPanel
extends JPanel
{
    private static final Dimension DIM = new Dimension(400, 150);
    private static final java.util.Random RNG = new java.util.Random();

    private Image cardback;

    private static final Image[] cards  = new Image[52];
    private static final   int[] cardinds = new int[52];

    private final JLabel label;

    private final int W;
    private final int H;
    private final int maxExt;
    private final boolean revealed;

    private final boolean isApplet;

    private int pOperc;
    private int mOperc;
    private int ncds;

    private int ext100;
    private int oPerc;
    private int ncdsrendered;

    public
    RenderPanel(JLabel label, int w, int h, int maxExt,
                boolean revealed, boolean isApplet)
    {
        this.label = label;

        this.W = w;
        this.H = h;

        this.maxExt = maxExt;
        this.revealed = revealed;

        this.isApplet = isApplet;

        cardback = null;

        if (! isApplet)
        {
            try
            {
                java.io.File file = new java.io.File("cards/cardback.bmp");
                cardback = javax.imageio.ImageIO.read(file);

                for (int i = 0; i < cards.length; ++i)
                {
                    file = new java.io.File("cards/card-" + (i+1) + ".bmp");
                    cards[i] = javax.imageio.ImageIO.read(file);
                }
            }
            catch (Exception exc)
            { exc.printStackTrace(); }
        }
    }

    public final void
    paintComponent(Graphics gr)
    {
        super.paintComponent(gr);

        int w = getWidth();
        int h = getHeight();

        computeParams();
        renderCards(gr);

        gr.setColor(Color.blue);
        gr.drawRect((w - maxExt)/2,
                    (h - H)/2, maxExt, H);

        gr.drawRect((w - maxExt + 2)/2,
                    (h - H + 2)/2, maxExt - 2, H - 2);
    }

    public final Dimension
    getMinimumSize()
    { return DIM; }

    public final Dimension
    getMaximumSize()
    { return DIM; }

    public final Dimension
    getPreferredSize()
    { return DIM; }

    public final void
    updateState(int pOperc, int mOperc, int ncds)
    {
        this.pOperc = pOperc;
        this.mOperc  = mOperc;
        this.ncds  = ncds;

        repaint();
    }

    public final void
    computeParams()
    {
        ncdsrendered = ncds;
        oPerc = pOperc;

        ext100 = (ncdsrendered * (100 - oPerc) + oPerc) * W;

        if (ext100 > 100 * maxExt)
        {
            /* We can't render all cards with the preferred
             * overlap percentage. */

            /* Required overlap percentage to render all cards */

            oPerc = (100 * (ncdsrendered * W - maxExt)) /
                    (W * (ncdsrendered - 1));

            if (oPerc > mOperc)
            {
                /* Too much overlap. */

                /* Compute how many cards we can render with
                 * an overlap percentage of mOperc */

                oPerc = mOperc;

                ncdsrendered = (100 * maxExt - mOperc * W ) /
                               ((100 - mOperc) * W);

                if (!(ncdsrendered > 0))
                {
                    System.out.println("Give up!");
                }

                /* We're done. We can render ncdsrendered
                 * cards with oPerc = mOperc */
            }

        }

        /* Final value of the extension. */

        ext100 = (ncdsrendered * (100 - oPerc) + oPerc) * W;

        if (ncdsrendered == 1)
        {
            label.setText("1 card rendered");
        }
        else
        {
            label.setText("" + ncdsrendered + " cards rendered");
        }

        /* The function returns ncdsrendered, oPerc,
         * and ext100. */
    }

    public final void
    randomizeCards()
    {
        for (int i = 0; i < cards.length; ++i)
        { cardinds[i] = RNG.nextInt(cards.length); }

        repaint();
    }

    public final void
    renderCards(Graphics gr)
    {
        int deltat100 = W * (100 - oPerc);
        int centert200 = 100 * (W + DIM.width) - ext100;

        for (int i = 0; i < ncdsrendered; ++i)
        {
            if (isApplet)
            {
                gr.setColor(Color.red);
                gr.fillRect((centert200  - 100 * W) / 200,
                            (DIM.height - H) / 2, W, H);

                gr.setColor(Color.black);
                gr.drawRect((centert200 - 100 * W + 200) / 200,
                            (DIM.height - H + 2)/2, W - 2, H - 2);
                gr.drawRect((centert200 - 100 * W + 400) / 200,
                            (DIM.height - H + 4)/2, W - 4, H - 4);

                gr.setColor(Color.white);
                gr.drawRect((centert200 - 100 * W + 600) / 200,
                            (DIM.height - H + 6)/2, W - 6, H - 6);
                gr.drawRect((centert200 - 100 * W + 800) / 200,
                            (DIM.height - H + 8)/2, W - 8, H - 8);
            }
            else
            {
                if (revealed)
                {
                    gr.drawImage(cards[cardinds[i]],
                                 (centert200  - 100 * W) / 200,
                                 (DIM.height - H) / 2, null);
                }
                else
                {
                    gr.drawImage(cardback,
                                 (centert200  - 100 * W) / 200,
                                 (DIM.height - H) / 2, null);
                }
            }

            centert200 += (2 * deltat100);
        }
    }
}
