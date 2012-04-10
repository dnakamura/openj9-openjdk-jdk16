/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.lwawt.macosx;

import java.awt.*;
import java.awt.peer.*;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.event.*;
import java.awt.image.*;
import java.util.List;
import java.io.*;

import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

class CFileDialog implements FileDialogPeer {

    private class Task implements Runnable {

        @Override
        public void run() {
            try {
                boolean navigateApps = false;
                int dialogMode = target.getMode();

                navigateApps = true;

                String title = target.getTitle();
                if (title == null) {
                    title = " ";
                }

                String userFileName = nativeRunFileDialog(title,
                        dialogMode, navigateApps,
                        target.getFilenameFilter() != null,
                        target.getDirectory(),
                        target.getFile());

                File file = null;
                if (userFileName != null) {
                    // the dialog wasn't cancelled
                    file = new File(userFileName);
                }

                if (file != null) {
                    // make sure directory always ends in '/'
                    String parent = file.getParent();
                    if (!parent.endsWith(File.separator)) {
                        parent = parent + File.separator;
                    }

                    // store results back in component
                    target.setDirectory(parent);
                    target.setFile(file.getName());
                } else {
                    // setting file name to null is how we tell
                    // java client that user hit the cancel button
                    target.setFile(null);
                }
            } finally {
                // Java2 Dialog waits for hide to let show() return
                target.dispose();
            }
        }
    }

    // The target FileDialog
    private final FileDialog target;

    CFileDialog(FileDialog target) {
        this.target = target;
    }

    @Override
    public void dispose() {
        LWCToolkit.targetDisposedPeer(target, this);
        // Unlike other peers, we do not have a native model pointer to
        // dispose of because the save and open panels are never released by
        // an application.
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            // Java2 Dialog class requires peer to run code in a separate thread
            // and handles keeping the call modal
            new Thread(new Task()).start(); // invokes my 'run' method, below...
        }
        // We hide ourself before "show" returns - setVisible(false)
        // doesn't apply
    }

    /**
     * A callback method.
     * If the file dialog has a file filter, ask it if inFilename is acceptable.
     * If the dialog doesn't have a file filter return true.
     */
    private boolean queryFilenameFilter(final String inFilename) {
        boolean ret = false;

        final FilenameFilter ff = target.getFilenameFilter();
        File fileObj = new File(inFilename);

        // Directories are never filtered by the FileDialog.
        if (!fileObj.isDirectory()) {
            File directoryObj = new File(fileObj.getParent());
            String nameOnly = fileObj.getName();
            ret = ff.accept(directoryObj, nameOnly);
        }
        return ret;
    }

    private native String nativeRunFileDialog(String title, int mode,
            boolean shouldNavigateApps, boolean hasFilenameFilter,
            String directory, String file);

    @Override
    public void setDirectory(String dir) {
    }

    @Override
    public void setFile(String file) {
    }

    @Override
    public void setFilenameFilter(FilenameFilter filter) {
    }

    @Override
    public void blockWindows(List<Window> windows) {
    }

    @Override
    public void setResizable(boolean resizeable) {
    }

    @Override
    public void setTitle(String title) {
    }

    @Override
    public void repositionSecurityWarning() {
    }

    @Override
    public void setAlwaysOnTop(boolean alwaysOnTop) {
    }

    @Override
    public void setModalBlocked(Dialog blocker, boolean blocked) {
    }

    @Override
    public void setOpacity(float opacity) {
    }

    @Override
    public void setOpaque(boolean isOpaque) {
    }

    @Override
    public void toBack() {
    }

    @Override
    public void toFront() {
    }

    @Override
    public void updateFocusableWindowState() {
    }

    @Override
    public void updateIconImages() {
    }

    @Override
    public void updateMinimumSize() {
    }

    @Override
    public void updateWindow() {
    }

    @Override
    public void beginLayout() {
    }

    @Override
    public void beginValidate() {
    }

    @Override
    public void endLayout() {
    }

    @Override
    public void endValidate() {
    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 0, 0, 0);
    }

    @Override
    public void applyShape(Region shape) {
    }

    @Override
    public boolean canDetermineObscurity() {
        return false;
    }

    @Override
    public int checkImage(Image img, int w, int h, ImageObserver o) {
        return 0;
    }

    @Override
    public void coalescePaintEvent(PaintEvent e) {
    }

    @Override
    public void createBuffers(int numBuffers, BufferCapabilities caps)
            throws AWTException {
    }

    @Override
    public Image createImage(ImageProducer producer) {
        return null;
    }

    @Override
    public Image createImage(int width, int height) {
        return null;
    }

    @Override
    public VolatileImage createVolatileImage(int width, int height) {
        return null;
    }

    @Override
    public void destroyBuffers() {
    }

    @Override
    public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {
    }

    @Override
    public Image getBackBuffer() {
        return null;
    }

    @Override
    public ColorModel getColorModel() {
        return null;
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        return null;
    }

    @Override
    public Graphics getGraphics() {
        return null;
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        return null;
    }

    @Override
    public Point getLocationOnScreen() {
        return null;
    }

    @Override
    public Dimension getMinimumSize() {
        return target.getSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    @Override
    public Toolkit getToolkit() {
        return Toolkit.getDefaultToolkit();
    }

    @Override
    public void handleEvent(AWTEvent e) {
    }

    @Override
    public boolean handlesWheelScrolling() {
        return false;
    }

    @Override
    public boolean isFocusable() {
        return false;
    }

    @Override
    public boolean isObscured() {
        return false;
    }

    @Override
    public boolean isReparentSupported() {
        return false;
    }

    @Override
    public void layout() {
    }

    @Override
    public void paint(Graphics g) {
    }

    @Override
    public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
        return false;
    }

    @Override
    public void print(Graphics g) {
    }

    @Override
    public void reparent(ContainerPeer newContainer) {
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary,
            boolean focusedWindowChangeAllowed, long time, Cause cause) {
        return false;
    }

    @Override
    public void setBackground(Color c) {
    }

    @Override
    public void setForeground(Color c) {
    }

    @Override
    public void setBounds(int x, int y, int width, int height, int op) {
    }

    @Override
    public void setEnabled(boolean e) {
    }

    @Override
    public void setFont(Font f) {
    }

    @Override
    public void setZOrder(ComponentPeer above) {
    }

    @Override
    public void updateCursorImmediately() {
    }

    @Override
    public boolean updateGraphicsData(GraphicsConfiguration gc) {
        return false;
    }
}
