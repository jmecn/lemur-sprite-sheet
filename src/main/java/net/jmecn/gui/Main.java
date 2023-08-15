package net.jmecn.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import net.jmecn.gui.state.GuiState;

/**
 * desc:
 *
 * @author yanmaoyuan
 * @date 2023/8/15
 */
public class Main {

    public static void main(String[] args) {
        AppSettings setting = new AppSettings(true);
        setting.setTitle("Lemur Sprite Sheet");
        setting.setResolution(1280, 720);
        setting.setResizable(true);
        setting.setFrameRate(60);
        setting.setSamples(4);
        setting.setGammaCorrection(false);
        setting.setRenderer(AppSettings.LWJGL_OPENGL33);
        setting.setUseRetinaFrameBuffer(false);

        SimpleApplication app = new SimpleApplication(new GuiState()) {
            @Override
            public void simpleInitApp() {
            }
        };
        app.setShowSettings(false);
        app.setSettings(setting);
        app.start();
    }
}
